# Spring WebFlux Security

## Reactive Security Config

- Spring reactive uses `ServerHttpSecurity` instead of `HttpSecurity`
  - its mostly similar to configure except for using `authorizeExchange`, `pathMatchers` and `anyExchange` instead
- By default, using `spring.security.user` properties will authenticate every API
- We can use in-memory user details by creating a bean of type `MapReactiveUserDetailsService` which implements `ReactiveUserDetailsService`
  - the setup is mostly similar to the `InMemoryUserDetailsManager`
- When we don't pass authentication information, the webflux endpoints tend to return 200 with no body under some cases
  - when `anyExchange` in security config is configured to be `permitAll`
  - so instead we want to specifically permit for all non-auth endpoints and then do `anyExchange.authenticated()`
  - authenticated endpoints can continue defining roles and then it will return 401 if no auth, 403 if wrong role and 200 if correct role

- As for using passwords directly out of Spring, it is not recommended by the Spring team
- Passwords are always stored and accessible only in the encoded format so there is no way to reuse the password based on user even if the current call is authenticated
- As a result, we will create a new internal user that will have all roles and will be used for all webclient calls
- The actual APIs that use webClient will still be authorized based on the existing non-intenral users
- This internal user will only be used by the application and no actual client

---

## Reactive CRUD operations

- Implemented the main Getter for students

---

## Caveats

- Security setup sometimes doesn't work if you have a jar running inside a docker container
- This happens due to external jars which can be found on system by default but not on docker image
- To address this, we add the following to `spring-boot-maven-plugin` in `pom.xml` plugins

```
<includeSystemScope>true</includeSystemScope>
```

---

- Figure out how to add: [TODO]
  - parallel calls with webclient
  - DB calls in R2JDBC
  - auth config in R2JDBC

---
