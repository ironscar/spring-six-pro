package com.ti.demo.springsixstarter.reactive.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.ti.demo.domain.exception.StudentErrorResponse;
import com.ti.demo.domain.exception.StudentException;
import com.ti.demo.domain.reactive.ComplexStudent;
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
            // added for web client async check
            Thread.sleep(1000);

            // get req params
            String fname = request.queryParam("fname").orElse(null);
            String lname = request.queryParam("lname").orElse(null);
            boolean isCustom = request.queryParam("custom").isPresent();
            boolean isComplex = request.queryParam("complex").isPresent();

            // complex implies join query and otherwise simple
            return (isComplex
                ? studentService.getComplexStudents(fname, lname)
                : studentService.getStudents(fname, lname, isCustom)
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
            // added for web client async check
            Thread.sleep(500);

            // get req params
            boolean isComplex = request.queryParam("complex").isPresent();
            int id = Integer.parseInt(request.pathVariable("id"));

            // complex implies join query and otherwise simple
            return (isComplex ? studentService.getComplexStudentById(id) : studentService.getStudentById(id))
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
            // added for web client async check
            Thread.sleep(2000);

            // get req params
            boolean isComplex = request.queryParam("complex").isPresent();

            return (isComplex    
                ? request.bodyToMono(ComplexStudent.class)
                    .flatMap(student -> studentService.saveGreetingsForComplexStudent(student))
                : request.bodyToMono(Student.class)
                    .flatMap(student -> studentService.saveStudent(student))
            ).then(ServerResponse.ok().build())
            .onErrorResume(this::buildErrorResponse);
        } catch (Exception e) {
            throw new StudentException(request.path() + "::" + e.getMessage());
        }     
    }

    public Mono<ServerResponse> deleteStudentById(ServerRequest request) {
        try {
            // added for web client async check
            Thread.sleep(1500);
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

    public Mono<ServerResponse> updateStudent(ServerRequest request) {
        try {
            String id = request.pathVariable("id");
            Integer actualId = StringUtils.hasLength(id) ? Integer.parseInt(id) : null;
            return request.bodyToMono(Student.class)
                .flatMap(student -> studentService.updateStudent(actualId, student))
                .then(ServerResponse.ok().build())
                .onErrorResume(this::buildErrorResponse);
        } catch (Exception e) {
            throw new StudentException(request.path() + "::" + e.getMessage());
        }
    }

    public Mono<ServerResponse> updateStudents(ServerRequest request) {
        try {
            // added for web client async check
            Thread.sleep(2000);
            String ids = request.queryParam("ids").orElse(null);
            String lastName = request.queryParam("lname").orElse(null);
            boolean useCustom = request.queryParam("custom").isPresent();
            List<String> idList = ids == null ? Collections.emptyList() : Arrays.asList(ids.split(","));
            return studentService.updateStudents(idList, lastName, useCustom)
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
