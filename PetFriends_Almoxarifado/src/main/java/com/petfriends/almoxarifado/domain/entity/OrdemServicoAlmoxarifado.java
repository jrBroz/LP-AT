package com.petfriends.almoxarifado.domain.entity;

import com.petfriends.almoxarifado.domain.valueobject.EnderecoEstoque;
import jakarta.persistence.*;

/**
 * Questão 1: Entity (Entidade de Domínio) representando o Agregado do Almoxarifado.
 * Esta entidade gerencia a separação física do pedido no estoque.
 */
@Entity
@Table(name = "ordem_servico_almoxarifado")
public class OrdemServicoAlmoxarifado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID ÚNICO gerado no microsserviço de Pedidos que se mantém na aplicação.
    // Atua como nossa chave de correlação.
    @Column(name = "pedido_id", nullable = false, unique = true)
    private String pedidoId;

    @Embedded
    private EnderecoEstoque enderecoEstoque;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAlmoxarifado status;

    protected OrdemServicoAlmoxarifado() {
        // JPA
    }

    public OrdemServicoAlmoxarifado(String pedidoId, EnderecoEstoque enderecoEstoque) {
        this.pedidoId = pedidoId;
        this.enderecoEstoque = enderecoEstoque;
        this.status = StatusAlmoxarifado.AGUARDANDO_SEPARACAO;
    }

    public void iniciarSeparacao() {
        this.status = StatusAlmoxarifado.EM_SEPARACAO;
    }

    public void concluirSeparacao() {
        this.status = StatusAlmoxarifado.SEPARADO;
    }

    public Long getId() { return id; }
    public String getPedidoId() { return pedidoId; }
    public EnderecoEstoque getEnderecoEstoque() { return enderecoEstoque; }
    public StatusAlmoxarifado getStatus() { return status; }

    public enum StatusAlmoxarifado {
        AGUARDANDO_SEPARACAO, EM_SEPARACAO, SEPARADO
    }
}
