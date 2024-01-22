package com.ti.demo.springsixstarter.dao.mybatis.xmlsql;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ti.demo.domain.mybatis.xmlsql.Student;

@Repository
public interface StudentDao {

    List<Student> getAll(@Param("fname") String fname, @Param("lname") String lname);

    Student find(@Param("id") Integer id);
    
    @Transactional
    void save(@Param("student") Student student);

    @Transactional
    void update(@Param("id") Integer id, @Param("updatedStudent") Student updatedStudent);

    int updateLastNameInBulk(@Param("ids") List<Integer> ids, @Param("lname") String lname);

    @Transactional
    void deleteStudentById(@Param("id") Integer id);

    int deleteStudents(@Param("ids") List<Integer> ids);

}
