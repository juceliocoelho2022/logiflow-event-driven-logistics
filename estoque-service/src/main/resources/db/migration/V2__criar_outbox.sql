CREATE TABLE outbox_eventos (
                                id UUID PRIMARY KEY,
                                aggregate_id UUID NOT NULL,
                                tipo VARCHAR(100) NOT NULL,
                                payload TEXT NOT NULL,
                                criado_em TIMESTAMPTZ NOT NULL,
                                publicado_em TIMESTAMPTZ
);

CREATE INDEX idx_outbox_estoque_pendente
    ON outbox_eventos(criado_em)
    WHERE publicado_em IS NULL;