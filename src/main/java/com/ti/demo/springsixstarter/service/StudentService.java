package com.ti.demo.springsixstarter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ti.demo.domain.Student;

/**
 * Need to update the imports for Student and StudentDao depending on hibernate or mybatis
 */
@Service
public class StudentService {

    private static List<Student> students = new ArrayList<>();

    public List<Student> getStudents(String fname, String lname) {
        return students
            .stream()
            .filter(student -> 
                (!StringUtils.hasText(lname) || lname.equals(student.getLastName())) && 
                (!StringUtils.hasText(fname) || fname.equals(student.getFirstName()))
            ).collect(Collectors.toList());
    }

    public Student getStudent(Integer id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id must be a non-zero integer");
        }
        Optional<Student> studentOpt = students.stream().filter(student -> id.equals(student.getId())).findFirst();
        return studentOpt.isPresent() ? studentOpt.get() : null;
    }

    public void saveStudent(Student student) {
        if (!StringUtils.hasText(student.getFirstName()) || !StringUtils.hasText(student.getLastName())) {
            throw new IllegalArgumentException("Names cannot be empty");
        }
        student.setId(students.size() + 1);
        students.add(student);
    }

    public void updateStudent(Integer studentId, Student updatedStudent) {
        if (!StringUtils.hasText(updatedStudent.getFirstName()) || !StringUtils.hasText(updatedStudent.getLastName())) {
            throw new IllegalArgumentException("Names cannot be null");
        }
        students.stream()
            .filter(student -> studentId.equals(student.getId()))
            .forEach(student -> {
                student.setEmail(updatedStudent.getEmail());
                student.setFirstName(updatedStudent.getFirstName());
                student.setLastName(updatedStudent.getLastName());
            });
    }
    
    public void updateLastNameInBulk(List<Integer> ids, String lname) {
        if (!StringUtils.hasText(lname)) {
            throw new IllegalArgumentException("Names cannot be null");
        }
        students.stream()
            .filter(student -> ids.contains(student.getId()))
            .forEach(student -> student.setLastName(lname));
    }

    public void deleteStudentById(Integer id) {
        students.removeIf(student -> id.equals(student.getId()));
    }

    public void deleteStudents(List<Integer> ids) {
        for (Integer id : ids) {
            students.removeIf(student -> id.equals(student.getId()));
        }
    }

}
