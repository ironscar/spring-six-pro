package com.ti.demo.springsixstarter.reactive.dao;

import com.ti.demo.domain.reactive.CustomUser;

import reactor.core.publisher.Mono;

public interface UserDao {

    public Mono<CustomUser> findByUserId(String userId);
    
}
