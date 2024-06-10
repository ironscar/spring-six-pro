package com.ti.demo.springsixstarter.reactive.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.ti.demo.domain.exception.StudentErrorResponse;
import com.ti.demo.domain.exception.StudentException;
import com.ti.demo.domain.reactive.Student;
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
                ).onErrorResume(this::buildErrorResponse);
        } catch (Exception e) {
            throw new StudentException(request.path() + "::" + e.getMessage());
        }
    }

    public Mono<ServerResponse> saveStudent(ServerRequest request) {
        try {
            return request.bodyToMono(Student.class)
                .flatMap(student -> studentService.saveStudent(student))
                .then(ServerResponse.ok().build())
                .onErrorResume(this::buildErrorResponse);
        } catch (Exception e) {
            throw new StudentException(request.path() + "::" + e.getMessage());
        }     
    }

    public Mono<ServerResponse> deleteStudentById(ServerRequest request) {
        try {
            List<Integer> ids = new ArrayList<>();
            ids.add(Integer.parseInt(request.pathVariable("id")));
            return studentService.deleteStudents(ids)
                .then(ServerResponse.ok().build())
                .onErrorResume(this::buildErrorResponse);
        } catch (Exception e) {
            throw new StudentException(request.path() + "::" + e.getMessage());
        }
    }

    public Mono<ServerResponse> deleteStudentsInBulk(ServerRequest request) {
        try {
            return request.bodyToMono(new ParameterizedTypeReference<List<Integer>>() {})
                .flatMap(ids -> studentService.deleteStudents(ids))
                .then(ServerResponse.ok().build())
                .onErrorResume(this::buildErrorResponse);
        } catch (Exception e) {
            throw new StudentException(request.path() + "::" + e.getMessage());
        }
    }

    private Mono<ServerResponse> buildErrorResponse(Throwable error) {
        return ServerResponse
            .badRequest()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(StudentErrorResponse
                .builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(error.getMessage())
                .timestamp(new Date())
                .build());
    }

}
