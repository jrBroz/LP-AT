package com.petfriends.transporte.domain.entity;

import com.petfriends.transporte.domain.valueobject.DadosDestino;
import jakarta.persistence.*;

/**
 * Questão 3: Entity (Entidade de Domínio) representando o Agregado do Transporte.
 * Esta entidade gerencia o transporte físico do pedido.
 */
@Entity
@Table(name = "entrega")
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID ÚNICO gerado no microsserviço de Pedidos que se mantém na aplicação.
    // Garante a rastreabilidade do mesmo pedido na logística.
    @Column(name = "pedido_id", nullable = false, unique = true)
    private String pedidoId;

    @Embedded
    private DadosDestino destino;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEntrega status;

    protected Entrega() {
        // JPA
    }

    public Entrega(String pedidoId, DadosDestino destino) {
        this.pedidoId = pedidoId;
        this.destino = destino;
        this.status = StatusEntrega.AGUARDANDO_COLETA;
    }

    public void iniciarRotaDeEntrega() {
        this.status = StatusEntrega.EM_ROTA;
    }

    public void finalizarEntrega() {
        this.status = StatusEntrega.ENTREGUE;
    }

    public Long getId() { return id; }
    public String getPedidoId() { return pedidoId; }
    public DadosDestino getDestino() { return destino; }
    public StatusEntrega getStatus() { return status; }

    public enum StatusEntrega {
        AGUARDANDO_COLETA, EM_ROTA, ENTREGUE
    }
}
