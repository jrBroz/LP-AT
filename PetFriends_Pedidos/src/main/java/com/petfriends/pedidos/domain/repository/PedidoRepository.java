package com.petfriends.pedidos.domain.repository;

import com.petfriends.pedidos.domain.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    // Busca um pedido a partir do seu ID global único
    Optional<Pedido> findByPedidoId(String pedidoId);
}
