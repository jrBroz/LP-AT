package com.petfriends.transporte.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Questão 11: Classe de configuração do RabbitMQ para o microsserviço Transporte.
 * Define a infraestrutura (Fila, Exchange e Roteamento) para receber eventos.
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "petfriends.exchange";
    public static final String QUEUE_TRANSPORTE = "petfriends.transporte.queue";
    
    // Transporte escuta quando a separação (almoxarifado) for concluída e o pedido estiver pronto para viagem
    public static final String ROUTING_KEY_PEDIDO_SEPARADO = "pedido.separado";

    @Bean
    public Queue transporteQueue() {
        return new Queue(QUEUE_TRANSPORTE, true);
    }

    @Bean
    public TopicExchange petFriendsExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding bindingTransporte(Queue transporteQueue, TopicExchange petFriendsExchange) {
        return BindingBuilder.bind(transporteQueue).to(petFriendsExchange).with(ROUTING_KEY_PEDIDO_SEPARADO);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
