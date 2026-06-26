package com.petfriends.pedidos.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "petfriends.exchange";
    
    // Fila para o Pedido escutar os retornos (atualizações de status) dos outros microsserviços
    public static final String QUEUE_STATUS_PEDIDO = "petfriends.pedidos.status.queue";
    public static final String ROUTING_KEY_STATUS_UPDATE = "pedido.status.update";

    @Bean
    public Queue statusPedidoQueue() {
        return new Queue(QUEUE_STATUS_PEDIDO, true);
    }

    @Bean
    public TopicExchange petFriendsExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding bindingStatusPedido(Queue statusPedidoQueue, TopicExchange petFriendsExchange) {
        return BindingBuilder.bind(statusPedidoQueue).to(petFriendsExchange).with(ROUTING_KEY_STATUS_UPDATE);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
