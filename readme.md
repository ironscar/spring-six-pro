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

### Hibernate annotated basics

- Specific classes for `StudentHbDao` and `StudentMbDao` are required for it to work with hibernate and mybatis respectively so recheck the imports in the `StudentService`
  - The `Student` class used also differs to recheck those imports in `StudentService` and `StudentController`
- we have clases mapped to tables and their fields mapped to columns
- `@Entity` requires a no-arg constructor so we use `@NoArgsConstructor` using lombok
- If the `@Entity` is not in same package as the current spring boot application class, we need to add the `@EntityScan` annotation and specify the packages where to find the entity classes
- `@Column`/`@Table` is optional and by default it uses the same name as the java variable/class respectively but better to be explicit
- `@GenerateValue` is used to specify that the key will be generated by DB and there are different strategies to it
- Often used along with `AUTO_INCREMENT` which takes the values where the indexing should start in the `CREATE` table command an can be udpated by `ALTER` table command
- Then we create a DAO interface and its implementation with the `@Repository` annotation and autowire the entity manager
  - When we can use the `persist` method of the entity manager to insert object into table as row
  - This also updates the reference of the object inserted to contain the id with which it was inserted if using generatedValue and autoIncrement
- We can also use `@Transactional` from spring-framework package to make the method transactional
  - we should specify these on the service layer as a best practice instead of dao layer
- To update single entity, we can first use entity manager to find them, update the values of the object and then calling `merge` method of the entity manager with new object to update it in DB
  - For multiple entities, refer to JPQL section
- We let the student class implement `Serializable` interface and specify a `serialVersionUID` so as to help with Serialization/Deserialization

### JPQL

- we can use `createQuery("FROM <entityname>")` to make more customized queries and get the results using `getResultList`
  - the entity name uses the name specified in `@Entity` or takes the class name (it is case-sensitive)
  - the field names are the member names in Java and not the column names in SQL so be very careful
- `createQuery` can use `WHERE, AND, OR, LIKE, ORDER BY` etc as well where params are specified as `:<paramname>` like `:pname`
- `createQuery` returns an object of type `TypedQuery`
- Params can be set using `TypedQuery.setParameter(paramName, value)` but should only be set if used
  - if we don't use `:name` then setting `:name` throws exception
- Setting these parameters are safer than directly setting in the string
- If setting parameters for `IN` clause for number column, we should use `List<Integer>` for the parameter type
- For updating multiple entities, we can use `createQuery("UPDATE <entityname> SET)` followed by `executeUpdate()`
- For delete, it works the same way where `entityManager.remove(Student)` is used to remove one record and `createQuery("DELETE FROM <entityname> WHERE)` followed by `executeUpdate()`
- Both update and delete using `executeUpdate` returns the number of records updated/deleted
- We can use `spring.jpa.hibernate.ddl-auto` to create/drop DB tables at app startup/shutdown automatically based on the entities available
  - values available include `create`, `create-only`, `drop`, `create-drop`, `none` etc
- Spring JPA has an interface called `JpaRepository<EntityType, PrimaryKeyType>` which provides ready methods like `findAll, findById, save, deleteById` etc
  - it also makes sure that save is transactional out of the box so we can remove `@Transactional` from the relevant service methods
- This paves the way for `spring-data-rest` which will scan the project for `JpaRepository` entities and automatically create endpoints like `GET, GET (id), POST, PUT, DELETE` with no new code
  - it just needs the dependency `spring-boot-data-rest` to be added to POM
  - the response for these endpoints include the actual data and metadata because its `HATEOAS` compliant

---

## Querying with MyBatis

### Mybatis XML SQL

- Add dependency `mybatis-spring-boot-starter`
- Add interface with `@Repository` to define the methods available and define `@Param(<paramName>)` for the params that should be available in mapper xml
- Under `resources`, create a director structure similar to the java interfaces and create an xml file with same name as interface
- In in the xml file, we can create resultMappers, select statements etc
  - top level element is `mapper` with attribute `namespace` and value is classpath of `StudentMbDao`
  - that can contain all other mybatis xml tags and elements
  - `resultMap` takes an `id` to refer to that mapper and `type` takes classpath of the result type
  - the above `id` of the mapper can be used in the `resultMap` attribute of `select` statements
  - `select` statements also take an `id`, whose value is the name of the interface method in java
  - here, all tables and columns use the exact name that is there in SQL but don't use semicolons
  - Parameters are generally used as `#{<paramName>}`
