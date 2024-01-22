package com.ti.demo.springsixstarter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ti.demo.domain.Student;

/**
 * Need to update the imports for Student and StudentDao depending on hibernate or mybatis
 */
@Service
public class StudentService {

    private List<Student> students = new ArrayList<>();

    public List<Student> getStudents(String fname, String lname) {
        return students.stream().filter(student -> fname.equals(student.getFirstName()) && lname.equals(student.getLastName())).collect(Collectors.toList());
    }

    public Student getStudent(Integer id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id must be a non-zero integer");
        }
        return students.stream().filter(student -> id == student.getId()).findFirst().get();
    }

    public void saveStudent(Student student) {
        student.setId(students.size());
        students.add(student);
    }

    public void updateStudent(Integer studentId, Student updatedStudent) {
        students.set(studentId, updatedStudent);
    }
    
    public void updateLastNameInBulk(List<Integer> ids, String lname) {
        for (int i = 0 ; i < ids.size(); i++) {
            int id = ids.get(i);
            Student toUpdatesStudent = students.get(id);
            toUpdatesStudent.setLastName(lname);
        }
    }

    public void deleteStudentById(Integer id) {
        students.remove(id);
    }

    public void deleteStudents(List<Integer> ids) {
        for (int i = 0 ; i < ids.size(); i++) {
            int id = ids.get(i);
            students.remove(id);
        }
    }

}
