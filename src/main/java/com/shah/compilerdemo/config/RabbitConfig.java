package com.shah.compilerdemo.config;

import com.shah.compilerdemo.utils.MessageConstant;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue queue(){
        return new Queue(MessageConstant.JAVA_QUEUE);
    }
    @Bean
    public Queue replyQueue(){
        return new Queue(MessageConstant.REPLY_QUEUE);
    }

    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(MessageConstant.JAVA_TOPIC_EXCHANGE);
    }
    @Bean
    public TopicExchange replyExchange(){
        return new TopicExchange(MessageConstant.REPLY_TOPIC_EXCHANGE);
    }

    @Bean Binding binding(Queue queue, TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(MessageConstant.JAVA_ROUTE_KEY);
    }
    @Bean Binding replyBinding(Queue queue, TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(MessageConstant.REPLY_TOPIC_EXCHANGE);
    }

    @Bean
    public MessageConverter converter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory factory){
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(factory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}
