# FinSense Backend

Backend do **FinSense** — plataforma de inteligência financeira para jovens de 18 a 30 anos, em parceria com a **Claro**.
MVP de trabalho acadêmico (FIAP). Este repositório substitui os dados mockados do [finsense-frontend](../finsense-frontend)
por uma API real: autenticação, transações, metas e resumo financeiro.

> IA/Insights fica para uma etapa futura — fora de escopo aqui.

## Stack

- **Java 17 + Spring Boot 3.3** (Maven)
- **PostgreSQL** (local via Docker Compose por enquanto; trocar para um banco em nuvem depois é só mudar a connection
  string — nenhuma mudança de código)
- **Flyway** para migrations
- **Spring Security + JWT** (jjwt) para autenticação stateless
- **Bean Validation** para validação de payloads

## Estrutura

```
src/main/java/com/finsense/backend/
├── auth/          # registro, login, JWT
├── user/          # entidade User
├── transaction/   # entidade Transaction + Category (alimentacao|transporte|lazer|saude|outros)
├── goal/          # entidade Goal (metas financeiras)
├── summary/        # saldo, gastos totais e por categoria
├── security/       # JwtService, filtro JWT, principal autenticado
├── config/          # SecurityConfig (CORS, filtro, regras de acesso)
└── common/          # tratamento global de erros
src/main/resources/
├── application.yml
└── db/migration/    # V1__..., V2__..., V3__... (Flyway)
```

## Pré-requisitos

- JDK 17+ (não precisa instalar Maven — o projeto usa o Maven Wrapper: `mvnw`/`mvnw.cmd`)
- Docker Desktop (para subir o Postgres local via `docker-compose.yml`) — alternativamente, qualquer Postgres local
  já rodando serve, desde que você ajuste as variáveis de ambiente

## Como rodar localmente

### 1. Subir o banco Postgres

```powershell
docker compose up -d
```

Isso sobe um Postgres 16 em `localhost:5432` com banco `finsense`, usuário `finsense`, senha `finsense`
(exatamente os defaults já configurados em `application.yml` — não precisa exportar nada para rodar local).

Se você não tem Docker instalado: instale o Docker Desktop, ou aponte `DB_URL`/`DB_USERNAME`/`DB_PASSWORD` para um
Postgres local já existente.

### 2. Rodar a aplicação

```powershell
.\mvnw.cmd spring-boot:run
```

Na primeira subida, o Flyway roda as migrations (`V1`, `V2`, `V3`) automaticamente e cria as tabelas `users`,
`transactions` e `goals`. A API sobe em `http://localhost:8080`.

### 3. Rodar os testes / build

```powershell
.\mvnw.cmd test
.\mvnw.cmd package
```

> Este projeto foi validado com `mvnw compile` e `mvnw package` (compila e empacota limpo). A execução completa
> contra um Postgres real (subida da app + chamadas HTTP) depende do Docker estar disponível — valide o passo 2 e os
> curls abaixo no seu ambiente antes de seguir para produção/nuvem.

## Variáveis de ambiente (todas com default para rodar local sem configurar nada)

| Variável | Default | Descrição |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/finsense` | Connection string. **É a única coisa que muda ao trocar para um Postgres em nuvem** (Supabase, RDS, Azure...). |
| `DB_USERNAME` | `finsense` | |
| `DB_PASSWORD` | `finsense` | |
| `JWT_SECRET` | secret de desenvolvimento embutido | **Troque isso em qualquer ambiente compartilhado/deployado.** Precisa ser uma chave Base64 de pelo menos 256 bits. |
| `JWT_EXPIRATION_MS` | `86400000` (24h) | Validade do token |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:4200` | Origem liberada para o Angular local |
| `SERVER_PORT` | `8080` | |

## Endpoints

Todos os endpoints exceto `/api/auth/**` exigem `Authorization: Bearer <token>`.

| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/auth/register` | Cadastro (nome, e-mail, senha, renda mensal opcional) |
| POST | `/api/auth/login` | Login |
| GET | `/api/transactions` | Lista as transações do usuário autenticado |
| POST | `/api/transactions` | Cria uma transação |
| DELETE | `/api/transactions/{id}` | Remove uma transação (204 sem corpo; 404 se não existir ou não for do usuário autenticado) |
| GET | `/api/goals` | Lista as metas do usuário autenticado |
| POST | `/api/goals` | Cria uma meta |
| POST | `/api/goals/{id}/deposit` | Deposita um valor em uma meta existente |
| DELETE | `/api/goals/{id}` | Remove uma meta (204 sem corpo; 404 se não existir ou não for do usuário autenticado) |
| GET | `/api/summary` | Saldo, total de gastos e gastos por categoria |

Categorias válidas (iguais ao frontend, não mude): `alimentacao`, `transporte`, `lazer`, `saude`, `outros`.

## Testando com curl

```bash
# 1. Cadastro
curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Renee Fera","email":"renee@example.com","password":"123456","monthlyIncome":3500}'

# Copie o "token" da resposta acima
TOKEN="cole_o_token_aqui"

# 2. Login (alternativa, se já tiver conta)
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"renee@example.com","password":"123456"}'

# 3. Criar transação
curl -s -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"description":"iFood","amount":45.90,"category":"alimentacao","date":"2026-09-01"}'

# 4. Listar transações
curl -s http://localhost:8080/api/transactions -H "Authorization: Bearer $TOKEN"

# 4b. Remover uma transação (204 sem corpo)
curl -s -o /dev/null -w "%{http_code}\n" -X DELETE http://localhost:8080/api/transactions/<id> \
  -H "Authorization: Bearer $TOKEN"

# 5. Criar meta
curl -s -X POST http://localhost:8080/api/goals \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name":"Viagem para Europa","targetAmount":15000,"emoji":"✈️","deadline":"2027-12-15","color":"#1565C0"}'

# Copie o "id" da meta criada
GOAL_ID="cole_o_id_aqui"

# 6. Depositar em uma meta
curl -s -X POST http://localhost:8080/api/goals/$GOAL_ID/deposit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"amount":200.00}'

# 7. Listar metas
curl -s http://localhost:8080/api/goals -H "Authorization: Bearer $TOKEN"

# 8. Remover uma meta (204 sem corpo)
curl -s -o /dev/null -w "%{http_code}\n" -X DELETE http://localhost:8080/api/goals/$GOAL_ID \
  -H "Authorization: Bearer $TOKEN"

# 9. Resumo financeiro (saldo, gastos totais e por categoria)
curl -s http://localhost:8080/api/summary -H "Authorization: Bearer $TOKEN"
```

No PowerShell, troque `curl` por `curl.exe` (para não cair no alias do `Invoke-WebRequest`) e `-d '...'` por
`-d '...'` com aspas simples também funciona no PowerShell 5.1+.

## Notas de design

- **Senha**: hash com BCrypt (`spring-boot-starter-security`), nunca armazenada em texto puro.
- **JWT**: token único (sem refresh token) contendo o `id` do usuário como subject — suficiente para o escopo atual.
- **Categoria**: enum Java (`Category`) convertido para os mesmos literais em `snake_case` minúsculo usados no
  frontend (`alimentacao`, `transporte`, ...), tanto no JSON quanto no banco (`CHECK` constraint na migration).
- **Renda mensal no cadastro**: o formulário atual do frontend (`auth.component.ts`) não coleta esse campo ainda, por
  isso `monthlyIncome` é opcional no `/api/auth/register` (default `0`) — ajuste quando o formulário for atualizado.
- **Sem Lombok**: o Lombok resolvido pelo Spring Boot Parent (1.18.34) não gera bytecode compatível com o JDK 25
  encontrado neste ambiente (annotation processing falhava silenciosamente). Entidades e serviços usam
  getters/setters/construtores escritos à mão para não depender dessa combinação de versões.
- **Sem testes automatizados ainda**: o escopo pedido foi rodar e testar manualmente via curl antes de pensar em CI;
  adicionar testes de integração (`@SpringBootTest` + Testcontainers, por exemplo) é um próximo passo natural.
