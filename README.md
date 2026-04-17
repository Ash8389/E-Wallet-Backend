# E-Wallet Backend

A production-grade digital wallet system built with Java Spring Boot microservices. Supports user registration, wallet management, fund transfers, and real-time payment notifications.

---

## Architecture

```
Client
  │
  ▼
API Gateway (8080)          ← JWT validation, routes requests
  │
  ├──▶ User Service (8081)         ← Registration, Login, JWT
  ├──▶ Wallet Service (8082)       ← Wallets, Balance, Transfers
  └──▶ Transaction Service (8083)  ← Transfer orchestration

Transaction Service ──REST──▶ Wallet Service   (sync, needs immediate result)
Transaction Service ──Kafka──▶ Notification Service  (async, fire-and-forget)

Infrastructure: MySQL · Redis · Apache Kafka
```

### Why sync for transfers, async for notifications?

A money transfer needs an immediate answer — does the wallet have sufficient balance? Did the debit succeed? This is a request-response operation, so it uses synchronous REST with a circuit breaker.

Sending a notification after a payment completes doesn't need a reply. If it's delayed by a few seconds, nothing breaks. This is where Kafka belongs — true fire-and-forget fan-out.

---

## Services

| Service | Port | Responsibility |
|---|---|---|
| API Gateway | 8080 | JWT validation, request routing |
| User Service | 8081 | Register, login, user details |
| Wallet Service | 8082 | Create wallet, credit, debit, transfer |
| Transaction Service | 8083 | Orchestrate transfers, transaction history |
| Notification Service | 8084 | Payment status notifications via Kafka |

---

## Tech Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.3.4 |
| API Gateway | Spring Cloud Gateway |
| Auth | JWT (JJWT) + Spring Security |
| Database | MySQL 8.0 — separate DB per service |
| Cache | Redis 7 — balance cache (5 min TTL), user detail cache |
| Messaging | Apache Kafka — payment notification events |
| Resilience | Resilience4j — Circuit Breaker + Retry |
| Validation | Jakarta Validation (`@Valid`, `@NotNull`, `@DecimalMin`) |
| Money | `BigDecimal` throughout — no floating point |
| API Docs | Springdoc OpenAPI / Swagger UI |
| Containerisation | Docker + Docker Compose |
| CI/CD | GitHub Actions → Docker Hub |

---

## Key Design Decisions

**Idempotency keys** — every transfer request requires an `Idempotency-Key` header. Duplicate requests with the same key return the original result without processing again. Safe to retry on network failure.

**Pessimistic locking** — wallet transfers use `SELECT FOR UPDATE` to prevent race conditions when two concurrent transfers try to debit the same wallet simultaneously.

**JWT in API Gateway** — the gateway validates every token and injects `X-User-Id`, `X-User-Email`, and `X-User-Name` headers before forwarding. Downstream services trust these headers and never see the raw token. Identity is always server-derived, never client-supplied.

**Separate databases** — each service owns its own MySQL database (`userdb`, `walletdb`, `transactiondb`). Services cannot query each other's tables directly.

**Circuit breaker on wallet client** — if the wallet service is unreachable, the circuit breaker opens after 3 failures and the transaction immediately returns `FAILED` instead of hanging.

---

## API Endpoints

All requests go through the API Gateway at `http://localhost:8080`.

### Auth — public, no token required

```bash
# Register
POST /api/users/register
Content-Type: application/json

{
  "name": "Ash",
  "email": "ash@example.com",
  "password": "password123"
}

# Login — returns JWT token
POST /api/users/login
Content-Type: application/json

{
  "email": "ash@example.com",
  "password": "password123"
}
```

### All other endpoints — require `Authorization: Bearer <token>`

```bash
# Create wallet
POST /api/wallet/
Authorization: Bearer <token>

# Get wallet details
GET /api/wallet/
Authorization: Bearer <token>

# Get balance
GET /api/wallet/balance/
Authorization: Bearer <token>

# Credit wallet (top up)
PUT /api/wallet/credit
Authorization: Bearer <token>
Content-Type: application/json

{ "amount": 1000.00 }

# Debit wallet (withdraw)
PUT /api/wallet/debit
Authorization: Bearer <token>
Content-Type: application/json

{ "amount": 500.00 }

# Transfer money
POST /api/transactions/transfer
Authorization: Bearer <token>
Idempotency-Key: unique-key-abc123
Content-Type: application/json

{
  "receiverId": 2,
  "amount": 250.00
}

# Transaction history
GET /api/transactions
Authorization: Bearer <token>
```

### Transfer response

```json
{
  "id": 1,
  "senderId": 101,
  "receiverId": 202,
  "amount": 250.00,
  "status": "DONE",
  "sendAt": "2026-04-18T10:30:00"
}
```

Status values: `PENDING` → `DONE` or `FAILED`

---

## Running Locally

### Prerequisites
- Docker Desktop

### Start everything

```bash
git clone https://github.com/Ash8389/E-Wallet-Backend.git
cd E-Wallet-Backend
docker compose up
```

That's it. Docker Compose starts MySQL, Redis, Zookeeper, Kafka, and all 4 services in the correct order using health checks.

**First run note:** MySQL port is mapped to `3307` on your host (to avoid conflicts with a local MySQL). Services communicate internally on port `3306`.

### Verify services are running

```bash
docker compose ps
```

All containers should show `running`. If any service shows `restarting`, check logs:

```bash
docker compose logs user-service
```

### Check infrastructure

```bash
# MySQL — verify all 3 databases were created
docker exec ewallet-mysql mysql -u ewallet -pewallet1234 -e "SHOW DATABASES;"

# Redis
docker exec ewallet-redis redis-cli ping

# Kafka topics (appear after first transaction)
docker exec ewallet-kafka kafka-topics --bootstrap-server localhost:9092 --list
```

### Stop

```bash
docker compose down          # stop containers
docker compose down -v       # stop + wipe database
```

---

## Project Structure

```
E-Wallet-Backend/
├── API-Gateway/              Spring Cloud Gateway + JWT filter
├── userservice/              Registration, login, user details
├── walletservice/            Wallet CRUD, balance, transfers
├── transactionservice/       Transfer orchestration
├── common-dto/               Shared event classes (TransactionEvent, Status)
├── docker-compose.yml        Full stack setup
├── init.sql                  Creates userdb, walletdb, transactiondb
└── .github/workflows/        GitHub Actions CI/CD
```

---

## CI/CD

GitHub Actions pipeline on every push to `main`:

```
Build common-dto
      ↓
Build all 5 services in parallel
      ↓
Push Docker images to Docker Hub
    ash270/ewallet-user-service:latest
    ash270/ewallet-user-service:<commit-sha>
    ash270/ewallet-wallet-service:latest
    ...
```

Each image gets two tags — `:latest` for convenience, a short commit SHA (e.g. `:abc1234`) for rollbacks.

Pull requests run a validation check that compiles all services and scans for hardcoded secrets before merging.

---

## Environment Variables

Services are configured via environment variables with local defaults for development.

| Variable | Service | Default |
|---|---|---|
| `SPRING_DATASOURCE_URL` | all | `localhost:3306` |
| `SPRING_DATA_REDIS_HOST` | user, wallet | `localhost` |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | wallet, transaction | `localhost:9092` |
| `WALLET_SERVICE_URL` | transaction | `http://localhost:8082` |
| `USER_SERVICE_URL` | gateway | `http://localhost:8081` |
| `JWT_SECRET` | user, gateway | development default |
