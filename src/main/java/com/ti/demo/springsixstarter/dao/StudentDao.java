package com.ti.demo.springsixstarter.dao;

import com.ti.demo.domain.hibernate.annotated.Student;

public interface StudentDao {
    
    void save(Student student);

}
