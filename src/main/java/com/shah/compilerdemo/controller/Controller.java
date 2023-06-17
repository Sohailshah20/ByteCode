package com.shah.compilerdemo.controller;

import com.shah.compilerdemo.service.ControllerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class Controller {
    private final ControllerService controllerService;

    public Controller(ControllerService controllerService) {
        this.controllerService = controllerService;
    }

//    @GetMapping("/hello")
//    public List<String> getContainers() {
//        return service.runCommandInContainer();
//    }

    @PostMapping("/run")
    public void saveFile(
            @RequestParam(name = "code") String code
    ){
         controllerService.runCode(code);
    }

}
