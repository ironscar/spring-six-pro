package com.ti.demo.springsixstarter.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ti.demo.domain.mybatis.xmlsql.Student;
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

    public List<Student> getStudents(String fname, String lname) {
        return studentDao.getAll(fname, lname);
    }

    public Student getStudent(Integer id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id must be a non-zero integer");
        }
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
