package br.com.logiflow.pedido.application;

import br.com.logiflow.pedido.domain.ItemPedido;
import br.com.logiflow.pedido.domain.Pedido;
import br.com.logiflow.pedido.domain.PedidoRepository;
import br.com.logiflow.pedido.event.PedidoCriadoEvent;
import br.com.logiflow.pedido.outbox.OutboxEvento;
import br.com.logiflow.pedido.outbox.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class PedidoApplicationService {

    private final PedidoRepository pedidos;
    private final OutboxRepository outbox;
    private final ObjectMapper json;

    public PedidoApplicationService(
            PedidoRepository pedidos,
            OutboxRepository outbox,
            ObjectMapper json) {
        this.pedidos = pedidos;
        this.outbox = outbox;
        this.json = json;
    }

    @Transactional
    public Pedido criar(UUID clienteId, List<NovoItem> itens) {
        validar(clienteId, itens);

        Pedido pedido = new Pedido(
                clienteId,
                itens.stream()
                        .map(item -> new ItemPedido(
                                item.produtoId(),
                                item.quantidade(),
                                item.precoUnitario()))
                        .toList());

        pedidos.save(pedido);

        UUID eventId = UUID.randomUUID();
        var evento = new PedidoCriadoEvent(
                eventId,
                pedido.getId(),
                clienteId,
                pedido.getItens().stream()
                        .map(item -> new PedidoCriadoEvent.Item(
                                item.getProdutoId(),
                                item.getQuantidade(),
                                item.getPrecoUnitario()))
                        .toList(),
                pedido.getValorTotal(),
                Instant.now(),
                1);

        try {
            String payload = json.writeValueAsString(evento);
            outbox.save(new OutboxEvento(
                    eventId,
                    pedido.getId(),
                    "PedidoCriado",
                    payload));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Falha ao serializar evento", exception);
        }

        return pedido;
    }

    @Transactional(readOnly = true)
    public List<Pedido> listar() {
        return pedidos.findAll();
    }

    private void validar(UUID clienteId, List<NovoItem> itens) {
        Objects.requireNonNull(clienteId, "O clienteId é obrigatório");

        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("O pedido deve possuir ao menos um item");
        }

        for (NovoItem item : itens) {
            if (item == null) {
                throw new IllegalArgumentException("O pedido não pode possuir item nulo");
            }
            if (item.produtoId() == null || item.produtoId().isBlank()) {
                throw new IllegalArgumentException("O produtoId é obrigatório");
            }
            if (item.quantidade() <= 0) {
                throw new IllegalArgumentException("A quantidade deve ser maior que zero");
            }
            if (item.precoUnitario() == null || item.precoUnitario().signum() <= 0) {
                throw new IllegalArgumentException("O preço unitário deve ser maior que zero");
            }
        }
    }

    public record NovoItem(
            String produtoId,
            int quantidade,
            BigDecimal precoUnitario) {
    }
}
