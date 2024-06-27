# Spring WebFlux Security

## Reactive Security Config

- Spring reactive uses `ServerHttpSecurity` instead of `HttpSecurity`
  - its mostly similar to configure except for using `authorizeExchange`, `pathMatchers` and `anyExchange` instead
- By default, using `spring.security.user` properties will authenticate every API
- We can use in-memory user details by creating a bean of type `MapReactiveUserDetailsService` which implements `ReactiveUserDetailsService`
  - the setup is mostly similar to the `InMemoryUserDetailsManager`
- When we don't pass authentication information, the webflux endpoints tend to return 200 with no body under some cases
  - when `anyExchange` in security config is configured to be `permitAll`
  - so instead we want to specifically permit for all non-auth endpoints and then do `anyExchange.authenticated()`
  - authenticated endpoints can continue defining roles and then it will return 401 if no auth, 403 if wrong role and 200 if correct role

- As for using passwords directly out of Spring, it is not recommended by the Spring team
- Passwords are always stored and accessible only in the encoded format so there is no way to reuse the password based on user even if the current call is authenticated
- As a result, we will create a new internal user that will have all roles and will be used for all webclient calls
- The actual APIs that use webClient will still be authorized based on the existing non-intenral users
- This internal user will only be used by the application and no actual client

---

## Reactive CRUD operations

- Implemented the main Getter for students
- If need to return nothing via mono, we can use `Mono<Void>` as return type and `Mono.empty()` as value
  - Useful if errors are in Mono but no success values are needed
  - If we do this, we cannot use `flatMap(mono -> Mono<>)` and instead we can use `.then(Mono<>)`

---

## Complex web client operations

- Reactive opeartors allow for better thread usage
- In `StudentClientHandler.java` class, we show an example of how to achieve multiple asynchronous calls
  - first parallel operation is inserting an object in DB (static class variable)
  - second parallel operation is fetching an object from DB by id and then deleting it
  - third parallel operation is fetching multiple objects from DB and bulk updating them
- We add logs at the start and end of each operation, and also in the middle for op2 and op3
  - it creates upto 4 threads and executes the operations in parallel as seen in terminal logs
  - we can make it more obvious by adding `Thread.sleep(xxx)` in the `StudentHandler` methods
- If one of these fails, the entire request gets blocked instead of failing
  - `onErrorComplete` simple marks the sequence complete but doesn't terminate the mono
  - `doOnError` on the other hand terminate the mono and returns the actual exception on failure
  - this however returns the request the moment there is an error and doesn't do any remaining operations
- To do independent operations regardless of failure
  - We can use `onErrorResume` and add to an errors list
  - At the end, instead of a `doOnError` using `and`, which will immediately fail if one of the sources fail
  - We will use `merge` and `doFinally` to check if errors if empty and return a response accordingly

---

## Simple reactive operations

- `Flux` and `Mono` are the basic types where flux can emiy multiple values whereas mono can do max one
- The values don't start flowing until we `susbcribe` to them
- both implement the `Publisher` interface but the actual reactive operators don't exist on that interface
- generally, when we subscribe to a flux/mono
  - it checks if its part of a previous flux/mono due to operators in a pipeline
  - if it is, it signals its source
  - this happens recursively until the actual source receives the signal and starts the data flow
- this is the default behavior referred to as `Cold Flux`
  - each subscribe will run the entire data in the flux through the pipeline
- the other kind is `Hot Flux` which doesn't need a subscriber to start data flow
  - therefore, it can miss values depending on when it is subscribed to
  - as long as there is one subscription to the cold flux its made from, it can push data

---

## Schedulers

- Schedulers allow controlling the threading model of the workload
- Some schedulers are already built into reactor
  - `immediate` does things on the same thread
  - `single` is for one-off tasks and creates one extra thread
  - `parallel` is for N parallel short-lived tasks in parallel threads (N = number of vCPUs)
  - `elastic` is for any number of tasks that keeps spawning threads on demand with no limit
  - `boundedElastic` is similar to `elastic` but puts a limit of threads after which it enqueues tasks
- Most operators work on the same thread they were signalled from except time-based ones like `delay`
- `subscibeOn` and `publishOn` are two scheduler operators discussed below in the `Operators` section
  - Generally `subscribeOn` is used after blocking operations whereas `publishOn` is used before blocking operations
  - If they are used together
    - it first follows the `subscribeOn` scheduler from bottom to top in terms of subscriptions
    - then it goes top to bottom on the `subscribeOn` scheduler till it reaches a `publishOn`
    - then everything happens on the `publishOn` scheduler until there are operators like `delay` which use a different scheduler
