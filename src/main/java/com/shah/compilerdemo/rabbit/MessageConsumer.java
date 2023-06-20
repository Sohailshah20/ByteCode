package com.shah.compilerdemo.rabbit;

import com.shah.compilerdemo.model.CodeMessage;
import com.shah.compilerdemo.service.DockerService;
import com.shah.compilerdemo.utils.MessageConstant;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class MessageConsumer {

    private final DockerService dockerService;

    public MessageConsumer(DockerService dockerService) {
        this.dockerService = dockerService;
    }

    @RabbitListener(queues = MessageConstant.JAVA_QUEUE)
    public String consumeFromQueue(CodeMessage codeMessage){
        File file = codeMessage.codelFile();
        return dockerService.compileCode(file.getName());
    }


}
