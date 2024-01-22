# Spring Web Basics

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
