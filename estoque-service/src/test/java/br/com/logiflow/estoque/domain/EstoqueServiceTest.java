package br.com.logiflow.estoque.domain;

import br.com.logiflow.estoque.event.PedidoCriadoEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EstoqueServiceTest {

    private final ReservaJpaRepository reservas = mock(ReservaJpaRepository.class);
    private final EventoProcessadoRepository eventos = mock(EventoProcessadoRepository.class);
    private final EstoqueService service = new EstoqueService(reservas, eventos);

    @Test
    void deveCriarReservaParaEventoNovo() {
        var evento = eventoComItens();
        when(eventos.existsById(evento.eventId())).thenReturn(false);

        service.reservar(evento);

        verify(reservas).save(org.mockito.ArgumentMatchers.any(Reserva.class));
        verify(eventos).save(org.mockito.ArgumentMatchers.any(EventoProcessado.class));
    }

    @Test
    void deveIgnorarEventoDuplicado() {
        var evento = eventoComItens();
        when(eventos.existsById(evento.eventId())).thenReturn(true);

        service.reservar(evento);

        verify(reservas, never()).save(org.mockito.ArgumentMatchers.any());
        verify(eventos, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void deveRecusarEventoSemItens() {
        var evento = new PedidoCriadoEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of(),
                BigDecimal.ZERO,
                Instant.now(),
                1);

        assertThrows(
                EstoqueService.EventoPermanenteException.class,
                () -> service.reservar(evento));

        verify(reservas, never()).save(org.mockito.ArgumentMatchers.any());
        verify(eventos, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void deveListarReservas() {
        when(reservas.findAll()).thenReturn(List.of());

        assertEquals(List.of(), service.listar());
        verify(reservas).findAll();
    }

    private PedidoCriadoEvent eventoComItens() {
        return new PedidoCriadoEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of(new PedidoCriadoEvent.Item(
                        "SKU-001",
                        2,
                        new BigDecimal("15.50"))),
                new BigDecimal("31.00"),
                Instant.now(),
                1);
    }
}
