package com.ti.demo.springsixstarter.reactive.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.ti.demo.domain.reactive.CustomUser;
import com.ti.demo.springsixstarter.reactive.dao.UserDao;

import reactor.core.publisher.Mono;

@Repository
public class UserDaoImpl implements UserDao {

    private DatabaseClient dbClient;

    UserDaoImpl(DatabaseClient dbc) {
        dbClient = dbc;
    }

    private static Mono<CustomUser> getCustomUserMapping(List<Map<String, Object>> rows) {
        return Mono.just(CustomUser.builder()
            .userId((String) rows.get(0).get("user_id"))
            .password((String) rows.get(0).get("pwd"))
            .enabled(Integer.parseInt((String) rows.get(0).get("enabled")) == 1)
            .age(Integer.parseInt((String) rows.get(0).get("age")))
            .roles(rows.stream().map(row -> (String) row.get("role")).toList())
            .build());
    }

    @Override
    public Mono<CustomUser> findByUserId(String userId) {
        return dbClient.sql("""
              SELECT 
                u.user_id user_id,
                u.pwd pwd,
                u.age,
                CASE
                    WHEN u.enabled = 'Y' THEN 1
                    ELSE 0
                END enabled,
                CONCAT('ROLE_', a.role) role
            FROM custom_users u
            JOIN custom_authorities a
            ON u.user_id = a.user_id
            WHERE u.user_id = :userId
        """)
        .bind("userId", userId)
        .fetch()
        .all()
        .bufferUntilChanged(result -> result.get("user_id"))
        .flatMap(row -> getCustomUserMapping(row))
        .singleOrEmpty();
    }

}
