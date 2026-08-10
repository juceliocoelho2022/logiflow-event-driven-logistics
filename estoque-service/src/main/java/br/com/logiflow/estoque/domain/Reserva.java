package br.com.logiflow.estoque.domain;
import br.com.logiflow.estoque.event.PedidoCriadoEvent; import jakarta.persistence.*; import java.time.Instant; import java.util.*;
@Entity @Table(name="reservas") public class Reserva {
 @Id private UUID id; @Column(name="pedido_id",nullable=false,unique=true) private UUID pedidoId; @Enumerated(EnumType.STRING) @Column(nullable=false) private Status status; @Column(name="criada_em",nullable=false) private Instant criadaEm;
 @OneToMany(mappedBy="reserva",cascade=CascadeType.ALL,orphanRemoval=true,fetch=FetchType.EAGER) private List<ItemReserva> itens=new ArrayList<>(); protected Reserva(){}
 public Reserva(PedidoCriadoEvent e){id=UUID.randomUUID();pedidoId=e.pedidoId();status=Status.RESERVADO;criadaEm=Instant.now();e.itens().forEach(i->itens.add(new ItemReserva(this,i.produtoId(),i.quantidade())));}
 public UUID getId(){return id;} public UUID getPedidoId(){return pedidoId;} public Status getStatus(){return status;} public Instant getCriadaEm(){return criadaEm;} public List<ItemReserva> getItens(){return List.copyOf(itens);} public enum Status{RESERVADO,REJEITADO}
}
