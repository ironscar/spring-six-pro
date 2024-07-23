# Spring WebFlux R2DBC

## R2DBC

- R2DBC is the technology used to work with databases in reactive spring (as JDBC is in non-reactive)
- We add the `spring-boot-starter-data-r2dbc` dependency to pom
- In addition, we also need a driver for mysql compliant with R2DBC
- For this, we use the `r2dbc-mysql` artifact from `io.asyncer`
- Postgres on the other hand has official dependency support on `start.spring.io`
- Then we need to add the following properties under `spring.r2dbc`
  - `url`: database connection url
  - `username` and `password` for the database
  - once all these details are added, default context load test passes and startup also works
  - there is an `initialization-mode` property but its mostly useful for embedded DBs which we aren't using
- this stil creates an issue where every connection to DB fails on time of query due to unknown timezone
  - for this, we need to create a bean of type `ConnectionFactoryOptionsBuilderCustomizer`
  - we update the zone to UTC as done in `AppConfig`

- POJO classes are defined similar to hibernate with annotations for table and columns
- Repository classes are defined by extending from `ReactiveCrudRepository<T, Integer>`
  - this provides a few methods like `find`, `save` etc
  - we can also provide custom queries with the `@Query` annotation
  - we can use method naming conventions to have custom behavior as in [https://docs.spring.io/spring-data/r2dbc/docs/1.4.6/reference/html/#repositories.query-methods.query-creation]
    - for example naming method as `find<Operation><PojoName>By<prop1><conditionType><prop2>` like `findDistinctStudentByFirstNameOrLastName`
    - this allows specifying firstName and lastName as parameters, and search them with an OR condition
    
    
- it works if any one or both provided, but for none provided, it gives no results instead of all [CHECK]
- complete the update method DB integration [cHECK]


---

- Figure out how to add: [TODO]
  - complex queries and conditions in R2DBC
  - auth config in R2DBC

---
