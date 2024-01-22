# Spring WebFlux Security

## Reactive Security Config

- Spring reactive uses `ServerHttpSecurity` instead of `HttpSecurity`
  - its mostly similar to configure except for using `authorizeExchange`, `pathMatchers` and `anyExchange` instead
- By default, using `spring.security.user` properties will authenticate every API
- We can use in-memory user details by creating a bean of type `MapReactiveUserDetailsService` which implements `ReactiveUserDetailsService`
  - the setup is mostly similar to the `InMemoryUserDetailsManager`
- For now, it works even without the auth for the app3 endpoints but not for actuator [WHY]

---

- Figure out how to add: [TODO]
  - all types of http methods and request bodies
  - parallel calls with webclient
  - DB calls in R2JDBC
  - auth config in R2JDBC
  - use password directly from spring security context (its encoded in bcrypt and we dont have the actual password so it fails)

---
