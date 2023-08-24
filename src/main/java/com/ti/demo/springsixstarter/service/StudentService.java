package com.ti.demo.springsixstarter.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ti.demo.domain.hibernate.annotated.Student;
import com.ti.demo.springsixstarter.dao.StudentDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StudentService {

    private StudentDao studentDao;

    /**
     * Method autowired automatically by spring
     * 
     * @param sd - student dao bean from hibernate annotated package
     */
    public StudentService(@Qualifier("hibernate-annotated-student-dao") StudentDao sd) {
        studentDao = sd;
    }

    public List<Student> getStudents(String fname, String lname) {
        log.info("Get all students with fname: {}, lname: {}", fname, lname);
        return studentDao.getAll(fname, lname);
    }

    public Student getStudent(Integer id) {
        log.info("Find student for id {}", id);
        return studentDao.find(id);
    }

    public void saveStudent(Student student) {
        studentDao.save(student);
        log.info("Save student {}", student);
    }

    public void updateStudent(Integer studentId, Student updatedStudent) {
        studentDao.update(studentId, updatedStudent);
        log.info("Update student: {}", updatedStudent);
    }

    @Transactional
    public int updateLastNameInBulk(List<Integer> ids, String lname) {
        int x = studentDao.updateLastNameInBulk(ids, lname);
        log.info("{} students updated", x);
        return x;
    }

    public void deleteStudentById(Integer id) {
        log.info("Deleteing student with id {}", id);
        studentDao.deleteStudentById(id);
    }

    @Transactional
    public int deleteStudents(List<Integer> ids) {
        int x = studentDao.deleteStudents(ids);
        log.info("{} students deleted", x);
        return x;
    }

}