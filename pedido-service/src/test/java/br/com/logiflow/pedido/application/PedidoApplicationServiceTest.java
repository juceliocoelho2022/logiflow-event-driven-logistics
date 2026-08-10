package br.com.logiflow.pedido.application;
import br.com.logiflow.pedido.domain.PedidoRepository; import br.com.logiflow.pedido.outbox.OutboxRepository; import com.fasterxml.jackson.databind.ObjectMapper; import org.junit.jupiter.api.*; import java.math.BigDecimal; import java.util.*; import static org.junit.jupiter.api.Assertions.*; import static org.mockito.Mockito.*;
class PedidoApplicationServiceTest {
 @Test void deveRecusarPedidoSemItens(){var service=new PedidoApplicationService(mock(PedidoRepository.class),mock(OutboxRepository.class),new ObjectMapper());assertThrows(IllegalArgumentException.class,()->service.criar(UUID.randomUUID(),List.of()));}
 @Test void deveCalcularTotal(){var service=new PedidoApplicationService(mock(PedidoRepository.class),mock(OutboxRepository.class),new ObjectMapper().findAndRegisterModules());var p=service.criar(UUID.randomUUID(),List.of(new PedidoApplicationService.NovoItem("SKU",2,new BigDecimal("15.50"))));assertEquals(new BigDecimal("31.00"),p.getValorTotal());}
}
