package br.com.logiflow.pedido.outbox;
import br.com.logiflow.pedido.event.PedidoCriadoEvent; import com.fasterxml.jackson.databind.ObjectMapper; import org.slf4j.*; import org.springframework.beans.factory.annotation.Value; import org.springframework.kafka.core.KafkaTemplate; import org.springframework.scheduling.annotation.Scheduled; import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
@Component
public class OutboxPublisher {
 private static final Logger log=LoggerFactory.getLogger(OutboxPublisher.class); private final OutboxRepository repo; private final ObjectMapper json; private final KafkaTemplate<String,Object> kafka; private final String topic;
 public OutboxPublisher(OutboxRepository repo,ObjectMapper json,KafkaTemplate<String,Object> kafka,@Value("${logiflow.topics.pedido-criado}")String topic){this.repo=repo;this.json=json;this.kafka=kafka;this.topic=topic;}
 @Scheduled(fixedDelayString="${logiflow.outbox.fixed-delay}") @Transactional public void publicar(){repo.findTop50ByPublicadoEmIsNullOrderByCriadoEmAsc().forEach(item->{try{var evento=json.readValue(item.getPayload(),PedidoCriadoEvent.class);kafka.send(topic,item.getAggregateId().toString(),evento).get();item.marcarPublicado();log.info("Evento publicado eventId={} pedidoId={}",item.getId(),item.getAggregateId());}catch(Exception e){log.error("Evento pendente eventId={}: {}",item.getId(),e.getMessage());}});}
}