- Put `@MapperScan(basePackages = {<packageName>})` in the top-level Application file for all packages containing mapper interfaces for mybatis
- Used `@Transactional` in the DAO layer for the methods that weren't marked as transactional in service

---

## Exception handling

- We can throw `ResponseStatusException` with specific `HttpStatus` enum value and reason from controller methods as is done in `StudentController`
  - this includes trace but we can hide that by setting `server.error.include-stacktrace=never` in props
  - this would end up being on a per-method basis (like is done for `StudentController.getStudent`)
  - this property doesn't change how the `RestControllerAdvice` based method works
- we can also create a `ExceptionController` class with the `@RestControllerAdvice` annotation with all the exception handlers
  - Use a method with the `@ExceptionHandler` annotation to catch that exception here
  - Return the required error response object
  - The method takes an argument that defines the type of exception it handles
  - By default this will return status 200 but we can use `@ResponseStatus(HttpStatus.<Code>)` to set the correct status
  - `@RestControllerAdvice` is `@ControllerAdvice` + `@ResponseBody` so we would have to use `ResponseEntity<StudentErrorResponse>` as return type if using `ControllerAdvice` and set the status there instead of the `@ResponseStatus`

---

## Spring AOP

- AOP stands for `Aspect Oriented Programming`
- It helps with doing common things across multiple places without touching all those places thereby allowing cleaner code but may make the application flow hard to follow and have some performance impacts
  - `aspect` is the module of code for a cross-cutting concern (like logging for example)
  - `advice` is what action is taken and when it should be applied
      - `before`, `after`, `after return`, `after throwing`, `around` are different advice types that exist
      - `before` fires before execution
      - `after throwing` only fires if there is exception during execution and passes that exception to calling class after logic
      - `after return` only fires if no exceptions after execution
      - `after` fires regardless of exception or successful return after execution
      - `around` fires before and after method execution using a `proceeding join point`
        - a `proceeding join point` can be used to execute the method at a specific step in the aspect
        - generally executes after `before` and before `after` aspects regardless of `Order` annotation
        - aspect method must return result from `proceeding join point` for actual methods to work
        - doesn't execute past the `proceeding join point` if there is exception and you don't catch it
        - if you catch it, `after throwing` aspect will not trigger since the exception is already handled
  - `join point` is when to apply code during program execution
  - `point cut` is where advice should be applied
  - `weaving` connects aspects to target objects
    - it can be `compile-time`, `load-time` or `run-time`, the latter being the slowest
    - Spring uses run-time weaving AOP with only method-level join-points and only beans can have aspects
- We create an aspect using `@Aspect` and spring requires marking that class as a bean too
  - example aspects are in the `aspect` package using point-cut expressions, get method names, params, return vals, thrown exceptions etc
  - We can define common pointcut expressions which can later be resued or combined in `CommonPointcuts`
  - To define an order to how multiple aspects are applied, we have to create separate aspect beans for them and then use `@Order(x)` for each of them, like is done with `SecondOrderAspect.java`
    - If you have an `afterThrowing` aspect of order(1) and an `after` aspect of order(2), the latter still gets executed first

---

## Spring Reactive 

### Basics

- Spring Reactive requires `spring-boot-starter-webflux` dependency
- For reactive APIs, we need a `handler` which takes a request and creates a response
  - This is a component bean and defines the method taking a `ServerRequest` and generating a `Mono<ServerResponse>`
  - To send a value in body, we can use `BodyInserters.fromValue(obj)` where `obj` is the value
- We also need a `router` to specify what route does the response come from
  - This takes a handler as argument to use in a route
  - Define a single config class with a single bean of `RouterFunctions<ServerResponse>`
  - You can define multiple routes here with `RouterFunctions.route().andRoute()...`
  - Each route method internally takes the request predicates like api route, content type etc and the handler method reference
- If multiple handlers, create separate routers for each handler and all related routes can go inside the same router
- Check examples in package `springsixstarter.reactive`

### Reactive Security Config

