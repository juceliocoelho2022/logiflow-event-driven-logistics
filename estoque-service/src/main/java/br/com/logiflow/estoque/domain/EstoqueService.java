package br.com.logiflow.estoque.domain;
import br.com.logiflow.estoque.event.PedidoCriadoEvent; import org.slf4j.*; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.util.*;
@Service public class EstoqueService {
 private static final Logger log=LoggerFactory.getLogger(EstoqueService.class);private final ReservaJpaRepository reservas;private final EventoProcessadoRepository eventos;
 public EstoqueService(ReservaJpaRepository r,EventoProcessadoRepository e){reservas=r;eventos=e;}
 @Transactional public void reservar(PedidoCriadoEvent e){if(eventos.existsById(e.eventId())){log.info("Evento duplicado ignorado eventId={} pedidoId={}",e.eventId(),e.pedidoId());return;}if(e.itens()==null||e.itens().isEmpty())throw new EventoPermanenteException("Pedido sem itens");reservas.save(new Reserva(e));eventos.save(new EventoProcessado(e.eventId()));}
 @Transactional(readOnly=true) public List<Reserva> listar(){return reservas.findAll();}
 public static class EventoPermanenteException extends RuntimeException{public EventoPermanenteException(String m){super(m);}}
}
