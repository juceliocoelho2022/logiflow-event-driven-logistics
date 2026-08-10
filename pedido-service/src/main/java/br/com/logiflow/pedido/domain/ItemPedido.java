package br.com.logiflow.pedido.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity @Table(name="itens_pedido")
public class ItemPedido {
    @Id private UUID id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="pedido_id", nullable=false) private Pedido pedido;
    @Column(name="produto_id", nullable=false) private String produtoId;
    @Column(nullable=false) private int quantidade;
    @Column(name="preco_unitario", nullable=false) private BigDecimal precoUnitario;
    protected ItemPedido() {}
    public ItemPedido(String produtoId, int quantidade, BigDecimal precoUnitario){this.id=UUID.randomUUID();this.produtoId=produtoId;this.quantidade=quantidade;this.precoUnitario=precoUnitario;}
    void vincular(Pedido pedido){this.pedido=pedido;}
    public BigDecimal subtotal(){return precoUnitario.multiply(BigDecimal.valueOf(quantidade));}
    public String getProdutoId(){return produtoId;} public int getQuantidade(){return quantidade;} public BigDecimal getPrecoUnitario(){return precoUnitario;}
}
