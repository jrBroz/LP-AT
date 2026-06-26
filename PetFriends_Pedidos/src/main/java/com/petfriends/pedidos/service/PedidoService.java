package com.petfriends.pedidos.service;

import com.petfriends.pedidos.config.RabbitMQConfig;
import com.petfriends.pedidos.domain.entity.Pedido;
import com.petfriends.pedidos.domain.repository.PedidoRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class PedidoService {

    private final PedidoRepository repository;
    private final RabbitTemplate rabbitTemplate;
    public static final String ROUTING_KEY_PEDIDO_CRIADO = "pedido.criado";
    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    public PedidoService(PedidoRepository repository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public Pedido criarNovoPedido(String cepDestino) {
        String pedidoIdStr = "PED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("[PEDIDOS] ======= INICIANDO FLUXO DO PEDIDO: {} =======", pedidoIdStr);
        
        // Persiste o Pedido no Banco de Dados
        Pedido pedido = new Pedido(pedidoIdStr, cepDestino);
        pedido = repository.save(pedido);
        log.info("[PEDIDOS] Pedido salvo no banco de dados com status CRIADO.");

        // Publica o evento para o Almoxarifado consumir
        Map<String, Object> eventoPedidoCriado = new HashMap<>();
        eventoPedidoCriado.put("pedidoId", pedido.getPedidoId());
        eventoPedidoCriado.put("cepDestino", pedido.getCepDestino());
        
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, ROUTING_KEY_PEDIDO_CRIADO, eventoPedidoCriado);
        log.info("[PEDIDOS] Evento de domínio 'pedido.criado' despachado para o mensageiro.");

        return pedido;
    }

    /**
     * Escuta a fila de atualização de status. 
     * Almoxarifado e Transporte enviarão eventos de "pedido.status.update" para manter a aplicação sincronizada.
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_STATUS_PEDIDO)
    public void atualizarStatusDoPedido(Map<String, String> payload) {
        String pedidoId = payload.get("pedidoId");
        String novoStatusStr = payload.get("status");

        Optional<Pedido> pedidoOpt = repository.findByPedidoId(pedidoId);
        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            try {
                Pedido.StatusPedido novoStatus = Pedido.StatusPedido.valueOf(novoStatusStr);
                pedido.atualizarStatus(novoStatus);
                repository.save(pedido);
                log.info("[PEDIDOS] <- Evento de Feedback Recebido! Status do pedido {} atualizado para: {}", pedidoId, novoStatus);
            } catch (IllegalArgumentException e) {
                log.warn("[PEDIDOS] Status desconhecido recebido do mensageiro: {}", novoStatusStr);
            }
        }
    }
}
