package com.ti.demo.springsixstarter.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.ti.demo.domain.reactive.ComplexStudent;
import com.ti.demo.domain.reactive.Student;
import com.ti.demo.springsixstarter.reactive.dao.ComplexStudentDao;
import com.ti.demo.springsixstarter.reactive.dao.StudentDao;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StudentService {

    private DatabaseClient dbClient;

    private StudentDao studentDao;

    private ComplexStudentDao complexStudentDao;

    StudentService(StudentDao sd, ComplexStudentDao csd, DatabaseClient dc) {
        studentDao = sd;
        complexStudentDao = csd;
        dbClient = dc;
    }

    public Mono<List<Student>> getStudents(String fname, String lname, boolean useCustom) {
        Flux<Student> studFlux = useCustom 
            ? studentDao.getStudentsByCustomQuery(fname, lname)
            : studentDao.findStudentByFirstNameOrLastName(fname, lname);
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
            .bind("id", id)
            .fetch()
            .rowsUpdated()
            .flatMap(count -> {
                if (count == 0L) {
                    return Mono.error(new NoSuchElementException("id doesn't exist"));
                }
                return Mono.empty();
            });
    }

    public Mono<Void> updateStudents(List<String> ids, String lastName, boolean useCustom) {
        if (CollectionUtils.isEmpty(ids) || lastName == null) {
            return Mono.error(new IllegalArgumentException("ids or name must not be null"));
        }

        Mono<Long> m = useCustom
            ? studentDao.updateStudentsByCustomLastNameQuery(ids, lastName)
            : dbClient.sql(
                """
                    UPDATE student SET 
                        last_name=:lname 
                    WHERE id in (:ids)
                """)
                .bind("lname", lastName)
                .bind("ids", ids)
                .fetch()
                .rowsUpdated();
        
        return m.flatMap(count -> {
            if (count == 0L) {
                return Mono.error(new NoSuchElementException("id doesn't exist"));
            }
            return Mono.empty();
        });
    }

    public Mono<List<ComplexStudent>> getComplexStudents(String firstName, String lastName) {
        return complexStudentDao.findAll(firstName, lastName).collectList();
    }

    public Mono<ComplexStudent> getComplexStudentById(Integer id) {
        if (id < 0) {
            return Mono.error(new IllegalArgumentException("id cannot be negative"));
        }
        return complexStudentDao.findById(id);
    }

    public Mono<Void> saveGreetingsForComplexStudent(ComplexStudent student) {
        if (student == null || CollectionUtils.isEmpty(student.getGreetings())) {
            return Mono.error(new IllegalArgumentException("no greetings to insert"));
        }
        complexStudentDao.saveGreetings(student).log().subscribe();
        return Mono.empty();
    }

}
