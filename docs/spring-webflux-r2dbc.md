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

### Quering alternatives

- `ReactiveCrudRepository` provides the following functionalities:
  - this provides a few methods like `find`, `save` etc
    - `save` doesn't automatically persist and needs to be subscribed to with `.subscribe()`
    - delete methods don't require subscribing
  - we can also provide custom select queries with the `@Query` annotation
  - we can use method naming conventions to have custom behavior
    - refer [https://docs.spring.io/spring-data/r2dbc/docs/1.4.6/reference/html/#repositories.query-methods.query-creation]
    - for example naming method as `find<Operation><PojoName>By<prop1><conditionType><prop2>` like `findDistinctStudentByFirstNameOrLastName`
    - this allows specifying firstName and lastName as parameters, and search them with an OR condition
    - but this specifically checks for the param values (even null), null doesn't imply skip condition
  - for quick simple queries, we can use this
- We can do queries directly using `DatabaseClient`:
  - we need to autowire the database client bean
  - we can specify the sql query string with parameters like `:<nameOfParam>` and then use a `.bind("<nameOfParam>", value)`
    - we can also use a `List` as a value if the parameter needs it like `in (:<listParamName>)`
    - but the entire query needs to be specified as string which makes it hard to maintain
      - we can use multi-line strings with Java 17 as `""" <multiline string content here> """`
  - this can do any type of query and not limited to selects
  - for more complex queries, we can use this

- add one example of `@Query` [CHECK]
- complex queries and conditions in R2DBC [CHECK]

---

- Figure out how to add: [TODO]
  - auth config in R2DBC

---
