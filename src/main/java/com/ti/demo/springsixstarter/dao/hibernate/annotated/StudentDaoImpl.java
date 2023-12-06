package com.ti.demo.springsixstarter.dao.hibernate.annotated;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ti.demo.domain.hibernate.annotated.Student;
import com.ti.demo.springsixstarter.dao.StudentDao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

@Repository(value = "hibernateAnnotatedStudentDao")
public class StudentDaoImpl implements StudentDao {

    private EntityManager entityManager;

    private StudentBasicJpaRepository studentRepository;

    /**
     * Constructor which gets autowired with the entity manager
     * since no other parameterized constructors, this is used and doesnt need the annotation
     */
    public StudentDaoImpl(EntityManager em, StudentBasicJpaRepository srep) {
        entityManager = em;
        studentRepository = srep;
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
        Optional<Student> result = studentRepository.findById(id);
        return result.isPresent() ? result.get() : null;
    }

    @Override
    public void save(Student student) {
        studentRepository.save(student);
    }

    @Override
    public void update(Integer id, Student updatedStudent) {
        Student student = find(id);
        if (student != null) {
            student.setFirstName(updatedStudent.firstName);
            student.setLastName(updatedStudent.lastName);
            student.setEmail(updatedStudent.email);
            studentRepository.save(student);
        }
    }

    @Override
    public int updateLastNameInBulk(List<Integer> ids, String lname) {
        return entityManager
            .createQuery("UPDATE student SET lastName = :lname WHERE id in (:ids)")
            .setParameter("lname", lname)
            .setParameter("ids", ids)
            .executeUpdate();
    }

    @Override
    public void deleteStudentById(Integer id) {
        studentRepository.deleteById(id);
    }

    @Override
    public int deleteStudents(List<Integer> ids) {
        return entityManager
            .createQuery("DELETE FROM student WHERE id IN (:ids)")
            .setParameter("ids", ids)
            .executeUpdate();
    }

}
