package com.ti.demo.springsixstarter.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ti.demo.domain.Coach;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/app")
public class FirstController {

    /**
     * Field injection example
     * We specify qualifier to make sure there are no bean conflicts
     * It works fine even when its a private variable
     */
    @Autowired
    @Qualifier("soccer")
    private Coach fieldCoach;

    private Map<String, Coach> coachMap;

    @Value("${app.custom.value1}")
    String value1;

    public FirstController() {
        log.debug("in non-param constructor");
        coachMap = new HashMap<>();
    }

    /**
     * Constructor injection example
     * If there is only one constructor, autowired annotation is unnecessary
     * in this case, spring automatically chooses this constructor as there is a bean to autowire
     * Since there are two coach beans now, we use qualifier to choose which one
     * Generally used for required dependencies
     * 
     * @param coach coach impl1
     */
    @Autowired
    public FirstController(@Qualifier("tennis") Coach coach) {
        log.debug("in param constructor");
        coachMap = new HashMap<>();
        coachMap.put("tennis", coach);
    }

    /**
     * Setter injection example
     * This gets called automatically after object creation
     * Since there are two coach beans now, we use qualifier to choose which one
     * Autowired annotation can be added to any method which gets automatically called
     * Generally used for optional dependencies
     * 
     * @param coach coach impl2
     */
    @Autowired
    public void setCoach(@Qualifier("cricket") Coach coach) {
        log.debug("in setter");
        coachMap.put("cricket", coach);
    }

    @GetMapping("/{type}/workout")
    public String getDailyWorkout(@PathVariable("type") String type) {
        return coachMap.get(type) != null ? coachMap.get(type).getDailyWorkout() : fieldCoach.getDailyWorkout();
    }
    
}
