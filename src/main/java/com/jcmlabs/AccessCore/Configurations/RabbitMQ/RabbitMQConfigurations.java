package com.jcmlabs.AccessCore.Configurations.RabbitMQ;

import com.jcmlabs.AccessCore.Shared.Payload.ConfigurationProperties.RabbitmqConfigurationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RabbitMQConfigurations {

    private final RabbitmqConfigurationProperties props;

    @Bean
    public ConnectionFactory rabbitConnectionFactory() {
        log.info("[RabbitMQ] Connecting to {}:{}", props.host(), props.port());

        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(props.host());
        factory.setPort(props.port());
        factory.setUsername(props.username());
        factory.setPassword(props.password());

        return factory;
    }

    @Bean
    public Queue queue() {
        log.info("[RabbitMQ] Creating Queue: {}", props.template().defaultReceiveQueue());

        return QueueBuilder
                .durable(props.template().defaultReceiveQueue())
                .withArgument("x-dead-letter-exchange", "notifications.dlx")
                .withArgument("x-dead-letter-routing-key", "notifications.dead")
                .build();
    }

    @Bean
    public TopicExchange exchange() {
        log.info("[RabbitMQ] Creating Exchange: {}", props.template().exchange());
        return new TopicExchange(props.template().exchange());
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        log.info("[RabbitMQ] Binding routing key: {}", props.template().routingKey());

        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(props.template().routingKey());
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        log.info("☠️ [RabbitMQ] Creating Dead Letter Exchange: notifications.dlx");
        return new TopicExchange("notifications.dlx");
    }

    @Bean
    public Queue deadLetterQueue() {
        String dlqName = props.template().defaultReceiveQueue() + ".dlq";
        log.info("🪦 [RabbitMQ] Creating Dead Letter Queue: {}", dlqName);

        return QueueBuilder.durable(dlqName).build();
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue,
                                     TopicExchange deadLetterExchange) {
        log.info("🔗 [RabbitMQ] Binding DLQ");

        return BindingBuilder
                .bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with("notifications.dead");
    }
}
