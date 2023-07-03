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
- Marking a bean as primary makes sure that it gets chosen whenever you autowire a bean of that type even without qualifiers but only one can be primary
- Normally the qualifier ends up being the class name or method name but you can pass a custom one in `@Component`, `@Bean` etc tags

## Lazy initialization

- We can specify `@Lazy` on specific beans and they will only be created when required (autowiring it implies requiring it)
- We can also globally specify `spring.main.lazy-initialization=true` to specify that all beans should be lazily initialized without having to specify it on each
- May help with faster startup time but could also cause problems in identifying config issues

## Bean scopes

- By default, beans are singleton and only one is created in the entire spring container
- Other scopes include Request, Session, GlobalSession, WebSocket, Prototype, Application
- The `setPrototypeCoaches` shows this by autowiring two `PrototypeCoach` beans and updating their names separately
- Marking `PrototypeCoach` as prototype shows that both have different names but without it, they show the latest set name as they are the same instance
  - prototype beans don't call destroy method of `DisposableBean` and get garbage collected when they are no longer used but resources they hold must be explicitly cleaned up
  - these beans are also `Lazy` by default and so no need to speciy them as lazy explicitly
- Similarly, we can create a `RequestScopeCoach` marked with `@RequestScope` which has a scope of request and proxy of target class making it simpler to use
  - request scope bean, even if autowired into a singleton bean, only get created once the request happens
  - if request bean autowires a prototype bean, we can call the destroy method of the prototype bean overridden for `DisposableBean` interface in the request bean destroy method
  - `BeanPostProcessor` interface should not be used on anything but singleton beans, otherwise it prevents app startup
- Beans can also be created using the `@Bean` annotation inside configuration classes but the method must not be private or final
  - This can be done in non-configuration classes but is not recommended as it processes the bean in lite mode and can inject subtle bugs
  - This is useful when you want to create a bean out of a third-party object that is not inherently available as a bean

---

## SQL Querying with JPA

- for mysql, we need to start the mysql service as administrator and the root user password is set in `mysql-init.txt` file for logging in
- add the `data-jpa` and `mysql-connector-j` dependencies in pom
- the rest of the queries begin in `setup.sql` where we create a new user for our spring boot project, a database, a table etc.
- the datasource url is specified as `jdbc:mysql://{url}/{dbname}`
- having datasource url, username and password allows the connect to the database with specified user and creates beans for DataSource, EntityManager etc
- hibernate is used by default for spring boot jpa
- in hibernate.annotated
  - we have clases mapped to tables and their fields mapped to columns
  - `@Entity` requires a no-arg constructor so we use `@NoArgsConstructor` using lombok
  - `@Column`/`@Table` is optional and by default it uses the same name as the java variable/class respectively but better to be explicit
  - `@GenerateValue` is used to specify that the key will be generated by DB and there are different strategies to it

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

