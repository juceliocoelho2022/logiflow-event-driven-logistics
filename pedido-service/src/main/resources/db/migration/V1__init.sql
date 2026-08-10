CREATE TABLE pedidos (
  id UUID PRIMARY KEY,
  cliente_id UUID NOT NULL,
  valor_total NUMERIC(15,2) NOT NULL,
  status VARCHAR(30) NOT NULL,
  criado_em TIMESTAMPTZ NOT NULL
);
CREATE TABLE itens_pedido (
  id UUID PRIMARY KEY,
  pedido_id UUID NOT NULL REFERENCES pedidos(id),
  produto_id VARCHAR(80) NOT NULL,
  quantidade INTEGER NOT NULL,
  preco_unitario NUMERIC(15,2) NOT NULL
);
CREATE TABLE outbox_eventos (
  id UUID PRIMARY KEY,
  aggregate_id UUID NOT NULL,
  tipo VARCHAR(100) NOT NULL,
  payload TEXT NOT NULL,
  criado_em TIMESTAMPTZ NOT NULL,
  publicado_em TIMESTAMPTZ
);
CREATE INDEX idx_outbox_pendente ON outbox_eventos(criado_em) WHERE publicado_em IS NULL;
