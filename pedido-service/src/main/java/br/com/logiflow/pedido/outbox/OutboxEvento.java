package br.com.logiflow.pedido.outbox;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="outbox_eventos")
public class OutboxEvento {
 @Id private UUID id; @Column(name="aggregate_id",nullable=false) private UUID aggregateId; @Column(nullable=false) private String tipo;
 @Column(nullable=false,columnDefinition="TEXT") private String payload; @Column(name="criado_em",nullable=false) private Instant criadoEm; @Column(name="publicado_em") private Instant publicadoEm;
 protected OutboxEvento(){} public OutboxEvento(UUID id,UUID aggregateId,String tipo,String payload){this.id=id;this.aggregateId=aggregateId;this.tipo=tipo;this.payload=payload;this.criadoEm=Instant.now();}
 public UUID getId(){return id;} public UUID getAggregateId(){return aggregateId;} public String getPayload(){return payload;} public Instant getCriadoEm(){return criadoEm;}
 public void marcarPublicado(){this.publicadoEm=Instant.now();}
}
