package com.ti.demo.springsixstarter.service.auth;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.ti.demo.domain.reactive.auth.CustomUserPrincipal;
import com.ti.demo.springsixstarter.reactive.dao.UserDao;

import reactor.core.publisher.Mono;

@Service("customUserDetailsService")
public class CustomUserDetailsService implements ReactiveUserDetailsService {

    private UserDao userDao;

    CustomUserDetailsService(UserDao ud) {
        userDao = ud;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userDao.findByUserId(username).map(CustomUserPrincipal::new);
    }

}