- If using `Future` for blocking operation, don't use it with `subscribeOn` as it doesn't guarantee it running on specific thread
  - `publishOn` guarantees it and therefore is recommended
  - ideally, don't use futures here as you can directly call the method that makes an API call instead of returning a `Future`
- To create a custom scheduler, you can use `newBoundedElastic`, `newParallel` and `newSingle` static methods of `Schedulers` class

---

## Backpressure

- Sometimes values in a stream are produced too quickly for a consumer to process
- `Backpressure` refers to the regulation of transmission of stream elements
- We can do one of the following things:
  - request only as many elements as client can process
  - limit the number of elements sent from publisher
  - cancel the stream from client when it cannot process anymore
- In each of these cases, we can check with the following setup:
  - start with a range of 1 to 20 and add a delay of 200ms
  - then add a log to each of these elements so that we know when that part of the chain emits
  - then add a publishOn so that the rest of the chain happens on a different thread
  - finally, sleep the thread for 1000ms and then log the value again in the subscription
  - this allows us to see exactly how many are published from publisher at a time and the different pace of processing by client
- cancel is very much like request but instead of a request again cycle, we just call `cancel()`

---

## Operators

- Create operators:
  - `empty` to create an empty flux/mono
  - `error(Throwable)` to create a flux/mono that completes with an error
  - `just` to create flux/mono from separate values
  - `fromIterable` to create flux/mono from a list
  - `interval` emits values starting from 0 based on a specified duration
    - first value is emitted after the first interval duration elapses
  - `range` creates a stream of values from a specified min to max including both

- Transform operators:
  - `map` does some transformation for each emitted value but only supports synchronous operations
  - `flatMap` does some transformation for each emitted value but supports asynchronous operations as well
    - if there is an internal flux, then flatMap will merge the values
    - if the internal flux is delayed, then values will be interleaved from both internal and external flux

- Time operators:
  - `delayElements` to delay each element from the flux/mono to be delayed by a certain amount of time

- Combine operators:
  - `zipWith` combines the  using a provided function and ends when any one of them ends
    - it waits for the first values of both streams and then emits the first value, then repeats
  - `zip` works similar to `zipWith` but is for multiple sources
  - `concat` taks multiple streams and combines them by subscribing to each stream in order
    - once the first stream is completed, only then is the second stream started
  - `concatWith` is only for two sources called as `a.concatWith(b)` and works similar to concat
  - `combineLatest` takes multiple stream sources and a combinator function
    - it takes the latest values from both streams and combines them
    - for any value emitted, it will check if there are most recent values from both streams and combine it
    - if one of them doesn't have a value yet, it will wait until there is one and then combine it
    - it will start checking from when the first value from either stream is emitted
    - combine it implies return the value using the combinator function
  - `merge` emits a value if any of its source streams emits a value
  - `mergeWith` is only for two sources called as `a.mergeWith(b)` and works similar to merge

- Filter operators:
  - `take` takes a parameter to specify how many values to take from source stream
  - `filter` takes a function returning boolean to filter the values of a stream

- Blocking operators:
  - `blockFirst` blocks until the first value is emitted or flux is completed
    - it also optionally takes a timeout to block until that time and else throw exception
  - `blockLast` blocks until the flux completes and returns the last value, also takes timeout parameter
  - `toIterable` returns an iterable for each emitted value and blocks on the `iter.next()` call

- Hot flux operators:
  - `share` returns a new hot flux from an existing cold flux

- Scheduler operators:
  - `publishOn` takes a scheduler argument and the corresponding stream executes on a new thread of that scheduler
    - works when the blocking operation can be preceded by this operator
    - it changes where the `onNext`, `onError` and `onComplete` methods are called
  - `subscribeOn` is similar but more generally is used when the blocking operation precedes this operator
    - it changes where the `subscribe` method gets called instead
    - it can still be used after the blocking operation but the internal workings are different from `publishOn`

---

## Caveats

- Security setup sometimes doesn't work if you have a jar running inside a docker container
- This happens due to external jars which can be found on system by default but not on docker image
- To address this, we add the following to `spring-boot-maven-plugin` in `pom.xml` plugins

```
<includeSystemScope>true</includeSystemScope>
```

---

- Figure out how to add: [TODO]
  - DB calls in R2JDBC
  - auth config in R2JDBC

---
