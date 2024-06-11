package com.ti.demo.springsixstarter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.ti.demo.domain.reactive.Student;

import reactor.core.publisher.Mono;

@Service
public class StudentService {

    private static List<Student> students = new ArrayList<>();

    static {
        students.add(Student.builder().id(1).firstName("Nora").lastName("Barnes").email("nbarnes@hzn.com").build());
        students.add(Student.builder().id(2).firstName("Kora").lastName("Barnes").email("kbarnes@hzn.com").build());
        students.add(Student.builder().id(3).firstName("Dora").lastName("Bell").email("dbell@hzn.com").build());
        students.add(Student.builder().id(4).firstName("Christia").lastName("Bell").email("cbell@hzn.com").build());
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
        int newIndex = !students.isEmpty() ? students.get(students.size()-1).getId() + 1 : 1;
        newStudent.setId(newIndex);
        students.add(newStudent);
        return Mono.empty();
    }

    public Mono<Void> deleteStudents(List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Mono.error(new IllegalArgumentException("ids must not be empty"));
        }
        if (!students.removeIf(student -> ids.contains(student.getId()))) {
            return Mono.error(new NoSuchElementException("no students found"));
        }
        return Mono.empty();
    }

    public Mono<Void> updateStudent(Integer id, Student updatedDetails) {
        if (id == null || updatedDetails.getFirstName() == null || updatedDetails.getLastName() == null) {
            return Mono.error(new IllegalArgumentException("Names or Id cannot be null"));
        }
        Student student = students
            .stream()
            .filter(stud -> id.equals(stud.getId()))
            .findFirst()
            .orElse(null);
        if (student == null) {
            return Mono.error(new NoSuchElementException("id does not exist"));
        }
        student.setEmail(updatedDetails.getEmail());
        student.setFirstName(updatedDetails.getFirstName());
        student.setLastName(updatedDetails.getLastName());
        return Mono.empty();
    }

    public Mono<Void> updateStudents(List<String> ids, String lastName) {
        if (CollectionUtils.isEmpty(ids) || lastName == null) {
            return Mono.error(new IllegalArgumentException("ids or name must not be null"));
        }
        int count = 0;
        for (int i = 0 ; i < students.size() ; i++) {
            Student student = students.get(i);
            if (ids.contains(student.getId().toString())) {
                student.setLastName(lastName);
                count++;
            }
            if (count == ids.size()) {
                break;
            }
        }
        if (count == 0) {
            return Mono.error(new NoSuchElementException("ids don't exist"));
        }
        return Mono.empty();
    }

}
