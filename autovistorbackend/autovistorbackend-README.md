# AutoVistor — Backend

API REST do sistema de vistoria veicular AutoVistor, construída em Spring Boot 4.1.0 (Java 21), com autenticação JWT, autorização por perfil, e um módulo financeiro completo (pagamento, boleto, nota fiscal e caixa).

Este backend corrige e amplia um projeto anterior do mesmo domínio (DF-Vistoria): checagem de conflito de horário em agendamentos, histórico de desligamento de funcionário preservado, senhas com hash, credenciais fora do código-fonte, e as funcionalidades financeiras que antes existiam só em documentação.

## Stack

- Java 21
- Spring Boot 4.1.0 (Web MVC, Data JPA, Security, Validation)
- MySQL 8
- Flyway (migrações versionadas)
- JJWT (autenticação JWT)
- Apache PDFBox (geração de laudo em PDF)
- Lombok

## Pré-requisitos

- JDK 21
- Maven (ou use o wrapper `./mvnw` incluído no projeto)
- MySQL 8 rodando localmente (ou acessível via rede)

## Configuração

### 1. Crie o banco de dados
```sql
CREATE DATABASE autovistor DEFAULT CHARACTER SET utf8mb4;
```
As tabelas são criadas automaticamente pelo Flyway na primeira execução — não é preciso rodar nenhum script manualmente.

### 2. Configure as variáveis de ambiente

| Variável | Obrigatória | Descrição |
|---|---|---|
| `DB_USERNAME` | Não (padrão: `root`) | Usuário do MySQL |
| `DB_PASSWORD` | **Sim** | Senha do MySQL |
| `JWT_SECRET` | **Sim** | Chave secreta para assinatura dos tokens JWT (mínimo 32 caracteres) |
| `LAUDOS_DIR` | Não (padrão: `./laudos`) | Diretório onde os PDFs de laudo são salvos |

A aplicação falha ao iniciar se `DB_PASSWORD` ou `JWT_SECRET` não estiverem definidas — isso é proposital, para nunca rodar com um segredo padrão inseguro.

**No IntelliJ:** configure essas variáveis em Run/Debug Configurations → Environment Variables.

**Via linha de comando:**
```bash
export DB_PASSWORD=sua_senha_aqui
export JWT_SECRET=uma_chave_aleatoria_de_pelo_menos_32_caracteres
./mvnw spring-boot:run
```

## Rodando o projeto

```bash
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`. No primeiro start, o Flyway aplica todas as migrações automaticamente (12 tabelas).

## Autenticação

Login unificado por e-mail (Cliente e Funcionário) e senha:

```
POST /auth/login
{
  "email": "usuario@exemplo.com",
  "senha": "senha123"
}
```

Retorna um token JWT (válido por 1h) — envie em requisições subsequentes no header:
```
Authorization: Bearer {token}
```

## Perfis de acesso

| Perfil | Pode acessar |
|---|---|
| `CLIENTE` | Próprio cadastro, veículos, agendamentos, vistorias, pagamentos, laudos |
| `VISTORIADOR` | Registrar vistoria e laudo dos agendamentos designados a ele |
| `GERENTE` | Gestão de funcionários, designação de vistoriador, confirmação de boleto, relatórios |

## Principais endpoints

| Método | Rota | Descrição |
|---|---|---|
| POST | `/auth/login` | Autenticação |
| POST | `/clientes` | Autocadastro de cliente (público) |
| POST | `/veiculos` | Cadastrar veículo |
| POST | `/agendamentos` | Agendar vistoria |
| POST | `/agendamentos/{id}/designar-vistoriador` | Designar vistoriador (Gerente) |
| POST | `/agendamentos/{id}/reagendar` | Reagendar |
| POST | `/agendamentos/{id}/cancelar` | Cancelar |
| POST | `/agendamentos/{id}/vistoria` | Registrar vistoria |
| POST | `/vistorias/{id}/pagamento` | Registrar pagamento |
| POST | `/pagamentos/{id}/confirmar-boleto` | Confirmar pagamento de boleto (Gerente) |
| POST | `/vistorias/{id}/laudo` | Gerar laudo em PDF |
| GET | `/vistorias/{id}/laudo/download` | Baixar laudo em PDF |
| GET | `/relatorios/operacional?inicio=...&fim=...` | Relatório operacional (Gerente) |
| GET | `/relatorios/financeiro?inicio=...&fim=...` | Relatório financeiro (Gerente) |

## Estrutura do projeto

```
src/main/java/br/com/william/autovistorbackend/
├── entity/       # entidades JPA
├── repository/    # Spring Data JPA
├── service/       # regras de negócio
├── controller/     # endpoints REST
├── dto/            # objetos de entrada/saída
├── security/       # JWT, autenticação
├── exception/       # exceções e tratamento global de erros
└── config/           # segurança, beans

src/main/resources/
├── application.properties
└── db/migration/    # migrações Flyway (V1, V2, ...)
```
