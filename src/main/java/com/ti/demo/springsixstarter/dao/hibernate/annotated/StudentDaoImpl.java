package com.ti.demo.springsixstarter.dao.hibernate.annotated;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ti.demo.domain.hibernate.annotated.Student;
import com.ti.demo.springsixstarter.dao.StudentDao;

import jakarta.persistence.EntityManager;

@Repository(value = "hibernate-annotated-student-dao")
public class StudentDaoImpl implements StudentDao {

    private EntityManager entityManager;

    /**
     * Constructor which gets autowired with the entity manager
     * since no other parameterized constructors, this is used and doesnt need the annotation
     */
    public StudentDaoImpl(EntityManager em) {
        entityManager = em;
    }

    @Override
    @Transactional
    public void save(Student student) {
        entityManager.persist(student);
    }
    
}
