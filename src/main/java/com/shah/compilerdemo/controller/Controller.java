package com.shah.compilerdemo.controller;

import com.shah.compilerdemo.service.ControllerService;
import org.springframework.web.bind.annotation.*;

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
    public String saveFile(
            @RequestParam(name = "code") String code
    ){
         return controllerService.runCode(code);
    }

    @GetMapping("/hello")
    public String hello(){
        return "App is running";
    }

}
