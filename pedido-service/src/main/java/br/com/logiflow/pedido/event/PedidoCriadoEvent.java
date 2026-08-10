package br.com.logiflow.pedido.event;
import java.math.BigDecimal; import java.time.Instant; import java.util.*;
public record PedidoCriadoEvent(UUID eventId, UUID pedidoId, UUID clienteId, List<Item> itens, BigDecimal valorTotal, Instant ocorridoEm, int versao) {
 public record Item(String produtoId, int quantidade, BigDecimal precoUnitario) {}
}
