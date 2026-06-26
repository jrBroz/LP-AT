package com.petfriends.transporte.service;

import com.petfriends.transporte.config.RabbitMQConfig;
import com.petfriends.transporte.domain.entity.Entrega;
import com.petfriends.transporte.domain.repository.EntregaRepository;
import com.petfriends.transporte.domain.valueobject.DadosDestino;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * Questão 12: Serviço (Consumidor) que recebe os eventos na logística.
 */
@Service
public class PedidoSeparadoListener {

    /**
     * Ouve a fila do transporte. Quando o evento de que a mercadoria está separada
     * e pronta for publicado, o microsserviço de Transporte executará sua funcionalidade.
     */
    private final EntregaRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(PedidoSeparadoListener.class);

    public PedidoSeparadoListener(EntregaRepository repository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_TRANSPORTE)
    public void receberEventoPedidoSeparado(Map<String, String> payload) {
        String pedidoId = payload.get("pedidoId");
        String cep = payload.get("cepDestino");
        log.info("[TRANSPORTE] -> Mercadoria liberada na doca. Assumindo pedido: {}", pedidoId);

        // FUNCIONALIDADE REAL DO TRANSPORTE NUM SISTEMA DE E-COMMERCE:
        // 1. Integrar com API dos Correios ou Transportadora (JadLog, Loggi)
        // 2. Com base no CEP e Dimensões da Caixa, gerar etiqueta de Rastreamento
        // 3. Traçar a Rota de Entrega e associar a placa do caminhão (motorista).
        // 4. Quando o motorista der partida, o status da entrega vai para EM_ROTA.
        
        DadosDestino destino = new DadosDestino(cep, "Rua Central Simulada");
        Entrega entrega = new Entrega(pedidoId, destino);
        entrega.iniciarRotaDeEntrega();
        repository.save(entrega);
        log.info("[TRANSPORTE] Caminhão despachado (Tracking ID gerado) e salvo no BD de Logística.");

        // Atualiza a central de Pedidos informando que o cliente já pode rastrear o produto
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("pedidoId", pedidoId);
        statusUpdate.put("status", "EM_ROTA");
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "pedido.status.update", statusUpdate);
        
        log.info("[TRANSPORTE] Status finalizado para o módulo de Pedidos. Ciclo concluído.");
    }
}
