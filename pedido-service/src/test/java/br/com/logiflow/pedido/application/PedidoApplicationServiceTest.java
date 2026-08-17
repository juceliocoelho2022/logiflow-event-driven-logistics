package br.com.logiflow.pedido.application;

import br.com.logiflow.pedido.domain.PedidoRepository;
import br.com.logiflow.pedido.outbox.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PedidoApplicationServiceTest {

    private final PedidoRepository pedidos = mock(PedidoRepository.class);
    private final OutboxRepository outbox = mock(OutboxRepository.class);
    private final PedidoApplicationService service = new PedidoApplicationService(
            pedidos,
            outbox,
            new ObjectMapper().findAndRegisterModules());

    @Test
    void deveRecusarPedidoSemItens() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.criar(UUID.randomUUID(), List.of()));
    }

    @Test
    void deveRecusarListaDeItensNula() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.criar(UUID.randomUUID(), null));
    }

    @Test
    void deveRecusarQuantidadeInvalida() {
        var item = new PedidoApplicationService.NovoItem(
                "SKU-001",
                0,
                new BigDecimal("15.50"));

        assertThrows(
                IllegalArgumentException.class,
                () -> service.criar(UUID.randomUUID(), List.of(item)));
    }

    @Test
    void deveRecusarPrecoInvalido() {
        var item = new PedidoApplicationService.NovoItem(
                "SKU-001",
                1,
                BigDecimal.ZERO);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.criar(UUID.randomUUID(), List.of(item)));
    }

    @Test
    void deveCalcularTotalEPersistirPedidoEEvento() {
        var item = new PedidoApplicationService.NovoItem(
                "SKU-001",
                2,
                new BigDecimal("15.50"));

        var pedido = service.criar(UUID.randomUUID(), List.of(item));

        assertEquals(new BigDecimal("31.00"), pedido.getValorTotal());
        verify(pedidos).save(pedido);
        verify(outbox).save(org.mockito.ArgumentMatchers.any());
    }
}