- Spring reactive uses `ServerHttpSecurity` instead of `HttpSecurity`
  - its mostly similar to configure except for using `authorizeExchange`, `pathMatchers` and `anyExchange` instead
- By default, using `spring.security.user` properties will authenticate every API
- We can use in-memory user details by creating a bean of type `MapReactiveUserDetailsService` which implements `ReactiveUserDetailsService`
  - the setup is mostly similar to the `InMemoryUserDetailsManager`

### Reactive Controller

- Path vaiables can be specified in the url passed to `RequestPredicates.GET` in router as `{param}`
  - they can be accessed in handler as `request.pathVariable(<paramName>)`
  - they will always be string and cannot be type-checked
- Query parameters need not even be specified in the url
  - they can be accessed in handler as `request.queryParam(<paramName>)` which returns an optional
  - they will always be string and cannot be type-checked
- Multiple paths for same handler can be specified by 
  - `(RequestPredicates.GET(path1).or(RequestPredicates.GET(path2))).and(...)`
- For exception handling, we can either do it at a handler level or a global level

- For handler level, `Mono` has multiple error methods like `onErrorReturn`, `onErrorResume` etc
    - we can use `onErrorResume` at the end of the chain after the main server response piece
    - here we can use the exception object to return a server response with a custom exception body
    - whenever there is an error at any part of the chain, it will skip the chain till the next error handler and execute it
    - `[WHY - CHECK NOW]` it only seemed to work for web client usage but not for actual response return
      - for now, using try/catch but need to figure out how to use the reactive error operators

  - For global level, we need to create global error handler component
    - This `GlobalExceptionHandler` implements `WebExceptionHandler` interface and overrides its `handle` method
    - Its set to `Order(-2)` as the default exception handler has order 1 and we want to use this instead
    - Here we can check for the exception type using `instanceof` and handle it accordingly
    - Now `WebExceptionHandler` is low level so we have to deal directly in bytes
    - Thus we use `ObjectMapper` to create bytes of our error response body and write it to response
    - This throws exceptions but it shouldn't, so to be safe, we add an error message and convert that to bytes for a single error message if this ever happens

### Reactive Programming

- A `Mono` is a reactive type that can emit at most one element (something like a one-time observable)
  - It allows subscribing to and specify actions on emit, on error and on complete
- A `Flux` is a reactive type that can emit any number of elements (true observable)
  - It also allows subscribing to in a similar manner
- `flatMap` can be used to convert a `Mono<X>` to `Mono<Y>` by passing a custom lambda function which returns another `Mono`
  - they can be chained in sequence to keep converting from one `Mono` to another
- `onErrorMap` can be used on convert Mono exceptions of one type to another

- Figure out how to add: [TODO]
  - all types of http methods and request bodies
  - parallel calls with webclient
  - DB calls in MongoDB
  - auth config in MongoDB
  - use password directly from spring security context (its encoded in bcrypt and we dont have the actual password so it fails)

### Caveats

- You shouldn't have `web` and `webflux` dependencies in same project as webflux APIs silently fail with 404 in that case
  - we can still use `WebClient` in normal REST APIs though
  - commenting the dependency and adding `webflux` and `web` as profiles to select the specific security config for now to make it work as `HttpSecurity` bean is only added for `web` and not for `webflux`
  - the `custom-jdbc-security` won't work anymore as spring reactive does not support it out of the box
    - JDBC/JPA are blocking-type APIs and so spring reactive does not recommend using `JdbcUserDetailsManager`
    - It recommends using MongoDB/Redis
  - as a result, the default username and password can be added as `Basic Auth` while calling API unless overridden
- Even if `web` dependency is commented out, the web APIs still work, but with the webflux auth config

---

## Deep Dive Todo

- Spring Reactive
- NoSQL 1 (MongoDB)
- Service communication (RabbitMQ / Kafka / gRPC)
- API layer 1 (GraphQL / Sockets)
- NoSQL 2 (Redis / ScyllaDB / Neo4j)
- Caching (Redis Sentinels)
- API layer 2 (FTP / SMTP)
- Spring Cloud
- Spring Security advanced use cases
- Filters & Interceptors
- @Transactional use cases
- Jmeter for performance comparison of competing technologies
- Spring boot testing

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

