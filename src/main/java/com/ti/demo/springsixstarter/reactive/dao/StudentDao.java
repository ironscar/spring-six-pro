package com.ti.demo.springsixstarter.reactive.dao;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.ti.demo.domain.reactive.Student;

import reactor.core.publisher.Flux;

public interface StudentDao extends ReactiveCrudRepository<Student, Integer> {

    Flux<Student> findStudentByFirstNameOrLastName(String fname, String lname);
    
}
