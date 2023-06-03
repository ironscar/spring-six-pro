package com.ti.demo.springsixstarter.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class FirstController {

    @GetMapping("/first")
    public Map<String, String> getFirstResponse() {
        Map<String, String> response = new HashMap<>();
        response.put("key", "value");
        return response;
    }
    
}
