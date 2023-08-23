package com.ti.demo.domain.exception;

import java.io.Serializable;
import java.util.Date;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentErrorResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private HttpStatus status;
    private String message;
    private Date timestamp;

}
