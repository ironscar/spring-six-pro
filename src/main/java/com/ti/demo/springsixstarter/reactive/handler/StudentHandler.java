package com.ti.demo.springsixstarter.reactive.handler;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.ti.demo.domain.exception.StudentErrorResponse;
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
            return studentService.getStudents(
                request.queryParam("fname").orElse(null),
                request.queryParam("lname").orElse(null)
            ).flatMap(students -> ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(students)
            );
        } catch (Exception e) {
            throw new StudentException(request.path() + "::" + e.getMessage());
        }
    }

    public Mono<ServerResponse> getStudentById(ServerRequest request) {
        try {
            return studentService.getStudentById(Integer.parseInt(request.pathVariable("id")))
                .flatMap(student -> ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(student)
                ).onErrorResume(error -> ServerResponse
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(StudentErrorResponse
                        .builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message(error.getMessage())
                        .timestamp(new Date())
                        .build())
                );
        } catch (Exception e) {
            throw new StudentException(request.path() + "::" + e.getMessage());
        }
    }

}
