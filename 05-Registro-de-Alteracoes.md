# Registro de Alterações — AutoVistor
### Mudanças identificadas após o início da implementação (pós v1.0)

Este documento **não substitui** `01-Documento-de-Requisitos.md`, `02-Modelagem-Banco-de-Dados.md` e `03-Arquitetura-Tecnica.md` — ele registra o que mudou em relação a eles depois que a implementação começou, e por quê. Serve como histórico de decisões, útil para justificar divergências entre o que foi planejado e o que foi entregue.

---

## 1. Autenticação — login por e-mail (não CPF/matrícula)

**Documento afetado:** `01-Documento-de-Requisitos.md` (RF012), `03-Arquitetura-Tecnica.md` (seção Segurança)

**Original:** não havia decisão explícita registrada; o fluxograma do projeto anterior (DF-Vistoria) sugeria CPF para Cliente e matrícula para Funcionário.

**Decisão tomada:** login unificado por **e-mail + senha** para todos os perfis (Cliente, Vistoriador, Gerente).

**Motivo:** e-mail já é campo único em ambas as tabelas (`cliente` e `funcionario`), eliminando a ambiguidade de "em qual tabela procurar" que existiria com CPF/matrícula (que são campos diferentes por tabela). Simplifica o `AutenticacaoService` e o formulário de login em todos os clientes.

---

## 2. RF003 — Autorização do Gerente sobre agendamentos

**Documento afetado:** `01-Documento-de-Requisitos.md` (RF003)

**Situação encontrada:** o documento original já previa "Ator: Cliente, Gerente" para RF003, mas a implementação inicial só liberou `criar`, `reagendar` e `cancelar` agendamento para o perfil `CLIENTE` — o Gerente não conseguia gerenciar agendamentos de nenhum cliente.

**Correção aplicada:** `reagendar` e `cancelar` passaram a aceitar `hasAnyRole('CLIENTE', 'GERENTE')`. Foi necessário também corrigir a lógica de busca no `AgendamentoService` (`buscarAgendamentoComPermissao`), que antes buscava sempre pelo par `(idAgendamento, idClienteLogado)` — o que faria qualquer chamada do Gerente retornar `404` mesmo para agendamentos existentes, já que o ID do Gerente nunca bate com `id_cliente`. Agora a busca é condicional pela role: Gerente busca livre por ID; Cliente continua restrito ao próprio agendamento.

**Testado:** Gerente reagenda/cancela agendamento de qualquer cliente; Cliente continua bloqueado de mexer no agendamento de outro cliente.

**Ainda em aberto:** `criar` (cadastrar) agendamento continua restrito só ao Cliente.

---

## 3. RF006 — Checklist de itens e fotos na vistoria

**Documento afetado:** `01-Documento-de-Requisitos.md` (RF006), `02-Modelagem-Banco-de-Dados.md` (tabelas novas)

**Situação encontrada:** a v1.0 definia RF006 como "Registrar execução de vistoria (resultado, observações)" — um resultado categórico mais um campo de texto livre, considerado raso demais para uma vistoria veicular real.

**Decisão tomada:** RF006 ampliado para incluir checklist de itens (lista fixa + itens livres, cada um com situação OK/Avaria e observação) e upload de fotos vinculadas à vistoria.

**Impacto técnico:** tabelas `item_vistoria`/`foto_vistoria` (migração `V3`), entidades `ItemVistoria`/`FotoVistoria` com `@OneToMany` em cascata a partir de `Vistoria`, endpoint multipart de upload, `VistoriaCadastroRequest`/`VistoriaResponse` atualizados.

**Status atual: ✅ Concluído também no desktop** (ver seção 8).

---

## 4. Correção de schema — `veiculo.ano_veiculo` (YEAR → INT)

**Documento afetado:** `02-Modelagem-Banco-de-Dados.md` (tabela `veiculo`)

**Situação encontrada:** a coluna foi criada com o tipo `YEAR` do MySQL, incompatível com o driver JDBC/Hibernate (mapeado como `java.sql.Date`, conflitando com o campo `Integer` da entidade).

**Correção aplicada:** migração `V2__altera_ano_veiculo_para_int.sql`, alterando a coluna para `INT`.

---

## 5. Endpoints reais vs. planejados

**Documento afetado:** `03-Arquitetura-Tecnica.md` (tabela de endpoints)

A tabela de endpoints da v1.0 era um esboço inicial. Principais diferenças: recursos aninhados (`/agendamentos/{id}/vistoria`, `/vistorias/{id}/pagamento`) em vez de recursos soltos; `/agendamentos/disponibilidade` nunca virou endpoint separado; `/laudos/{idVistoria}` virou `/vistorias/{idVistoria}/laudo` e `.../download`. Endpoints novos: `/agendamentos/pendentes`, `/agendamentos/vistoriador/{id}`, `/pagamentos/boletos-pendentes`, `/pagamentos/{id}/confirmar-boleto`, upload/download de fotos, `/clientes/todos`, `PUT /funcionarios/{id}`.

---

## 6. Stack e versões confirmadas

**Documento afetado:** `03-Arquitetura-Tecnica.md`

