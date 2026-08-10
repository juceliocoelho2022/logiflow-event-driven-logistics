package br.com.logiflow.pedido.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "pedidos")
public class Pedido {
    @Id private UUID id;
    @Column(name="cliente_id", nullable=false) private UUID clienteId;
    @Column(name="valor_total", nullable=false) private BigDecimal valorTotal;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private Status status;
    @Column(name="criado_em", nullable=false) private Instant criadoEm;
    @OneToMany(mappedBy="pedido", cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.EAGER)
    private List<ItemPedido> itens = new ArrayList<>();
    protected Pedido() {}
    public Pedido(UUID clienteId, List<ItemPedido> itens) {
        this.id=UUID.randomUUID(); this.clienteId=clienteId; this.status=Status.CRIADO; this.criadoEm=Instant.now();
        itens.forEach(i -> { i.vincular(this); this.itens.add(i); });
        this.valorTotal=itens.stream().map(ItemPedido::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public UUID getId(){return id;} public UUID getClienteId(){return clienteId;} public BigDecimal getValorTotal(){return valorTotal;}
    public Status getStatus(){return status;} public Instant getCriadoEm(){return criadoEm;} public List<ItemPedido> getItens(){return List.copyOf(itens);}
    public enum Status { CRIADO, PROCESSANDO, CANCELADO }
}
