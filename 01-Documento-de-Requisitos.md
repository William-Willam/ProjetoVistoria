# Documento de Requisitos — AutoVistor
### Sistema de Vistoria Veicular — v1.0

---

## 1. Introdução

O AutoVistor é um sistema de gestão de vistorias veiculares, composto por três aplicações que compartilham um único backend: uma **API** (regras de negócio e persistência), um **cliente web** (para clientes finais) e um **cliente desktop** (para vistoriadores e gerentes). O objetivo é cobrir todo o ciclo de uma vistoria — cadastro, agendamento, execução, pagamento e emissão de laudo — com controle de acesso por perfil.

Este documento nasce da análise de um projeto anterior do mesmo domínio (DF-Vistoria), incorporando as correções e lacunas identificadas naquela revisão.

## 2. Atores

| Ator | Descrição |
|---|---|
| **Cliente** | Cadastra veículos, agenda vistorias, acompanha status, realiza pagamento e acessa laudos. Usa o cliente web. |
| **Vistoriador** | Executa vistorias designadas, registra resultado e observações, emite laudo. Usa o cliente desktop. |
| **Gerente** | Administra funcionários e clientes, acompanha financeiro, gera relatórios. Usa o cliente desktop. |

## 3. Requisitos Funcionais

| ID | Requisito | Ator(es) | Prioridade |
|---|---|---|---|
| RF001 | Cadastrar cliente (autocadastro pelo próprio cliente, ou cadastro manual pelo Gerente) | Cliente, Gerente | Essencial |
| RF002 | Cadastrar veículo, vinculado a um cliente | Cliente, Gerente | Essencial |
| RF003 | Criar, editar, cancelar e **reagendar** agendamento de vistoria | Cliente, Gerente | Essencial |
| RF004 | Impedir agendamento em horário/data já ocupado para o mesmo vistoriador | Sistema | Essencial |
| RF005 | Designar vistoriador a um agendamento | Gerente | Importante |
| RF006 | Registrar execução de vistoria (resultado, observações) | Vistoriador | Essencial |
| RF007 | Emitir laudo técnico em PDF a partir de uma vistoria concluída | Vistoriador | Essencial |
| RF008 | Registrar pagamento (Pix, Débito, Crédito, Boleto, Dinheiro) | Cliente, Vistoriador | Essencial |
| RF009 | Emitir boleto bancário para pagamento pendente | Sistema | Importante |
| RF010 | Emitir nota fiscal após confirmação de pagamento | Sistema | Importante |
| RF011 | Registrar lançamento em caixa (entrada/saída) a partir de um pagamento confirmado | Sistema | Importante |
| RF012 | Autenticar usuário (Cliente, Vistoriador, Gerente) por login e senha, com token de sessão | Todos | Essencial |
| RF013 | Cadastrar, editar e desligar funcionário, preservando histórico de desligamento | Gerente | Essencial |
| RF014 | Emitir relatórios operacionais (agendamentos, vistorias por status) | Gerente | Importante |
| RF015 | Emitir relatórios financeiros (receita por período, por forma de pagamento) | Gerente | Importante |
| RF016 | Consultar histórico de laudos e pagamentos | Cliente | Essencial |

> RF003, RF004, RF009–RF011 e RF013 (preservação de histórico) corrigem lacunas encontradas na análise do DF-Vistoria: lá, não havia edição/cancelamento de agendamento, não havia checagem de conflito de horário, não existia boleto/nota fiscal/caixa, e o histórico de desligamento era apagado por um `ON DELETE CASCADE` mal configurado.

## 4. Requisitos Não Funcionais

| ID | Requisito | Prioridade |
|---|---|---|
| RNF001 | Senhas armazenadas com hash (BCrypt), nunca em texto puro | Essencial |
| RNF002 | Credenciais de banco de dados fora do código-fonte (variáveis de ambiente) | Essencial |
| RNF003 | Autenticação via JWT, com expiração de token | Essencial |
| RNF004 | Controle de acesso por perfil (RBAC) em todos os endpoints da API | Essencial |
| RNF005 | API deve responder em até 1s para operações de consulta simples | Importante |
| RNF006 | Integridade referencial e transacional garantida no banco (MySQL/InnoDB) | Essencial |
| RNF007 | Interface desktop responsiva e consistente entre telas (JavaFX + CSS) | Importante |
| RNF008 | Compatibilidade com Java 21 (LTS) | Essencial |
| RNF009 | Migrações de banco versionadas (Flyway) | Importante |
| RNF010 | Documentação técnica e manual do usuário disponíveis e atualizados | Desejável |

## 5. Fora de escopo (v1.0)
- Notificações push/e-mail automáticas.
- Aplicativo mobile nativo.
- Integração com gateway de pagamento real (a v1 simula a confirmação de pagamento).
