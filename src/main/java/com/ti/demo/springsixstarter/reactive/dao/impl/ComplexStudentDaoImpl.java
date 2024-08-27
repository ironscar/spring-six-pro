package com.ti.demo.springsixstarter.reactive.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.ti.demo.domain.reactive.ComplexStudent;
import com.ti.demo.domain.reactive.Greeting;
import com.ti.demo.springsixstarter.reactive.dao.ComplexStudentDao;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ComplexStudentDaoImpl implements ComplexStudentDao {

    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String EMAIL = "email";
    private static final String ST_ID = "st_id";
    private static final String G_ID = "g_id";
    private static final String G_MSG = "g_msg";

    private static final String STD_GRT_JOIN_QUERY = """
        SELECT 
            s.id st_id,
            s.first_name first_name,
            s.last_name last_name,
            s.email email,
            g.id g_id,
            g.message g_msg
        FROM student s
        LEFT JOIN greeting g
        ON s.id = g.student_id
    """;

    private DatabaseClient dbClient;

    ComplexStudentDaoImpl(DatabaseClient dc) {
        dbClient = dc;
    }

    public static Mono<ComplexStudent> getComplexStudentMapping1(List<Map<String, Object>> rows) {
        return Mono.just(ComplexStudent.builder()
            .id(Integer.parseInt(rows.get(0).get(ST_ID).toString()))
            .firstName((String) rows.get(0).get(FIRST_NAME))
            .lastName((String) rows.get(0).get(LAST_NAME))
            .email((String) rows.get(0).get(EMAIL))
            .greetings(rows.stream().map(row -> getGreetingMapping1(row)).filter(Objects::nonNull).toList())
            .build());
    }

    public static Greeting getGreetingMapping1(Map<String, Object> row) {
        if (row.get(G_ID) != null) {
            return Greeting.builder()
                .id(Integer.parseInt(row.get(G_ID).toString()))
                .message((String) row.get(G_MSG))
                .build();
        }
        return null;
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
            .flatMap(row -> getComplexStudentMapping1(row));
    }

    @Override
    public Mono<ComplexStudent> findById(Integer id) {
        return dbClient.sql(String.format("%s WHERE s.id = :%s", STD_GRT_JOIN_QUERY, ST_ID))
            .bind(ST_ID, id)
            .fetch()
            .all()
            .bufferUntilChanged(result -> result.get(ST_ID))
            .flatMap(row -> getComplexStudentMapping1(row))
            .singleOrEmpty();
    }

    @Override
    public Mono<Long> saveGreetings(ComplexStudent student) {
        // build query
        String insertStub = String.format("""
            INSERT INTO greeting (message, student_id) 
            SELECT * FROM (%s) new_greets
            WHERE student_id in (
                SELECT id FROM student WHERE id = %d
            )""", 
            student.getGreetings()
                .stream()
                .map(greeting -> String.format(
                    "SELECT '%s' message, %d student_id", 
                    greeting.getMessage(), 
                    student.getId())
                ).reduce(null, (a, b) -> a == null 
                    ? b : String.format("%s UNION %s", a, b)), 
            student.getId());

        // run query
        return dbClient.sql(insertStub).fetch().rowsUpdated();
    }

}
