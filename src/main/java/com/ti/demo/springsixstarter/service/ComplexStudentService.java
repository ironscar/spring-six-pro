package com.ti.demo.springsixstarter.service;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ti.demo.domain.mybatis.xmlsql.Student;

@Service
public class ComplexStudentService {

    private StudentService studentService;

    public ComplexStudentService(StudentService studentService) {
        this.studentService = studentService;
    }

    @Transactional
    public Integer asyncTransactionOperation() throws InterruptedException {
        try {
            Student student1 = Student.builder()
                .firstName("F1")
                .lastName("L1")
                .email("E1")
                .build();
            
            // insert the student
            studentService.saveStudent(student1);

            // update first name in bulk for this specific student
            CompletableFuture<Integer> c1 = studentService
                .updateFirstNameInBulk(Arrays.asList(19), "Lana2");

            // update last name in bulk for this specific student
            CompletableFuture<Integer> c2 = studentService
                .updateLastNameInBulk(Arrays.asList(18), "L2");

            return c1.get() + c2.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new InterruptedException(e.getMessage());
        }
    }

}
