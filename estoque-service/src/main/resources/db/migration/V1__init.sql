CREATE TABLE reservas (
 id UUID PRIMARY KEY,
 pedido_id UUID NOT NULL UNIQUE,
 status VARCHAR(30) NOT NULL,
 criada_em TIMESTAMPTZ NOT NULL
);
CREATE TABLE itens_reserva (
 id UUID PRIMARY KEY,
 reserva_id UUID NOT NULL REFERENCES reservas(id),
 produto_id VARCHAR(80) NOT NULL,
 quantidade INTEGER NOT NULL
);
CREATE TABLE eventos_processados (
 event_id UUID PRIMARY KEY,
 processado_em TIMESTAMPTZ NOT NULL
);
