package com.ti.demo.springsixstarter.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ti.demo.domain.reactive.Student;

import reactor.core.publisher.Mono;

@Service
public class StudentService {

    private static List<Student> students = new ArrayList<>();

    static {
        students.add(Student.builder().id(1).firstName("Nora").lastName("Barnes").email("nbarnes@hzn.com").build());
        students.add(Student.builder().id(2).firstName("Kora").lastName("Barnes").email("kbarnes@hzn.com").build());
    }

    public Mono<List<Student>> getStudents() {
        return Mono.just(students);
    }
    
}
