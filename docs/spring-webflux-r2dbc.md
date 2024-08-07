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
- Repository classes are defined by extending from `R2dbcRepository<T, Integer>`

### Quering alternatives

- `R2dbcRepository` provides the following functionalities:
  - this provides a few methods like `find`, `save` etc
    - `save` doesn't automatically persist and needs to be subscribed to with `.subscribe()`
    - delete methods don't require subscribing
  - we can also provide custom queries with the `@Query` annotation
    - update/insert/delete need the `@Modifying` annotation on the method as well
  - we can use method naming conventions to have custom behavior
    - refer https://docs.spring.io/spring-data/relational/reference/r2dbc/query-methods.html
    - for example naming method as `find<Operation><PojoName>By<prop1><conditionType><prop2>` like `findDistinctStudentByFirstNameOrLastName`
    - this allows specifying firstName and lastName as parameters, and search them with an OR condition
    - but this specifically checks for the param values (even null), null doesn't imply skip condition
  - for quick simple queries that can be written using one constant string, we can use this
- We can do queries directly using `DatabaseClient`:
  - we need to autowire the database client bean
  - for more complex queries, that may require manipulating the sql string, we can use this
- for both of the above, 
  - we can specify the sql query string with parameters like `:<nameOfParam>` and then use a `.bind("<nameOfParam>", value)`
  - we can also use a `List` as a value if the parameter needs it like `in (:<listParamName>)`
  - but the entire query needs to be specified as string which makes it hard to maintain
  - we can use multi-line strings with Java 17 as `""" <multiline string content here> """`

### Joins

- R2DBC is not an ORM and so doesn't support joins out of the box
- When we add a new field which has no mapping in existing DAO implementations using out-of-the-box
  - then those DAO methods will start failing as it doesn't know how to map that field
  - so if we need relationships, we cannot use the out-of-the-box implementations available
- So we would create our own interface for the DAO and then a class to implement those specifically
  - in addition, we will also define the mappings of the relationships for each entity ourselves
  - we will use the `DatabaseClient` based querying as that gives us the most flexibility
  - we use `.fetch().all()` to get all results
  - we use `bufferUntilChanged` to group all rows of that specific type into one flux emit
    - this is also used for `findById` to convert it into the right format even if there will be just one
  - then we map this using the mapper methods we define like `getComplexStudentMapping1`
    - we should avoid using `@Column` in this case as different mappings may have different column aliases
    - we depend completely on the mapping methods for this
- For this, refer to `ComplexStudentDaoImpl`
  - this doesn't extend from `R2dbcRepository` and instead from a new repository entirely
  - we define mappings and query implementations in the same file like we do in MyBatis in the synchronous world
  - we don't need the annotations on the domain class anymore since we do our own mapping

### Multi-inserts

- We can use `DatabaseClient` to run the query and we can get `rowsUpdated()`
- But all of the query-building has to be done by us manually
- When we call the dao method for insert, we must add a `.subscribe()` else it doesn't work (as with `R2dbcRepository`)
- Attempt to create methods for programmatic query building with `if-else`, `foreach` and entity relationships [TODO]

### Auth config

- auth config in R2DBC [TODO]

---

## References

- https://docs.spring.io/spring-data/relational/reference/r2dbc.html

---
