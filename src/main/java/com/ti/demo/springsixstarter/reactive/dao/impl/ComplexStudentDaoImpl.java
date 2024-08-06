package com.ti.demo.springsixstarter.reactive.dao.impl;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;

import com.ti.demo.domain.reactive.ComplexStudent;
import com.ti.demo.springsixstarter.reactive.dao.ComplexStudentDao;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ComplexStudentDaoImpl implements ComplexStudentDao {

    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String ST_ID = "st_id";
    private static final String STD_GRT_JOIN_QUERY = """
        SELECT 
            s.id st_id,
            s.first_name first_name,
            s.last_name last_name,
            s.email email,
            g.id g_id,
            g.message g_msg
        FROM student s
        JOIN greeting g
        ON s.id = g.student_id
    """;

    private DatabaseClient dbClient;

    ComplexStudentDaoImpl(DatabaseClient dc) {
        dbClient = dc;
    }

    @Override
    public Flux<ComplexStudent> findAll(String firstName, String lastName) {
        return dbClient.sql(String.format(
                "%s WHERE s.first_name = :%s OR s.last_name = :%s", STD_GRT_JOIN_QUERY, FIRST_NAME, LAST_NAME
            )).bind(FIRST_NAME, firstName)
            .bind(LAST_NAME, lastName)
            .fetch()
            .all()
            .bufferUntilChanged(result -> result.get(ST_ID))
            .flatMap(ComplexStudent::getComplexStudentMapping1);
    }

    @Override
    public Mono<ComplexStudent> findById(Integer id) {
        return dbClient.sql(String.format("%s WHERE s.id = :%s", STD_GRT_JOIN_QUERY, ST_ID))
            .bind(ST_ID, id)
            .fetch()
            .all()
            .bufferUntilChanged(result -> result.get(ST_ID))
            .flatMap(ComplexStudent::getComplexStudentMapping1)
            .singleOrEmpty();
    }

}
