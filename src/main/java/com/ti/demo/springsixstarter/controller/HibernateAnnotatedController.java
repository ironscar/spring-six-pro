package com.ti.demo.springsixstarter.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ti.demo.domain.hibernate.annotated.Student;
import com.ti.demo.springsixstarter.service.StudentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
            log.error(e.getMessage());
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
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body cannot be null");
        }
    }

    @PutMapping(value = "/{id}")
    public void updatedStudent(@PathVariable("id") Integer id, @RequestBody Student updatedStudent) {
        try {
            if (updatedStudent != null && id != null) {
                updatedStudent.setId(id);
                studentService.updateStudent(id, updatedStudent);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body and id cannot be null");
        }
    }

    @PutMapping(value = {"","/"})
    public void updateStudents(
        @RequestParam(value = "ids", required = true) String ids,
        @RequestParam(value = "lname", required = true) String lname) {
        try {
            List<Integer> actualIds = Arrays.asList(ids.split(","))
                .stream()
                .map(id -> Integer.parseInt(id))
                .collect(Collectors.toList());
            studentService.updateLastNameInBulk(actualIds, lname);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ids must be valid list of integers");
        }
    }

    @DeleteMapping(value = "/{id}")
    public void deleteStudentById(@PathVariable("id") Integer id) {
        try {
            studentService.deleteStudentById(id);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id must be a valid integer");
        }
    }

    @DeleteMapping(value = {"", "/"})
    public void deleteStudents(@RequestBody List<Integer> ids) {
        try {
            if (!CollectionUtils.isEmpty(ids)) {
                studentService.deleteStudents(ids);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id must be a valid integer");
        }
    }

}
