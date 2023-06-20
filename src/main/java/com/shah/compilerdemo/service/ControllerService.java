package com.shah.compilerdemo.service;

import com.shah.compilerdemo.model.CodeMessage;
import com.shah.compilerdemo.rabbit.MessageProducer;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class ControllerService {

    private final FileOperationsService fileService;
    private final MessageProducer messageService;

    public ControllerService(FileOperationsService fileService, DockerService dockerService, MessageProducer messageService) {
        this.fileService = fileService;
        this.messageService = messageService;
    }

    public String runCode(String code){
        File file = fileService.saveFile(code);
        Object o = messageService.addCodeToQueue(new CodeMessage(file));
        return o.toString();
    }
}
