package com.petfriends.pedidos.controller;

import com.petfriends.pedidos.service.PedidoService;
import com.petfriends.pedidos.domain.entity.Pedido;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

/**
 * Endpoint para a simulação da criação do Pedido.
 * Ao criar o pedido, ele gera o ID e publica o evento na mensageria.
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> criarPedido(@RequestBody Map<String, Object> request) {
        String cep = (String) request.get("cep");
        
        // A lógica de negócio pesada, salvar no DB e publicar no RabbitMQ está toda abstraída no Service
        Pedido pedidoSalvo = pedidoService.criarNovoPedido(cep);

        Map<String, Object> response = new HashMap<>();
        response.put("mensagem", "Pedido criado e gravado com sucesso. Processamento assíncrono iniciado.");
        response.put("pedidoId", pedidoSalvo.getPedidoId());
        
        return ResponseEntity.ok(response);
    }
}
