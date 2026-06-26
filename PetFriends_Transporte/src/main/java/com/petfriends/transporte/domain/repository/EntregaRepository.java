package com.petfriends.transporte.domain.repository;

import com.petfriends.transporte.domain.entity.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Questão 3: Repository para o Agregado do Transporte.
 */
@Repository
public interface EntregaRepository extends JpaRepository<Entrega, Long> {

    // Método para buscar a entrega baseada no ID original do Pedido.
    Optional<Entrega> findByPedidoId(String pedidoId);
}
