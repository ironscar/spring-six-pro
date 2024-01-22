# Spring WebFlux Security

## Reactive Security Config

- Spring reactive uses `ServerHttpSecurity` instead of `HttpSecurity`
  - its mostly similar to configure except for using `authorizeExchange`, `pathMatchers` and `anyExchange` instead
- By default, using `spring.security.user` properties will authenticate every API
- We can use in-memory user details by creating a bean of type `MapReactiveUserDetailsService` which implements `ReactiveUserDetailsService`
  - the setup is mostly similar to the `InMemoryUserDetailsManager`

---
