package com.ti.demo.springsixstarter.dao;

import java.util.List;

import com.ti.demo.domain.hibernate.annotated.Student;

public interface StudentDao {

    List<Student> getAll(String fname, String lname);

    Student find(Integer id);
    
    void save(Student student);

    void update(Integer id, Student updatedStudent);

    public int updateLastNameInBulk(List<Integer> ids, String lname);

}
