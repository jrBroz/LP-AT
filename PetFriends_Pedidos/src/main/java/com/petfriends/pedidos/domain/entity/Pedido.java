package com.petfriends.pedidos.domain.entity;

import jakarta.persistence.*;

/**
 * Entidade principal do microsserviço de Pedidos.
 * Armazena os dados do pedido e mantém o rastreio do status atual 
 * ao longo de toda a aplicação (Almoxarifado -> Transporte).
 */
@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID de Negócio Único que será repassado para Almoxarifado e Transporte
    @Column(name = "pedido_id", nullable = false, unique = true)
    private String pedidoId;

    @Column(nullable = false)
    private String cepDestino;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status;

    protected Pedido() {}

    public Pedido(String pedidoId, String cepDestino) {
        this.pedidoId = pedidoId;
        this.cepDestino = cepDestino;
        this.status = StatusPedido.CRIADO;
    }

    public void atualizarStatus(StatusPedido novoStatus) {
        this.status = novoStatus;
    }

    public Long getId() { return id; }
    public String getPedidoId() { return pedidoId; }
    public String getCepDestino() { return cepDestino; }
    public StatusPedido getStatus() { return status; }

    public enum StatusPedido {
        CRIADO, EM_SEPARACAO, SEPARADO, EM_ROTA, ENTREGUE
    }
}
