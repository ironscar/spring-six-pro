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
  - Run `sudo docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -v /datadir/docker.cnf:/etc/mysql/conf.d/docker.cnf -v /datadir/mysql1:/var/lib/mysql --name mysql1 mysql`
    - The `-v` is another way of creating a volume and mapping it to a specific directory (we do this to maintain data in the volume)
    - It wasn't working with `512MB` of memory on the VM so increased it to `768MB` and now server starts and keeps running
      - the error said `inappropriate ioctl for device`
    - Troubleshoot connection from workbench on host to mysql container on VM
      - this works for root but not for other users due to two reasons
        - the mysql container default enables `skip-name-resolve` which also cannot be disabled from outside without overriding configurations
        - the mysql user needs to specify the hosts from which it can connect, for our case that is essentially all hosts `%`
        - this also failed the `GRANT` statement for similar reasons but using `%` works
        - more specifically, we should create two users, one for host `192.168.0.103` which is our actual host and one for `192.168.0.106` which is our stage app server
    - Connecting between actual container and mysql container timing out
      - the spring boot props aren't getting set from environment properties properly
        - because datasource resolution is happening before value resolution just like constructor happening before value resolution
        - so we set a datasource bean manually in a new configuration class
        - for passing the build, we create a test datasource bean in the same configuration class with random values for now, later we can update it with H2
        - we also need to mark active profile as `test` in `test/resources/applications.properties` and then choose between the datasource beans on the basis of this profile
      - even after props are set, there is a connection link failure between the two VMs
        - we use the IP to test out first as VM may not know what domain it is and `/etc/hosts` was not editable in the docker container
        - IPs work but in a cloud native world, IPs can change so we should eventually try to make it work with domain names as well [TODO]
  - We migrated to postgresql as a container
    - Run `sudo docker run -d -p 5432:5432 -e POSTGRES_PASSWORD=postgrespass -e PGDATA=/var/lib/postgresql/data/pgdata -v /datadir/postgresdb:/var/lib/postgresql/data --name postgresdb1 postgres:16.4-alpine3.20`

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
