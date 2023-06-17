package com.shah.compilerdemo.service;

import com.shah.compilerdemo.model.CodeMessage;
import com.shah.compilerdemo.utils.MessageConstant;
import org.jvnet.hk2.annotations.Service;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.File;

@Service
public class MessageService {

    private final RabbitTemplate rabbitTemplate;
    private final DockerService dockerService;

    public MessageService(RabbitTemplate rabbitTemplate, DockerService dockerService) {
        this.rabbitTemplate = rabbitTemplate;
        this.dockerService = dockerService;
    }

    public void addCodeToQueue(CodeMessage codeMessage){
        rabbitTemplate.convertAndSend(MessageConstant.JAVA_TOPIC_EXCHANGE,MessageConstant.JAVA_ROUTE_KEY,codeMessage);
    }

    @RabbitListener(queues = MessageConstant.JAVA_QUEUE)
    public String consumeFromQueue(CodeMessage codeMessage){
        File file = codeMessage.codelFile();
        return dockerService.compileCode(file.getName());
    }
}
