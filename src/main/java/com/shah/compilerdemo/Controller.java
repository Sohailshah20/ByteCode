package com.shah.compilerdemo;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class Controller {

    private final CompileService service;

    public Controller(CompileService service) {
        this.service = service;
    }

    @GetMapping("/hello")
    public List<String> getContainers() {
        return service.runCommandInContainer();
    }

    @PostMapping("/save")
    public String saveFile(
            @RequestParam(name = "code") String code
    ){
        return service.formatCode(code);
    }

}
