package com.ti.demo.springsixstarter.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class FirstController {

    @Value("${app.custom.value1}")
    String value1;

    @GetMapping("/first")
    public Map<String, String> getFirstResponse() {
        Map<String, String> response = new HashMap<>();
        response.put("key", value1);
        return response;
    }
    
}
