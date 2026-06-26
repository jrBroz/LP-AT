package com.petfriends.almoxarifado.service;

import com.petfriends.almoxarifado.config.RabbitMQConfig;
import com.petfriends.almoxarifado.domain.entity.OrdemServicoAlmoxarifado;
import com.petfriends.almoxarifado.domain.repository.OrdemServicoRepository;
import com.petfriends.almoxarifado.domain.valueobject.EnderecoEstoque;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * Questão 10: Serviço (Consumidor) que recebe os eventos do PetFriends_Pedidos.
 */
@Service
public class PedidoCriadoListener {

    /**
     * Ouve a fila do almoxarifado. Quando o microsserviço Pedidos publicar o evento "pedido.criado",
     * esta função consumirá a mensagem para executar a funcionalidade do Almoxarifado.
     */
    private final OrdemServicoRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(PedidoCriadoListener.class);

    public PedidoCriadoListener(OrdemServicoRepository repository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ALMOXARIFADO)
    public void receberEventoPedidoCriado(Map<String, String> payload) {
        String pedidoId = payload.get("pedidoId");
        log.info("[ALMOXARIFADO] -> Evento consumido. Iniciando processamento do pedido: {}", pedidoId);

        // FUNCIONALIDADE REAL DO ALMOXARIFADO NUM SISTEMA DE E-COMMERCE:
        // 1. Verificar se os itens (rações, coleiras) existem em estoque.
        // 2. Decrementar a quantidade na tabela principal de Inventário (Estoque Geral).
        // 3. Gerar a Ordem de Separação para que um funcionário (Estoquista)
        //    vá fisicamente até o Corredor e a Prateleira embalar a caixa.
        
        EnderecoEstoque endereco = new EnderecoEstoque("Corredor A", "Prateleira 5");
        OrdemServicoAlmoxarifado ordem = new OrdemServicoAlmoxarifado(pedidoId, endereco);
        
        // Simula que o estoquista bipou o código de barras da caixa e fechou o pacote.
        ordem.concluirSeparacao(); 
        repository.save(ordem);
        log.info("[ALMOXARIFADO] Estoque decrementado. Ordem de empacotamento salva e fechada no BD.");

        // Atualiza a central de Pedidos via mensageria
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("pedidoId", pedidoId);
        statusUpdate.put("status", "SEPARADO");
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "pedido.status.update", statusUpdate);

        // Dispara gatilho para o caminhão (Transporte) vir buscar a caixa na doca
        Map<String, String> eventoTransporte = new HashMap<>();
        eventoTransporte.put("pedidoId", pedidoId);
        eventoTransporte.put("cepDestino", payload.get("cepDestino"));
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "pedido.separado", eventoTransporte);
        
        log.info("[ALMOXARIFADO] Evento 'pedido.separado' repassado ao módulo de Transporte.");
    }
}
