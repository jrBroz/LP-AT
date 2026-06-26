package com.petfriends.almoxarifado.domain.repository;

import com.petfriends.almoxarifado.domain.entity.OrdemServicoAlmoxarifado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Questão 1: Repository para o Agregado do Almoxarifado.
 */
@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServicoAlmoxarifado, Long> {
    
    // Método para buscar a ordem de serviço baseada no ID original do Pedido.
    Optional<OrdemServicoAlmoxarifado> findByPedidoId(String pedidoId);
}
