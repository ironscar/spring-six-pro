# Spring Web MyBatis

## Querying with MyBatis

### Mybatis XML SQL

- Add dependency `mybatis-spring-boot-starter` and `spring-boot-starter-jdbc` (latter is only required if `spring-boot-starter-data-jpa` is not a dependency already)
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
