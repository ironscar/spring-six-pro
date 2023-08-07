package com.ti.demo.springsixstarter.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ti.demo.domain.hibernate.annotated.Student;
import com.ti.demo.springsixstarter.service.StudentService;

@RestController
@RequestMapping("/app2/student")
public class HibernateAnnotatedController {

    private StudentService studentService;

    /**
     * Method autowired by spring
     * 
     * @param ss - student service bean
     */
    public HibernateAnnotatedController(StudentService ss) {
        studentService = ss;
    }

    @GetMapping(value = {"", "/"})
    public List<Student> getStudents(
        @RequestParam(name = "fname", required = false, defaultValue = "") String fname,
        @RequestParam(name = "lname", required = false, defaultValue = "") String lname    
    ) {
        return studentService.getStudents(fname, lname);
    }

    @GetMapping(value = "/{id}")
    public Student getStudent(@PathVariable(name = "id") Integer id) {
        Student student = null;
        try {
            student = studentService.getStudent(id);
            if (student == null) {
                throw new NullPointerException("Student not found");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student with specified id not found");
        }
        return student;
    }

    @PostMapping(value = {"", "/"})
    public void saveStudent(@RequestBody Student student) {
        try {
            if (student != null) {
                studentService.saveStudent(student);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body cannot be null");
        }
    }

}
