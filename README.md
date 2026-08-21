# AutoVistor

Sistema de gestão de vistorias veiculares, com arquitetura em três camadas: **backend** (API REST), **desktop** (aplicação para Vistoriadores e Gerentes) e **frontend web** (planejado, para Clientes).

O projeto cobre todo o ciclo de uma vistoria — cadastro de cliente e veículo, agendamento, execução (com checklist detalhado e fotos), pagamento, emissão de laudo em PDF e relatórios gerenciais — com autenticação, autorização por perfil e um módulo financeiro completo (boleto, nota fiscal e controle de caixa).

Este projeto nasceu da análise de um sistema anterior do mesmo domínio (DF-Vistoria), incorporando correções de segurança, bugs de negócio e lacunas de requisitos identificadas naquela revisão — o histórico completo dessas decisões está documentado em [`05-Registro-de-Alteracoes.md`](05-Registro-de-Alteracoes.md).

---

## Estrutura do repositório

```
.
├── autovistorbackend/        # API REST (Spring Boot)
├── autovistor-desktop/       # Aplicação desktop (JavaFX) — Vistoriador e Gerente
├── Banco de dados/           # Scripts e modelagem do MySQL
├── 01-Documento-de-Requisitos.md
├── 02-Modelagem-Banco-de-Dados.md
├── 03-Arquitetura-Tecnica.md
├── 04-Manual-do-Usuario.md
├── 05-Registro-de-Alteracoes.md
├── Roteiro_Postman_AutoVistor.md
└── LICENSE
```

## Stack técnica

| Camada | Tecnologia |
|---|---|
| Backend | Java 21 · Spring Boot 4.1.0 · Spring Security (JWT) · Spring Data JPA · MySQL 8 · Flyway |
| Desktop | Java 21 · JavaFX 21 · HttpClient nativo (Java) |
| Documentação de PDF | Apache PDFBox |
| Testes | JUnit 5 · Mockito · MockMvc |
| Frontend web | React *(planejado)* |

## Documentação

| Documento | Conteúdo |
|---|---|
| [01 — Documento de Requisitos](01-Documento-de-Requisitos.md) | Requisitos funcionais e não funcionais |
| [02 — Modelagem de Banco de Dados](02-Modelagem-Banco-de-Dados.md) | Schema SQL e diagrama de entidades |
| [03 — Arquitetura Técnica](03-Arquitetura-Tecnica.md) | Camadas do backend/desktop, endpoints, fluxos |
| [04 — Manual do Usuário](04-Manual-do-Usuario.md) | Guia de uso por perfil (Cliente, Vistoriador, Gerente) |
| [05 — Registro de Alterações](05-Registro-de-Alteracoes.md) | O que mudou desde o planejamento inicial, e por quê |
| [Roteiro Postman](Roteiro_Postman_AutoVistor.md) | Sequência completa de testes manuais da API |

> Os documentos 01–03 registram o planejamento inicial e permanecem como estavam. Para saber o que foi de fato implementado — e onde isso diverge do planejamento original — comece pelo **05**.

## Como rodar

### Backend
```bash
cd autovistorbackend
# configure as variáveis de ambiente DB_PASSWORD e JWT_SECRET
./mvnw spring-boot:run
```
Instruções completas de setup (banco, variáveis de ambiente, autenticação, endpoints) estão no [`README`](autovistorbackend/README.md) da pasta do backend.

### Desktop
```bash
cd autovistor-desktop
mvn javafx:run
```
Requer o backend rodando em `http://localhost:8080`.

## Perfis de acesso

| Perfil | Acesso |
|---|---|
| **Cliente** | Cadastro de veículos, agendamento de vistoria, pagamento, acompanhamento de laudos *(via web, planejado)* |
| **Vistoriador** | Execução de vistorias designadas (checklist, fotos, resultado), emissão de laudo *(via desktop)* |
| **Gerente** | Gestão de funcionários e clientes, designação de vistoriador, financeiro, relatórios *(via desktop)* |

## Licença
Ver [`LICENSE`](LICENSE).
