# LogiFlow

Sistema de logística orientado a eventos para portfólio, construído com Java 21, Spring Boot, PostgreSQL, Apache Kafka e React.

## Arquitetura da Sprint 1

1. `pedido-service` recebe `POST /api/pedidos`.
2. Pedido e evento Outbox são gravados na mesma transação PostgreSQL.
3. Um publicador envia o evento pendente para `logiflow.pedidos.criados.v1`, usando `pedidoId` como chave.
4. `estoque-service` consome o evento, impede duplicidade por `eventId` e registra a reserva.
5. Falhas transitórias são repetidas; depois seguem para `logiflow.pedidos.criados.v1.DLT`.

## Requisitos

- Docker Desktop com Docker Compose
- Java 21 e Maven 3.9 (somente para desenvolvimento fora do Docker)
- Node.js 20+ (somente para desenvolvimento do painel fora do Docker)

## Executar tudo

```bash
docker compose up --build
```

Depois acesse:

- Painel: http://localhost:3000
- Swagger Pedido: http://localhost:8081/swagger-ui.html
- Swagger Estoque: http://localhost:8082/swagger-ui.html
- Kafka UI: http://localhost:8090

## Criar pedido pelo terminal

```bash
curl -X POST http://localhost:8081/api/pedidos \
  -H "Content-Type: application/json" \
  -d '{"clienteId":"11111111-1111-1111-1111-111111111111","itens":[{"produtoId":"SKU-001","quantidade":2,"precoUnitario":49.90}]}'
```

Consulte o pedido e as reservas no painel ou por API:

```bash
curl http://localhost:8081/api/pedidos
curl http://localhost:8082/api/reservas
```

## Testar idempotência

Republique no Kafka UI o mesmo JSON mantendo o mesmo `eventId`. O consumidor reconhecerá o evento já processado e não criará outra reserva.

## Decisões importantes

- Evento é contrato próprio, não entidade JPA.
- `pedidoId` é a chave Kafka e preserva a ordem relativa do pedido.
- Outbox evita pedido confirmado sem evento persistido.
- `eventId` é chave única no consumidor.
- Offset é confirmado somente após a transação do consumidor terminar.
- A DLT possui listener e logs; numa próxima sprint ela terá painel de reprocessamento.

## Próximas sprints

- Expedição e rastreamento de entrega
- Autenticação JWT e perfis
- Reprocessamento seguro da DLT
- Métricas Prometheus/Grafana e tracing
- Testcontainers para fluxo real Kafka/PostgreSQL
- Schema Registry e evolução de contratos

