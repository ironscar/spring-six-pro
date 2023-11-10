package com.ti.demo.springsixstarter.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<Student> getStudents(String fname, String lname) {
        return studentDao.getAll(fname, lname);
    }

    public Student getStudent(Integer id) {
        return studentDao.find(id);
    }

    public void saveStudent(Student student) {
        studentDao.save(student);
    }

    public void updateStudent(Integer studentId, Student updatedStudent) {
        studentDao.update(studentId, updatedStudent);
    }

    @Transactional
    public int updateLastNameInBulk(List<Integer> ids, String lname) {
        return studentDao.updateLastNameInBulk(ids, lname);
    }

    public void deleteStudentById(Integer id) {
        studentDao.deleteStudentById(id);
    }

    @Transactional
    public int deleteStudents(List<Integer> ids) {
        return studentDao.deleteStudents(ids);
    }

}
