package com.petfriends.almoxarifado.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Questão 9: Classe de configuração do RabbitMQ para o microsserviço Almoxarifado.
 * Define a infraestrutura (Fila, Exchange e Roteamento) para receber eventos de Pedidos.
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "petfriends.exchange";
    public static final String QUEUE_ALMOXARIFADO = "petfriends.almoxarifado.queue";
    
    // A chave de roteamento para quando um pedido é criado pelo Microsserviço Pedidos
    public static final String ROUTING_KEY_PEDIDO_CRIADO = "pedido.criado";

    @Bean
    public Queue almoxarifadoQueue() {
        return new Queue(QUEUE_ALMOXARIFADO, true);
    }

    @Bean
    public TopicExchange petFriendsExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding bindingAlmoxarifado(Queue almoxarifadoQueue, TopicExchange petFriendsExchange) {
        return BindingBuilder.bind(almoxarifadoQueue).to(petFriendsExchange).with(ROUTING_KEY_PEDIDO_CRIADO);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
