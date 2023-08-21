package com.ti.demo.springsixstarter.dao.hibernate.annotated;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ti.demo.domain.hibernate.annotated.Student;
import com.ti.demo.springsixstarter.dao.StudentDao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

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
    public List<Student> getAll(String fname, String lname) {
        String query = "FROM student";
        if (!fname.isEmpty()) {
            query += " WHERE firstName = :fname";
        }
        if (!lname.isEmpty()) {
            query += (fname.isEmpty() ? " WHERE " : " AND ") + "lastName = :lname";
        }
        query += " ORDER BY id DESC";
        TypedQuery<Student> typedQuery = entityManager.createQuery(query, Student.class);
        if (!fname.isEmpty()) {
            typedQuery.setParameter("fname", fname);
        }
        if (!lname.isEmpty()) {
            typedQuery.setParameter("lname", lname);
        }
        return typedQuery.getResultList();
    }

    @Override
    public Student find(Integer id) {
        return entityManager.find(Student.class, id);
    }

    @Override
    @Transactional
    public void save(Student student) {
        entityManager.persist(student);
    }

    @Override
    @Transactional
    public void update(Integer id, Student updatedStudent) {
        Student student = find(id);
        student.setFirstName(updatedStudent.firstName);
        student.setLastName(updatedStudent.lastName);
        student.setEmail(updatedStudent.email);
        entityManager.merge(student);
    }

    @Override
    @Transactional
    public int updateLastNameInBulk(List<Integer> ids, String lname) {
        return entityManager
            .createQuery("UPDATE student SET lastName = :lname WHERE id in (:ids)")
            .setParameter("lname", lname)
            .setParameter("ids", ids)
            .executeUpdate();
    }
    
}
