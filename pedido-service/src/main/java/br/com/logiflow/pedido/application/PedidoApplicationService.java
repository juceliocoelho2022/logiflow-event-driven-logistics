package br.com.logiflow.pedido.application;
import br.com.logiflow.pedido.domain.*; import br.com.logiflow.pedido.event.PedidoCriadoEvent; import br.com.logiflow.pedido.outbox.*;
import com.fasterxml.jackson.core.JsonProcessingException; import com.fasterxml.jackson.databind.ObjectMapper; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal; import java.time.Instant; import java.util.*;
@Service
public class PedidoApplicationService {
 private final PedidoRepository pedidos; private final OutboxRepository outbox; private final ObjectMapper json;
 public PedidoApplicationService(PedidoRepository pedidos,OutboxRepository outbox,ObjectMapper json){this.pedidos=pedidos;this.outbox=outbox;this.json=json;}
 @Transactional public Pedido criar(UUID clienteId,List<NovoItem> itens){
  if(itens.isEmpty()) throw new IllegalArgumentException("O pedido deve possuir ao menos um item");
  Pedido pedido=new Pedido(clienteId,itens.stream().map(i->new ItemPedido(i.produtoId(),i.quantidade(),i.precoUnitario())).toList()); pedidos.save(pedido);
  UUID eventId=UUID.randomUUID(); var evento=new PedidoCriadoEvent(eventId,pedido.getId(),clienteId,pedido.getItens().stream().map(i->new PedidoCriadoEvent.Item(i.getProdutoId(),i.getQuantidade(),i.getPrecoUnitario())).toList(),pedido.getValorTotal(),Instant.now(),1);
  try{outbox.save(new OutboxEvento(eventId,pedido.getId(),"PedidoCriado",json.writeValueAsString(evento)));}catch(JsonProcessingException e){throw new IllegalStateException("Falha ao serializar evento",e);} return pedido;
 }
 @Transactional(readOnly=true) public List<Pedido> listar(){return pedidos.findAll();}
 public record NovoItem(String produtoId,int quantidade,BigDecimal precoUnitario){}
}