Spring Boot 4.1.0 (starters renomeados, Flyway exigindo starter dedicado + `flyway-mysql` separado). Java 21, JJWT 0.13.0, Apache PDFBox 3.0.8, JavaFX 21.0.4.

---

## 7. Lacunas conhecidas — situação na v1.0 do registro (RESOLVIDAS nesta rodada, ver seção 8)
- ~~RF008 (pagamento): backend completo, sem tela no desktop~~
- ~~RF001/RF002 (Gerente cadastra cliente/veículo): backend aceita, sem tela~~
- ~~RF013 (editar funcionário): endpoint existe, sem tela~~
- ~~Logout: não existe em nenhum dashboard~~
- ~~Menu do Vistoriador: dashboard sem estrutura de navegação lateral~~
- RNF011 (testes automatizados): **ainda pendente**, próxima etapa do projeto.
- Desktop ainda não publicado no GitHub: **ainda pendente**.

---

## 8. Rodada de correções do desktop (checklist, fotos, pagamento, logout, menu, edição, cadastro pelo Gerente)

**Documentos afetados:** `03-Arquitetura-Tecnica.md` (seção Desktop), `04-Manual-do-Usuario.md` (ainda não revisado — ver nota abaixo)

**Contexto:** após uma primeira análise de progresso, sete lacunas do desktop foram identificadas e corrigidas numa única rodada de trabalho:

1. **Logout** — implementado nos dashboards de Gerente e Vistoriador (`SessaoUsuario.encerrar()` + `NavigationService` de volta ao login).
2. **Menu do Vistoriador** — criado `dashboard_vistoriador.fxml` com barra lateral, espelhando o padrão já usado pelo Gerente (antes ele ia direto para "Vistorias Designadas" sem estrutura de navegação).
3. **Registrar Vistoria expandido (RF006 + RF008 completos no desktop)** — a tela de registrar vistoria deixou de ser um `Dialog` simples e virou uma janela própria (`registrar_vistoria.fxml` + `RegistrarVistoriaController`), com: `TableView` editável de checklist (itens fixos + itens livres, com remoção restrita a itens livres), seletor de múltiplas fotos (`FileChooser`), e campos de pagamento — tudo salvo numa sequência única (vistoria → fotos → pagamento → laudo).
4. **Editar funcionário** — tela de Gestão de Funcionários ganhou botão "Editar", exigiu adicionar o método `put()` ao `ApiClient` (só existiam `get()`/`post()` até então).
5. **Cadastro de cliente/veículo pelo Gerente** — nova tela "Clientes e Veículos", exigiu endpoint novo no backend (`GET /clientes/todos`, antes só existia busca por ID).
6. **Upload multipart no `ApiClient`** — como o `HttpClient` nativo do Java não tem suporte embutido a `multipart/form-data`, foi implementado manualmente (`postMultipart`), montando o corpo da requisição byte a byte com boundary.
7. **Refatoração `DashboardBaseController`** — extraída uma classe base abstrata (`areaConteudo`, `botaoSair`, `carregarTela()`, `fazerLogout()`) para eliminar a duplicação que havia entre `DashboardGerenteController` e `DashboardVistoriadorController`. Atenção: campos `@FXML` funcionam normalmente por herança quando `protected`; o método `initialize()` **não** foi movido para a base (é chamado via reflection pelo `FXMLLoader`, e depender de herança para esse método específico é frágil) — cada subclasse mantém o próprio `initialize()`, chamando `configurarLogout()` da base.

**Bugs encontrados e corrigidos durante essa rodada:**
- `PagamentoService.listarBoletosPendentes()` sem `@Transactional` causava `LazyInitializationException` ao acessar `pagamento.getValor()` fora de sessão Hibernate.
- `GlobalExceptionHandler` sem handler para `BadCredentialsException` fazia login inválido retornar erro genérico ("Erro desconhecido") em vez da mensagem correta.
- Duas ocorrências de **campo `@FXML` órfão**: um botão (`botaoRelatorios`) existia no Controller mas sumiu do FXML numa edição; depois, o inverso — um botão (`botaoClientesVeiculos`) foi adicionado ao FXML do Gerente sem o `setOnAction` correspondente no Controller, e por engano também apareceu referenciado no Controller do Vistoriador sem existir no FXML dele. **Padrão identificado:** esse tipo de erro compila normalmente e só se manifesta em runtime (silenciosamente, ou como `NullPointerException`) — não há checagem do compilador Java para a correspondência `@FXML` ↔ elemento FXML.

**Pendente:** `04-Manual-do-Usuario.md` ainda não foi revisado para descrever essas telas novas (checklist/fotos/pagamento no registro de vistoria, edição de funcionário, cadastro de cliente/veículo pelo Gerente) — o manual atual descreve um fluxo mais simples do que o que existe hoje.

---

## Como usar este documento
Cada vez que uma decisão de implementação divergir do que está registrado em `01`, `02` ou `03`, adicione uma nova seção numerada aqui, no mesmo formato: **Documento afetado**, **Situação encontrada**, **Decisão tomada/Correção aplicada**, e (quando aplicável) **Pendente**. Os documentos originais permanecem como estavam — este arquivo é o histórico do que mudou e por quê.