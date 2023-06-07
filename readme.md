# Read Me First
The following was discovered as part of building this project:

* The original package name 'com.ti.demo.spring-six-starter' is invalid and this project uses 'com.ti.demo.springsixstarter' instead.

---

# Getting Started

## Spring Boot Actuator

- Use `management.endpoints.web.exposure.include` to expose actuator endpoints
  - Use `*` for all or else use comma delimited list like `health,info`
  - For info, we also need to add `management.info.env.enabled=true`
  - Then all `info.xxx` props will show up in the info response
- We can also use `management.endpoints.web.exposure.exclude` to exclude endpoints
- Just hitting `/actuator` gives all the available endpoints

## Spring Boot Security

- Adding the dependency will put all endpoints under login
- Default username is `user` and password gets generated in the console at startup
  - we can override this using `spring.security.user` properties
  - Spring security will auto-redirect to `/login` on browsers
  - You can also specify the username and password as basic auth on API requests
  - This will let you make the authenticated API requests
- We can create an `AppSecurityConfig` class to permit or authenticate specific requests using `SecurityFilterChain`
  - if you do this, spring security will not redirect to login and basic auth will also not work
  - disbaling this config class for now
- Once logged in, it will maintain credentials in session so use Incognito to test login

## Application props

- You can set logging levels for specific packages by `logging.level.package=LOGLEVEL`
  - LOGLEVEL can be `TRACE,DEBUG,INFO,WARN,ERROR,FATAL,OFF`
  - You can save logs using `logging.file.name` and `logging.file.path` properties

## ComponentScan

- by default, `@SpringBootApplication` scans everything under the currenct package recursively
- if we want to use other stuff from outside, we need `@ComponentScan`
- we can also directly add `scanBasePackages` inside `@SpringBootApplication` with a comma delimited list like `{"pkg1", "pkg2" ...}`

## Autowiring & Dependency injection

- Check the constructor, setter and field injection in `FirstController.java`

---

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.1.0/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.1.0/maven-plugin/reference/html/#build-image)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/3.1.0/reference/htmlsingle/#using.devtools)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/3.1.0/reference/htmlsingle/#appendix.configuration-metadata.annotation-processor)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.1.0/reference/htmlsingle/#web)
* [Thymeleaf](https://docs.spring.io/spring-boot/docs/3.1.0/reference/htmlsingle/#web.servlet.spring-mvc.template-engines)
* [Spring Security](https://docs.spring.io/spring-boot/docs/3.1.0/reference/htmlsingle/#web.security)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Handling Form Submission](https://spring.io/guides/gs/handling-form-submission/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)

