# Read Me First
The following was discovered as part of building this project:

* The original package name 'com.ti.demo.spring-six-starter' is invalid and this project uses 'com.ti.demo.springsixstarter' instead.
* Each module to be learned is covered under a separate branch under `main/` with its docs under `docs/`
  - `main/spring-basics` covers the basics of beans, autowiring and actuator using a simple rest controller
  - `main/spring-web-basics` covers the basics of rest controllers and exception handling
  - `main/spring-web-hibernate` covers the basics of using hibernate with spring web for SQL queries
  - `main/spring-web-mybatis` covers the basics of using mybatis with spring web for SQL queries
  - `main/spring-web-security` covers the basics of spring security for web
  - `main/spring-aop` covers the basics of AOP in spring

---

## Deep Dive Todo

- Spring Reactive
- NoSQL (MongoDB / Redis / ScyllaDB / Neo4j)
- Service communication (RabbitMQ / Kafka / gRPC)
- API layer (GraphQL / Sockets / FTP / SMTP)
- Caching (Redis & Sentinels)
- Spring Cloud
- Spring Security advanced use cases
- Filters & Interceptors
- @Transactional use cases
- Jmeter for performance comparison of competing technologies
- Spring boot testing

---

## Containerization & Deployment details

- Jenkinsfile and Dockerfile details are similar to Container-demo project
- Ansible and Jenkins pretty much use the same setup apart from slave being used with jdk 17 now
- As for mysql, we will create a container on the `inventory/db` vm (which doesn't have user permissions for docker so we use it with sudo)
  - Run `sudo mkdir -p /datadir/mysql1`
  - Run `sudo docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -v /datadir/mysql1:/var/lib/mysql --name mysql1 mysql`
    - The `-v` is another way of creating a volume and mapping it to a specific directory (we do this to maintain data in the volume)
    - It wasn't working with `512MB` of memory on the VM so increased it to `768MB` and now server starts and keeps running
      - the error said `inappropriate ioctl for device`
    - Troubleshoot connection from workbench on host to mysql container on VM [TODO]
    - Troubleshoot connection from actual app container deployed by ansible on `app1` to mysql container on `db` [TODO]

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
