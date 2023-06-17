package com.shah.compilerdemo.service;

import com.shah.compilerdemo.model.CodeMessage;
import org.jvnet.hk2.annotations.Service;

import java.io.File;

@Service
public class ControllerService {

    private final FileOperationsService fileService;
    private final DockerService dockerService;
    private final MessageService messageService;

    public ControllerService(FileOperationsService fileService, DockerService dockerService, MessageService messageService) {
        this.fileService = fileService;
        this.dockerService = dockerService;
        this.messageService = messageService;
    }

    public void runCode(String code){
        File file = fileService.saveFile(code);
        messageService.addCodeToQueue(new CodeMessage(file));
    }
}
