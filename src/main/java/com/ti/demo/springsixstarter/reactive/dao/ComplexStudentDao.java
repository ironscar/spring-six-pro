package com.ti.demo.springsixstarter.reactive.dao;

import com.ti.demo.domain.reactive.ComplexStudent;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ComplexStudentDao {

    Flux<ComplexStudent> findAll(String firstName, String lastName);

    Mono<ComplexStudent> findById(Integer id);

    Mono<Long> saveGreetings(ComplexStudent student);

}
