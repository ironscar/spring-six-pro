package com.ti.demo.springsixstarter.controller;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ti.demo.domain.exception.StudentErrorResponse;
import com.ti.demo.domain.exception.StudentException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public StudentErrorResponse handleException(StudentException e) {
        log.error("Student exception: ", e.getMessage());
        return StudentErrorResponse
            .builder()
            .status(HttpStatus.BAD_REQUEST)
            .message(e.getMessage())
            .timestamp(new Date())
            .build();
    }

}
