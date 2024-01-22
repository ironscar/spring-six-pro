# Spring AOP

- AOP stands for `Aspect Oriented Programming`

- It helps with doing common things across multiple places without touching all those places thereby allowing cleaner code but may make the application flow hard to follow and have some performance impacts
  - `aspect` is the module of code for a cross-cutting concern (like logging for example)
  - `advice` is what action is taken and when it should be applied
      - `before`, `after`, `after return`, `after throwing`, `around` are different advice types that exist
      - `before` fires before execution
      - `after throwing` only fires if there is exception during execution and passes that exception to calling class after logic
      - `after return` only fires if no exceptions after execution
      - `after` fires regardless of exception or successful return after execution
      - `around` fires before and after method execution using a `proceeding join point`
        - a `proceeding join point` can be used to execute the method at a specific step in the aspect
        - generally executes after `before` and before `after` aspects regardless of `Order` annotation
        - aspect method must return result from `proceeding join point` for actual methods to work
        - doesn't execute past the `proceeding join point` if there is exception and you don't catch it
        - if you catch it, `after throwing` aspect will not trigger since the exception is already handled
  - `join point` is when to apply code during program execution
  - `point cut` is where advice should be applied
  - `weaving` connects aspects to target objects
    - it can be `compile-time`, `load-time` or `run-time`, the latter being the slowest
    - Spring uses run-time weaving AOP with only method-level join-points and only beans can have aspects

- We create an aspect using `@Aspect` and spring requires marking that class as a bean too
  - example aspects are in the `aspect` package using point-cut expressions, get method names, params, return vals, thrown exceptions etc
  - We can define common pointcut expressions which can later be resued or combined in `CommonPointcuts`
  - To define an order to how multiple aspects are applied, we have to create separate aspect beans for them and then use `@Order(x)` for each of them, like is done with `SecondOrderAspect.java`
    - If you have an `afterThrowing` aspect of order(1) and an `after` aspect of order(2), the latter still gets executed first

- To create custom annotations for AOP, we need to create an annotation first
  - we define its target (on method) and retention (if available at runtime or not)
  - find the example at `com.ti.demo.springsixstarter.aspect.custom`
  - we define a new pointcut expression as `@annotation(<FullClasspathOfAnnotation>)`
  - then we use this in the advice either with other pointcut expressions or on its own
  - Using this annotation on methods will directly apply the aspect on them now

---
