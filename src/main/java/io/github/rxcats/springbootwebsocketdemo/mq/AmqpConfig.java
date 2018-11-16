package io.github.rxcats.springbootwebsocketdemo.mq;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfig {

    @Bean
    public ConnectionFactory sampleConnectionFactory() {

        CachingConnectionFactory connectionFactory =  new CachingConnectionFactory("192.168.99.100");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    @Bean
    public AmqpAdmin sampleAmqpAdmin() {
        return new RabbitAdmin(sampleConnectionFactory());
    }

    @Bean
    public RabbitTemplate sampleRabbitTemplate() {
        return new RabbitTemplate(sampleConnectionFactory());
    }

    @Bean
    public DirectExchange sampleDirect() {
        return new DirectExchange("sample");
    }

}
