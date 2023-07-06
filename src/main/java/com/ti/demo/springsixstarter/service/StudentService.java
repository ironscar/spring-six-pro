package com.ti.demo.springsixstarter.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ti.demo.domain.hibernate.annotated.Student;
import com.ti.demo.springsixstarter.dao.StudentDao;

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

    public void saveStudent(Student student) {
        studentDao.save(student);
    }

}
