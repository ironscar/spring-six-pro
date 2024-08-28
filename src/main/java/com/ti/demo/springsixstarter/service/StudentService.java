package com.ti.demo.springsixstarter.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ti.demo.domain.mybatis.xmlsql.Student;
import com.ti.demo.springsixstarter.aspect.custom.ExecutionStatsCustomAspect;
import com.ti.demo.springsixstarter.dao.mybatis.xmlsql.StudentDao;

/**
 * Need to update the imports for Student and StudentDao depending on hibernate or mybatis
 */
@Service
public class StudentService {

    private StudentDao studentDao;

    /**
     * Method autowired automatically by spring
     * 
     * @param sd - student dao bean
     */
    public StudentService(StudentDao sd) {
        studentDao = sd;
    }

    @ExecutionStatsCustomAspect
    public List<Student> getStudents(String fname, String lname) {
        return studentDao.getAll(fname, lname);
    }

    @ExecutionStatsCustomAspect
    public Student getStudent(Integer id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id must be a non-zero integer");
        }
        return studentDao.find(id);
    }

    @ExecutionStatsCustomAspect
    public void saveStudent(Student student) {
        studentDao.save(student);
    }

    @ExecutionStatsCustomAspect
    public void updateStudent(Integer studentId, Student updatedStudent) {
        studentDao.update(studentId, updatedStudent);
    }

    @Async
    @Transactional
    @ExecutionStatsCustomAspect
    public CompletableFuture<Integer> updateLastNameInBulk(List<Integer> ids, String lname) {
        int count = studentDao.updateLastNameInBulk(ids, lname);
        if (count == 0) {
            throw new IllegalArgumentException("no last names got updated");
        }
        return CompletableFuture.completedFuture(count);
    }

    @Async
    @Transactional
    @ExecutionStatsCustomAspect
    public CompletableFuture<Integer> updateFirstNameInBulk(List<Integer> ids, String fname) {
        int count = studentDao.updateFirstNameInBulk(ids, fname);
        if (count == 0) {
            throw new IllegalArgumentException("no first names got updated");
        }
        return CompletableFuture.completedFuture(count);
    }

    @ExecutionStatsCustomAspect
    public void deleteStudentById(Integer id) {
        studentDao.deleteStudentById(id);
    }

    @ExecutionStatsCustomAspect
    @Transactional
    public int deleteStudents(List<Integer> ids) {
        return studentDao.deleteStudents(ids);
    }

}
