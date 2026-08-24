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


## fotos
<img width="1365" height="767" alt="Captura de tela 2026-08-24 130518" src="https://github.com/user-attachments/assets/8410d95d-66d0-4988-a063-2ffe5513ef65" />
<img width="1365" height="767" alt="Captura de tela 2026-08-24 130344" src="https://github.com/user-attachments/assets/79e2be92-b338-46a9-9f58-e395a1fa3fbf" />
<img width="1365" height="767" alt="Captura de tela 2026-08-24 130339" src="https://github.com/user-attachments/assets/a883e1ae-2092-4484-b359-16b61ee35979" />
<img width="1365" height="767" alt="Captura de tela 2026-08-24 130325" src="https://github.com/user-attachments/assets/3cd93258-f45d-4ac8-8991-de778e002f3c" />
<img width="1365" height="767" alt="Captura de tela 2026-08-24 130250" src="https://github.com/user-attachments/assets/8b528345-b576-4a09-897a-c6f16c087717" />
<img width="1365" height="767" alt="Captura de tela 2026-08-24 130241" src="https://github.com/user-attachments/assets/24dbe052-8955-422d-960e-369c9ebe90ba" />
<img width="1365" height="767" alt="Captura de tela 2026-08-24 130230" src="https://github.com/user-attachments/assets/388cba0f-9763-4f5e-8a98-a0965946c3ec" />
<img width="1365" height="767" alt="Captura de tela 2026-08-24 130224" src="https://github.com/user-attachments/assets/4049f8dc-8ac7-4e8e-8982-d891ba22f23d" />
<img width="1365" height="767" alt="Captura de tela 2026-08-24 130101" src="https://github.com/user-attachments/assets/3ede91dd-e9fa-40bb-95ba-05f5c3bd058c" />
<img width="1365" height="767" alt="Captura de tela 2026-08-24 125831" src="https://github.com/user-attachments/assets/a0dfb00a-330a-45eb-86c3-d443977946f5" />
<img width="1365" height="767" alt="Captura de tela 2026-08-24 125546" src="https://github.com/user-attachments/assets/d94d843a-3503-451d-9a05-31c9a0983e3c" />
<img width="1365" height="767" alt="Captura de tela 2026-08-24 125514" src="https://github.com/user-attachments/assets/948cc45d-b14f-4fea-a375-53d5aa3f93de" />
<img width="1365" height="767" alt="Captura de tela 2026-08-24 125454" src="https://github.com/user-attachments/assets/b947ab9f-6172-4f73-b387-b91a1b8b1afa" />
<img width="1365" height="766" alt="Captura de tela 2026-08-24 125421" src="https://github.com/user-attachments/assets/9b029a76-f076-445e-95ed-41cd38095c32" />
<img width="1365" height="767" alt="Captura de tela 2026-08-24 125401" src="https://github.com/user-attachments/assets/e1efb474-17ff-467d-9189-5acc22a1da96" />
<img width="1364" height="767" alt="Captura de tela 2026-08-24 125340" src="https://github.com/user-attachments/assets/83162fc5-6304-423b-830e-16b960eb6c90" />
<img width="1365" height="767" alt="Captura de tela 2026-08-24 124938" src="https://github.com/user-attachments/assets/f2ce10cf-726b-4089-8c1b-47a1625ccdf4" />
<img width="1365" height="767" alt="Captura de tela 2026-08-24 124929" src="https://github.com/user-attachments/assets/bf7a16ae-5f59-458b-b10e-435c8184f109" />
<img width="1365" height="767" alt="Captura de tela 2026-08-24 124851" src="https://github.com/user-attachments/assets/598f7046-9aae-449a-abf2-e2a11345d28e" />
<img width="1365" height="767" alt="Captura de tela 2026-08-24 124357" src="https://github.com/user-attachments/assets/de2f3f5d-1cd8-4357-943f-63ef9da00b40" />
<img width="1365" height="767" alt="08-detalhesAgendamento-parte2" src="https://github.com/user-attachments/assets/58c01a18-343a-4718-8e28-f6a61c36084c" />
<img width="1365" height="767" alt="08-detalhesAgedamento" src="https://github.com/user-attachments/assets/94ab74d0-f6e5-46dc-92c4-e7a547f91883" />
<img width="1365" height="767" alt="07-agendamento-parte2" src="https://github.com/user-attachments/assets/468b8feb-c5d2-4170-997b-6216cdc0423b" />
<img width="1365" height="767" alt="07-agendamento" src="https://github.com/user-attachments/assets/aacd0b3f-ddca-4db5-829d-d5b428ddd14f" />
<img width="1365" height="767" alt="06-cadastrarveiculos" src="https://github.com/user-attachments/assets/eb6a64a5-d303-4f50-97b0-458c49fb1154" />
<img width="1365" height="767" alt="05-dashboard-parte2" src="https://github.com/user-attachments/assets/a7c20896-3121-441f-b7bf-fd73fa11de5d" />
<img width="1365" height="767" alt="05-dashboard" src="https://github.com/user-attachments/assets/2753acfc-0343-4936-a8ad-d401d98243fe" />
<img width="1365" height="767" alt="04-acessarconta" src="https://github.com/user-attachments/assets/2ea20dbd-56ff-4be9-8738-b849bc207eb5" />
<img width="1365" height="767" alt="03-criaconta" src="https://github.com/user-attachments/assets/63ca24ca-24c9-405f-97d1-fe8deeed807e" />
<img width="1365" height="767" alt="02-home-parte2" src="https://github.com/user-attachments/assets/a700f7a8-003b-4149-a36e-d85bb9331751" />
<img width="1365" height="767" alt="02-home-parte1" src="https://github.com/user-attachments/assets/6e24bf48-64cc-4fd9-af19-e87d09aad6c4" />
<img width="1365" height="767" alt="01-login" src="https://github.com/user-attachments/assets/5998ebf3-242a-4c84-b7cd-16128701eb26" />


## Licença
Ver [`LICENSE`](LICENSE).
