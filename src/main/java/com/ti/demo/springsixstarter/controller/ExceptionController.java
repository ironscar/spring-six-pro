package com.ti.demo.springsixstarter.controller;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.ti.demo.domain.exception.StudentErrorResponse;
import com.ti.demo.domain.exception.StudentException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler
    public ResponseEntity<StudentErrorResponse> handleException(StudentException e) {
        log.error("Student exception: ", e.getMessage());
        StudentErrorResponse response = StudentErrorResponse
            .builder()
            .status(HttpStatus.BAD_REQUEST)
            .message(e.getMessage())
            .timestamp(new Date())
            .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
