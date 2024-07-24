package com.ti.demo.springsixstarter.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.ti.demo.domain.reactive.Student;
import com.ti.demo.springsixstarter.reactive.dao.StudentDao;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StudentService {

    private DatabaseClient dbClient;

    private StudentDao studentDao;

    StudentService(StudentDao sd, DatabaseClient dc) {
        studentDao = sd;
        dbClient = dc;
    }

    public Mono<List<Student>> getStudents(String fname, String lname) {
        Flux<Student> studFlux = studentDao.findStudentByFirstNameOrLastName(fname, lname);
        return studFlux.collectList();
    }

    public Mono<Student> getStudentById(Integer id) {
        if (id < 0) {
            return Mono.error(new IllegalArgumentException("id cannot be negative"));
        }
        return studentDao.findById(id).switchIfEmpty(Mono.error(new NoSuchElementException("student not found")));
    }

    public Mono<Void> saveStudent(Student newStudent) {
        if (!(StringUtils.hasText(newStudent.getFirstName()) && StringUtils.hasText(newStudent.getLastName()))) {
            return Mono.error(new IllegalArgumentException("Names cannot be null"));
        }
        studentDao.save(newStudent).subscribe();
        return Mono.empty();
    }

    public Mono<Void> deleteStudents(List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Mono.error(new IllegalArgumentException("ids must not be empty"));
        }
        return studentDao.deleteAllById(ids);
    }

    public Mono<Void> updateStudent(Integer id, Student updatedDetails) {
        if (id == null || updatedDetails.getFirstName() == null || updatedDetails.getLastName() == null) {
            return Mono.error(new IllegalArgumentException("Names or Id cannot be null"));
        }
        return dbClient.sql(
            """
                UPDATE student SET 
                    first_name=:fname, 
                    last_name=:lname, 
                    email=:email 
                WHERE id = :id
            """)
            .bind("fname", updatedDetails.getFirstName())
            .bind("lname", updatedDetails.getLastName())
            .bind("email", updatedDetails.getEmail())
            .bind("id", id.toString())
            .fetch()
            .rowsUpdated()
            .flatMap(count -> {
                if (count == 0L) {
                    return Mono.error(new NoSuchElementException("id doesn't exist"));
                }
                return Mono.empty();
            });
    }

    public Mono<Void> updateStudents(List<String> ids, String lastName) {
        if (CollectionUtils.isEmpty(ids) || lastName == null) {
            return Mono.error(new IllegalArgumentException("ids or name must not be null"));
        }
        return dbClient.sql(
            """
                UPDATE student SET 
                    last_name=:lname 
                WHERE id in (:ids)
            """)
            .bind("lname", lastName)
            .bind("ids", ids)
            .fetch()
            .rowsUpdated()
            .flatMap(count -> {
                if (count == 0L) {
                    return Mono.error(new NoSuchElementException("id doesn't exist"));
                }
                return Mono.empty();
            });
    }

}
