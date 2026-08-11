package br.com.logiflow.estoque.domain;

import br.com.logiflow.estoque.event.PedidoCriadoEvent;
import br.com.logiflow.estoque.event.ReservaConfirmadaEvent;
import br.com.logiflow.estoque.outbox.OutboxEvento;
import br.com.logiflow.estoque.outbox.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class EstoqueService {

 private static final Logger log =
         LoggerFactory.getLogger(EstoqueService.class);

 private final ReservaJpaRepository reservas;
 private final EventoProcessadoRepository eventos;
 private final OutboxRepository outbox;
 private final ObjectMapper objectMapper;

 public EstoqueService(
         ReservaJpaRepository reservas,
         EventoProcessadoRepository eventos,
         OutboxRepository outbox,
         ObjectMapper objectMapper
 ) {
  this.reservas = reservas;
  this.eventos = eventos;
  this.outbox = outbox;
  this.objectMapper = objectMapper;
 }

 @Transactional
 public void reservar(PedidoCriadoEvent eventoRecebido) {
  if (eventos.existsById(eventoRecebido.eventId())) {
   log.info(
           "Evento duplicado ignorado eventId={} pedidoId={}",
           eventoRecebido.eventId(),
           eventoRecebido.pedidoId()
   );
   return;
  }

  if (eventoRecebido.itens() == null ||
          eventoRecebido.itens().isEmpty()) {
   throw new EventoPermanenteException("Pedido sem itens");
  }

  Reserva reserva = reservas.save(new Reserva(eventoRecebido));

  UUID eventId = UUID.randomUUID();

  var eventoConfirmado = new ReservaConfirmadaEvent(
          eventId,
          reserva.getPedidoId(),
          reserva.getId(),
          Instant.now(),
          1
  );

  try {
   String payload =
           objectMapper.writeValueAsString(eventoConfirmado);

   outbox.save(new OutboxEvento(
           eventId,
           reserva.getPedidoId(),
           "ReservaConfirmada",
           payload
   ));
  } catch (JsonProcessingException exception) {
   throw new IllegalStateException(
           "Falha ao serializar ReservaConfirmadaEvent",
           exception
   );
  }

  eventos.save(new EventoProcessado(eventoRecebido.eventId()));

  log.info(
          "Reserva criada e registrada no Outbox reservaId={} pedidoId={}",
          reserva.getId(),
          reserva.getPedidoId()
  );
 }

 @Transactional(readOnly = true)
 public List<Reserva> listar() {
  return reservas.findAll();
 }

 public static class EventoPermanenteException
         extends RuntimeException {

  public EventoPermanenteException(String mensagem) {
   super(mensagem);
  }
 }
}