package com.ti.demo.springsixstarter.dao.hibernate.annotated;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ti.demo.domain.hibernate.annotated.Student;

/**
 * Provides ready implementations for basic CRUD operations like findAll, findById etc
 */
public interface StudentBasicJpaRepository extends JpaRepository<Student, Integer> {}
