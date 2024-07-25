package com.ti.demo.springsixstarter.reactive.dao;

import java.util.List;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.ti.demo.domain.reactive.Student;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StudentDao extends ReactiveCrudRepository<Student, Integer> {

    Flux<Student> findStudentByFirstNameOrLastName(String fname, String lname);

    @Query("""
        SELECT
            id, first_name, last_name, email
        FROM student 
        WHERE first_name = :fname OR last_name = :lname
    """)
    Flux<Student> getStudentsByCustomQuery(String fname, String lname);

    @Modifying
    @Query("""
        UPDATE student SET
            last_name = :lname
        WHERE id in (:ids)     
    """)
    Mono<Long> updateStudentsByCustomLastNameQuery(List<String> ids, String lname);

}
