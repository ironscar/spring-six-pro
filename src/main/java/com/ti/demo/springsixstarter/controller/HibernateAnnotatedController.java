package com.ti.demo.springsixstarter.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping(value = {"", "/"})
    public void saveStudent(@RequestBody Student student) {
        if (student != null) {
            studentService.saveStudent(student);
        }
    }

}
