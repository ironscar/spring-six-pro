# Spring Web Security

## Spring Boot Security

- Adding the dependency will put all endpoints under login
- Default username is `user` and password gets generated in the console at startup
  - we can override this using `spring.security.user` properties
  - Spring security will auto-redirect to `/login` on browsers
  - You can also specify the username and password as basic auth on API requests
  - This will let you make the authenticated API requests
- We can create an `AppSecurityConfig` class to permit or authenticate specific requests using `SecurityFilterChain`
  - if you do this, spring security will not redirect to login and basic auth will also not work
  - disabling the authorization piece of the config class for now
- Once logged in, it will maintain credentials in session so use Incognito to test login
- This basic authentication will work for GET requests but not for others due to csrf token requirements
  - for now lets disable csrf for local use in `AppSecurityConfig`
- We can add user roles here in app or by jdbc, first lets take in app
  - Using `inMemoryUserDetailsManager`
  - The password is specified as `{noop}<password>` where the `{noop}` specifies the encoding algorithm id, in this case its plaintext
  - We can add requestMatchers in the filterChain to control which paths are secured for what role and which are not
  - Doing this will override the username/password props we set in `application.properties` and only the in-memory creds will work
- Next up is adding users in jdbc
  - This requires the `spring-boot-starter-jdbc` dependency (unless `spring-boot-starter-data-jpa` is already included)
  - Spring security defines a table structure which it can directly pull user data from
    - The default structure includes:
      - a `users` table with columns `username`, `password` and `enabled`
      - an `authorities` table with columns `username` and `authority` (roles)
      - `username` in `authorities` is an FK to `users` table
      - the `authority` columm takes the role with `ROLE_` as spring security expects but doesn't automatically do like it does for in-memory
    - Then we create a new `UserDetailsManager` bean and return a `JdbcUserDetailsManager` mean with the autowired `DataSource` instance
    - For this, we will use `{bcrypt}` instead of `{noop}` which specifies encrypted by bcrypt, which is how we should store passwords
      - We generate the bcrypt password and prefix it with `{bcrypt}` and store it in DB, everything else works the same
  - You can define your custom table structure but then you need to write additional logic for pulling the data 
    - we create two new tables with similar data `custom_users` and `custom_authorities`
    - We need to set two properties like SQL statements on the `JdbcUserDetailsManager` which are `setUsersByUsernameQuery` and `setAuthoritiesByUsernameQuery`
    - The `?` in the statement specifies the parameter that is injected later
    - Notice that we append `ROLE_` here instead of in table to make it similar to in-memory
  - Finally, we can get the details of logged in users in endpoints using `@AuthenticationPrincipal` on an argument
    - Type of argument is `org.springframework.security.core.userdetails.User` as used in `StudentController.getStudents`

---

## Caveats

- Security setup sometimes doesn't work if you have a jar running inside a docker container
- This happens due to external jars which can be found on system by default but not on docker image
- To address this, we add the following to `spring-boot-maven-plugin`

```
<includeSystemScope>true</includeSystemScope>
```

---
