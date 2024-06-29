# Spring WebFlux R2DBC

## R2DBC

- R2DBC is the technology used to work with databases in reactive spring (as JDBC is in non-reactive)
- We add the `spring-boot-starter-data-r2dbc` dependency to pom
- In addition, we also need a driver for mysql compliant with R2DBC
- For this, we use the `r2dbc-mysql` artifact from `io.asyncer`
- Postgres on the other hand has official dependency support on `start.spring.io`

- Currently test load fails so see hwo to fix that [FIX]

---

- Figure out how to add: [TODO]
  - DB calls in R2JDBC
  - auth config in R2JDBC

---
