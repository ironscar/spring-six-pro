package com.ti.demo.springsixstarter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ti.demo.domain.reactive.Student;

import reactor.core.publisher.Mono;

@Service
public class StudentService {

    private static List<Student> students = new ArrayList<>();

    static {
        students.add(Student.builder().id(1).firstName("Nora").lastName("Barnes").email("nbarnes@hzn.com").build());
        students.add(Student.builder().id(2).firstName("Kora").lastName("Barnes").email("kbarnes@hzn.com").build());
    }

    public Mono<List<Student>> getStudents(String fname, String lname) {
        return Mono.just(students
            .stream()
            .filter(student -> {
                boolean fnameMatch = true;
                if (StringUtils.hasText(fname)) {
                    fnameMatch = student.getFirstName().equals(fname);
                }
                boolean lnameMatch = true;
                if (StringUtils.hasText(lname)) {
                    lnameMatch = student.getLastName().equals(lname);
                }
                return fnameMatch && lnameMatch;
            }).collect(Collectors.toList()));
    }

    public Mono<Student> getStudentById(Integer id) {
        if (id < 0) {
            return Mono.error(new IllegalArgumentException("id cannot be negative"));
        }
        Student student = students
            .stream()
            .filter(stud -> id.equals(stud.getId()))
            .findFirst()
            .orElse(null);
        return student == null ? Mono.error(new NoSuchElementException("No matching student")) : Mono.just(student);
    }

    public Mono<Void> saveStudent(Student newStudent) {
        if (!(StringUtils.hasText(newStudent.getFirstName()) && StringUtils.hasText(newStudent.getLastName()))) {
            return Mono.error(new IllegalArgumentException("Names cannot be null"));
        }
        newStudent.setId(students.size());
        students.add(newStudent);
        return Mono.empty();
    }

}
