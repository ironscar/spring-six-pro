package com.ti.demo.springsixstarter.reactive.exception;

import java.util.Date;

import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ti.demo.domain.exception.StudentErrorResponse;
import com.ti.demo.domain.exception.StudentException;

import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class ReactiveExceptionHandler implements WebExceptionHandler {

    private ObjectMapper mapper;

    ReactiveExceptionHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (ex instanceof StudentException) {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            return returnWithExchangeBody(
                exchange,
                StudentErrorResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(ex.getMessage())
                    .timestamp(new Date())
                    .build()
            );
        }
        return Mono.error(ex);
    }

    /**
     * Method to abstract away the writing of the error response body to the exchange
     * 
     * @param exchange - exchange
     * @param errorResponse - error response
     * @return - Mono
     */
    private Mono<Void> returnWithExchangeBody(ServerWebExchange exchange, StudentErrorResponse errorResponse) {
        return exchange.getResponse().writeWith(Mono.just(setExchangeBody(exchange, errorResponse)));
    }

    /**
     * Method to parse error response to bytes and return the data buffer to write to exchange
     * 
     * @param exchange -  the exchange
     * @param errorResponse - the error response
     * @return - response bytes as data buffer
     */
    private DataBuffer setExchangeBody(ServerWebExchange exchange, StudentErrorResponse errorResponse) {
        try {
            return exchange.getResponse().bufferFactory().wrap(mapper.writeValueAsBytes(errorResponse));
        } catch (Exception e) {
            String errorMsg = "Error Response couldn't be parsed in reactive exception handler due to : " + e.getMessage();
            return exchange.getResponse().bufferFactory().wrap(errorMsg.getBytes());
        }
    }

}