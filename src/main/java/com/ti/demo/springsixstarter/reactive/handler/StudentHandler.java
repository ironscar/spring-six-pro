package com.ti.demo.springsixstarter.reactive.handler;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.ti.demo.domain.exception.StudentException;
import com.ti.demo.springsixstarter.service.StudentService;

import reactor.core.publisher.Mono;

@Component
public class StudentHandler {

    private StudentService studentService;

    StudentHandler(StudentService studentService) {
        this.studentService = studentService;
    }

    public Mono<ServerResponse> getStudents(ServerRequest request) {
        try {
            return studentService.getStudents()
                .flatMap(students -> ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(students)
                );
        } catch (Exception e) {
            throw new StudentException(e.getMessage());
        }
    }

}
