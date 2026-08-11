package br.com.logiflow.estoque.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;

    public OutboxPublisher(
            OutboxRepository repository,
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${logiflow.topics.reserva-confirmada}") String topic
    ) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Scheduled(fixedDelayString = "${logiflow.outbox.fixed-delay:2000}")
    @Transactional
    public void publicar() {
        repository
                .findTop50ByPublicadoEmIsNullOrderByCriadoEmAsc()
                .forEach(item -> {
                    try {
                        kafkaTemplate.send(
                                topic,
                                item.getAggregateId().toString(),
                                item.getPayload()
                        ).get();

                        item.marcarPublicado();

                        log.info(
                                "ReservaConfirmada publicada eventId={} pedidoId={}",
                                item.getId(),
                                item.getAggregateId()
                        );
                    } catch (Exception exception) {
                        log.error(
                                "Evento de reserva continua pendente eventId={}: {}",
                                item.getId(),
                                exception.getMessage()
                        );
                    }
                });
    }
}