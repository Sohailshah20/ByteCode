package com.shah.compilerdemo.rabbit;

import com.shah.compilerdemo.model.CodeMessage;
import com.shah.compilerdemo.utils.MessageConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageProducer {
    private final RabbitTemplate rabbitTemplate;

    public MessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public Object addCodeToQueue(CodeMessage codeMessage){
        return rabbitTemplate.convertSendAndReceive(MessageConstant.JAVA_TOPIC_EXCHANGE, MessageConstant.JAVA_ROUTE_KEY, codeMessage);
    }


}
