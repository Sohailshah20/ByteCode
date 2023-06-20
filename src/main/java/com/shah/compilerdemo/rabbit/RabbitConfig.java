package com.shah.compilerdemo.rabbit;

import com.shah.compilerdemo.utils.MessageConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

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
        return BindingBuilder.bind(queue).to(replyExchange()).with(MessageConstant.REPLY_ROUTE_KEY);
    }

    @Bean
    public MessageConverter converter(){
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public RabbitTemplate template(ConnectionFactory factory){
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(factory);
        rabbitTemplate.setMessageConverter(converter());
        rabbitTemplate.setReplyAddress(MessageConstant.REPLY_QUEUE);
        rabbitTemplate.setReplyTimeout(Duration.ofSeconds(4).toMillis());
        return rabbitTemplate;
    }
    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer(connectionFactory);
        simpleMessageListenerContainer.setQueues(replyQueue());
        simpleMessageListenerContainer.setMessageListener(template(connectionFactory));
        return simpleMessageListenerContainer;
    }
}
