package br.com.logiflow.estoque.event;

import java.time.Instant;
import java.util.UUID;

public record ReservaConfirmadaEvent(
        UUID eventId,
        UUID pedidoId,
        UUID reservaId,
        Instant ocorridoEm,
        int versao
) {
}