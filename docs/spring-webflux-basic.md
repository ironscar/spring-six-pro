# Spring WebFlux Basics

## Basics

- Spring Reactive requires `spring-boot-starter-webflux` dependency
- For reactive APIs, we need a `handler` which takes a request and creates a response
  - This is a component bean and defines the method taking a `ServerRequest` and generating a `Mono<ServerResponse>`
  - To send a value in body, we can use `BodyInserters.fromValue(obj)` where `obj` is the value
- We also need a `router` to specify what route does the response come from
  - This takes a handler as argument to use in a route
  - Define a single config class with a single bean of `RouterFunctions<ServerResponse>`
  - You can define multiple routes here with `RouterFunctions.route(<predicates>, <handler>).andRoute(<predicates>, <handler>)...` or `RouterFunctions.route().GET(<predicates>, <handler>)...`
  - Each route method internally takes the request predicates like api route, content type etc and the handler method reference
  - Predicates by default may require `RequestPredicates.` before them but we can just `import static RequestPredicates.*` to use the members directly
  - We can do the same for `RouterFunctions` as it has some useful static members
    - like we can use `RouterFunctions.nest` to specify a base url and common media type etc
- When specifying paths in router function, take care that there cannot be conflicts
  - if first is `/reactive/{id}` and second is `/reactive/client`, then second will never happen
  - because it will attempt to resolve client with the path variable for `id`
  - so we should order the router accordingly
- If multiple handlers, create separate routers for each handler and all related routes can go inside the same router
  - These methods can each take a different handler but they need to be named differently
  - The name makes sure that there are no bean conflicts in Spring
  - Finally, if there are path conflicts like `/{id}` and `/client`, then use `@Order` to specify which routes are processed first
- Check examples in package `springsixstarter.reactive`

## Reactive Controller

- Path vaiables can be specified in the url passed to `RequestPredicates.GET` in router as `{param}`
  - they can be accessed in handler as `request.pathVariable(<paramName>)`
  - they will always be string and cannot be type-checked
- Query parameters need not even be specified in the url
  - they can be accessed in handler as `request.queryParam(<paramName>)` which returns an optional
  - they will always be string and cannot be type-checked
- Multiple paths for same handler can be specified by 
  - `(RequestPredicates.GET(path1).or(RequestPredicates.GET(path2))).and(...)`
- We cannot seem to be able to define type of body for a POST/PUT/DEL/PATCH request in router
  - In handler, we have to do `request.bodyToMono()` and then go from there
  - Use `ParameterizedTypeReference` to cast to generic types like `List<X>`

## Reactive Exception Handling

- For handler level, `Mono` has multiple error methods like `onErrorReturn`, `onErrorResume` etc
  - we can use `onErrorResume` at the end of the chain after the main server response piece
    - whenever we want to continue regardless of error, we can use `onErrorResume`
    - generally, we can handle a single error by `doOnError` and then empty it by `onErrorResume(e -> Mono.empty())` to continue with rest of the values
  - here we can use the exception object to return a server response with a custom exception body
  - whenever there is an error at any part of the chain, it will skip the chain till the next error handler and execute it
  - it only seemed to work for web client usage but not for actual response return
    - this only works if the exception is supplied as a `Mono.error`
    - throwing a general exception directly will just skip the chain and can only caught by try-catch
    - But an exception returned in a `Mono.error` will be caught by the `onError` handlers
    - This is why when there is an error in webclient, it raises it as a `Mono.error` and gets caught by the `onError` handlers

- For global level, we need to create global error handler component
  - This `GlobalExceptionHandler` implements `WebExceptionHandler` interface and overrides its `handle` method
  - Its set to `Order(-2)` as the default exception handler has order 1 and we want to use this instead
  - Here we can check for the exception type using `instanceof` and handle it accordingly
  - Now `WebExceptionHandler` is low level so we have to deal directly in bytes
  - Thus we use `ObjectMapper` to create bytes of our error response body and write it to response
  - This throws exceptions but it shouldn't, so to be safe, we add an error message and convert that to bytes for a single error message if this ever happens
  - This setup also works for the servlet controller endpoint exception handling and you wouldn't need an exception controller either as long as webflux is in dependency, but since that is more concise to write, we will keep it

---

## Web Client

- We can create a web client with specific `baseUrl` in a config class
- We can use `client.get().uri(<path>).retrieve().bodyToMono(<type>)` to make the API call
- For usual classes, `bodyToMono` takes the `<Class>.class`
- For generic classes, we can define a `ParameterizedTypeReference<GenType<InternalType>> () {}`
- the retuned value is a mono so we can further process it reactively

---

## Reactive Programming

- A `Mono` is a reactive type that can emit at most one element (something like a one-time observable)
  - It allows subscribing to and specify actions on emit, on error and on complete
- A `Flux` is a reactive type that can emit any number of elements (true observable)
  - It also allows subscribing to in a similar manner
- `flatMap` can be used to convert a `Mono<X>` to `Mono<Y>` by passing a custom lambda function which returns another `Mono`
  - they can be chained in sequence to keep converting from one `Mono` to another
- `onErrorMap` can be used on convert Mono exceptions of one type to another

## Caveats

- You shouldn't have `web` and `webflux` dependencies in same project as webflux APIs silently fail with 404 in that case
  - we can still use `WebClient` in normal REST APIs though

---

- Figure out how to add: [TODO]
  - parallel calls with webclient
  - DB calls in R2JDBC
  - auth config in R2JDBC
  - use password directly from spring security context (its encoded in bcrypt and we dont have the actual password so it fails)

---
