package com.ti.demo.springsixstarter.reactive.router;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.ti.demo.springsixstarter.reactive.handler.StudentHandler;

@Configuration
public class StudentRouter {

    @Bean
    public RouterFunction<ServerResponse> studentApiRoutes(StudentHandler handler) {
        return route((
            (GET("/app2/student")
                .or(GET("/app2/student/"))
            ).and(accept(MediaType.APPLICATION_JSON))
        ), handler::getStudents);
    }

}
