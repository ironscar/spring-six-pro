package com.ti.demo.springsixstarter.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.ti.demo.domain.reactive.Student;
import com.ti.demo.springsixstarter.reactive.dao.StudentDao;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StudentService {

    private StudentDao studentDao;

    StudentService(StudentDao sd) {
        studentDao = sd;
    }

    public Mono<List<Student>> getStudents(String fname, String lname) {
        Flux<Student> studFlux = studentDao.findStudentByFirstNameOrLastName(fname, lname);
        return studFlux.collectList();
    }

    public Mono<Student> getStudentById(Integer id) {
        if (id < 0) {
            return Mono.error(new IllegalArgumentException("id cannot be negative"));
        }
        // check how to do no matching exceptions
        return studentDao.findById(id);
    }

    public Mono<Void> saveStudent(Student newStudent) {
        if (!(StringUtils.hasText(newStudent.getFirstName()) && StringUtils.hasText(newStudent.getLastName()))) {
            return Mono.error(new IllegalArgumentException("Names cannot be null"));
        }
        studentDao.save(newStudent);
        return Mono.empty();
    }

    public Mono<Void> deleteStudents(List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Mono.error(new IllegalArgumentException("ids must not be empty"));
        }
        // check how to do no matching exceptions
        return studentDao.deleteAllById(ids);
    }

    public Mono<Void> updateStudent(Integer id, Student updatedDetails) {
        if (id == null || updatedDetails.getFirstName() == null || updatedDetails.getLastName() == null) {
            return Mono.error(new IllegalArgumentException("Names or Id cannot be null"));
        }
        // Student student = students
        //     .stream()
        //     .filter(stud -> id.equals(stud.getId()))
        //     .findFirst()
        //     .orElse(null);
        // if (student == null) {
        //     return Mono.error(new NoSuchElementException("id does not exist"));
        // }
        // student.setEmail(updatedDetails.getEmail());
        // student.setFirstName(updatedDetails.getFirstName());
        // student.setLastName(updatedDetails.getLastName());
        return Mono.empty();
    }

    public Mono<Void> updateStudents(List<String> ids, String lastName) {
        if (CollectionUtils.isEmpty(ids) || lastName == null) {
            return Mono.error(new IllegalArgumentException("ids or name must not be null"));
        }
        // int count = 0;
        // for (int i = 0 ; i < students.size() ; i++) {
        //     Student student = students.get(i);
        //     if (ids.contains(student.getId().toString())) {
        //         student.setLastName(lastName);
        //         count++;
        //     }
        //     if (count == ids.size()) {
        //         break;
        //     }
        // }
        // if (count == 0) {
        //     return Mono.error(new NoSuchElementException("ids don't exist"));
        // }
        return Mono.empty();
    }

}
