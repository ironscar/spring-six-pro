package com.ti.demo.springsixstarter.reactive.router;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
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
        return nest(
            path("/reactive/app2/student").and(accept(MediaType.APPLICATION_JSON)),
            route()
                .GET(path("").or(path("/")), handler::getStudents)
                .GET(path("/{id}").or(path("/{id}/")), handler::getStudentById)
                .POST(path("").or(path("/")), handler::saveStudent)
                .DELETE(path("/{id}").or(path("/{id}/")), handler::deleteStudentById)
                .DELETE(path("").or(path("/")), handler::deleteStudentsInBulk)
                .build()
        );
    }

}
