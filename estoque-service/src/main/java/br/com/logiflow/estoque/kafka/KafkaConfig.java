package br.com.logiflow.estoque.kafka;
import br.com.logiflow.estoque.domain.EstoqueService.EventoPermanenteException; import org.apache.kafka.common.TopicPartition; import org.springframework.context.annotation.*; import org.springframework.kafka.core.KafkaTemplate; import org.springframework.kafka.listener.*; import org.springframework.util.backoff.FixedBackOff;
@Configuration public class KafkaConfig {
 @Bean DefaultErrorHandler errorHandler(KafkaTemplate<Object,Object> template){var recoverer=new DeadLetterPublishingRecoverer(template,(r,e)->new TopicPartition(r.topic()+".DLT",r.partition()));var handler=new DefaultErrorHandler(recoverer,new FixedBackOff(1500L,2L));handler.addNotRetryableExceptions(EventoPermanenteException.class);return handler;}
}
