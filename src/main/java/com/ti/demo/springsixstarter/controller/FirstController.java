package com.ti.demo.springsixstarter.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ti.demo.domain.Coach;
import com.ti.demo.domain.impl.PrototypeCoach;
import com.ti.demo.domain.impl.RequestScopeCoach;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/app")
public class FirstController {

    @Autowired
    @Qualifier("customValue1")
    private String customValue1;

    /**
     * Field injection example
     * We don't specify qualifier even though multiple implementations as one of them is Primary
     * It works fine even when its a private variable
     */
    @Autowired
    private Coach fieldCoach;

    private Map<String, Coach> coachMap;

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
        log.debug("in normal setter");
        coachMap.put("cricket", coach);
    }

    /**
     * this method is to autowire two prototype beans and show that they are actually two separate references
     * 
     * @param coach1 the first bean
     * @param coach2 the second bean
     */
    @Autowired
    public void setPrototypeCoaches(PrototypeCoach coach1, PrototypeCoach coach2) {
        log.debug("in prototype setter");
        
        coach1.setName("proto1");
        coachMap.put(coach1.getName(), coach1);

        coach2.setName("proto2");
        coachMap.put(coach2.getName(), coach2);

        log.debug("proto1 name : " + coach1.getName());
        log.debug("proto2 name : " + coach2.getName());
    }

    /**
     * this method is to autowire the request bean here even though the bean only gets created when a request is received
     * 
     * @param requestCoach - the request bean
     */
    @Autowired
    public void setRequestCoach(RequestScopeCoach requestCoach) {
        log.debug("in request setter");
        coachMap.put("request", requestCoach);
    }

    @GetMapping("/{type}/workout")
    public String getDailyWorkout(@PathVariable("type") String type) {
        log.debug("get typed workout controller start with custom value: " + customValue1);
        return coachMap.get(type) != null ? coachMap.get(type).getDailyWorkout() : fieldCoach.getDailyWorkout();
    }
    
}
