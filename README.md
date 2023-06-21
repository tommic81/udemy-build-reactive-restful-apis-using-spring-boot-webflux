# Build reactive restful apis using Spring Boot Webflux

- [Course code](https://github.com/dilipsundarraj1/Teach-ReactiveSpring)

- [Reactor 3 Reference Guide](https://projectreactor.io/docs/core/release/reference/)
- [Which-operator](https://projectreactor.io/docs/core/release/reference/#which-operator)

#### Why Reactive Programming?

- Tomcat by default can handle 200 connections
- Tomcat pool size: `server.tomcat.max-threads`

#### Introduction

- Works like events
- One Event or Message for a every result item from Data Source
- Data Sources:
  - Data Base
  - External Service
  - File etc.,
- One Event or Message for completion or error
- Data flow as an Event Driven stream
  - OnNext(item) – Data Stream events.
  - OnComplete() -> Completion/Success event.
  - OnError() -> Error Event

##### Reactive Streams specification

###### Publisher

```java
public interface Publisher<T> {
    public void subscribe(Subscriber<? super T> s);
}
```

- Represents the **Data Source**
  - Data Base
  - External Service etc.

###### Subscriber

```java
public interface Subscriber<T> {
    public void onSubscribe(Subscription s);
    public void onNext(T t); //receiving data
    public void onError(Throwable t)
    public void onComplete();//end of receiving
}
```

###### Subscription

```java
public interface Subscription {
    public void request(long n); //give me data
    public void cancel(); //cancelling subscription
}
```

###### Processor

```java
public interface Processor<T, R> extends Subscriber<T>, Publisher<R>{
}
```

##### Reactive Libraries

- RxJava
- Reactor
- Flow Class - JDK 9

#### Project Reactor

- reactor-core
- reactor-test
- reactor-netty

##### reactor-core

- Core library for Project Reactor.
- Implementation of Reactive Streams Specification.
- Java 8 (Minimum)
- Flux and Mono
- Reactive Types of project
  reactor
- Flux – Represents 0 to N
  elements
- Mono – Represents 0 to 1
  element

###### Flux – 0 to N elements

```java
Flux.just("String", "Spring Boot", "Reactive Spring Boot").map(s -> s.concat("flux"))
    .subscribe(System.out::println);
```

###### Mono – 0 to 1 elements

```java
Mono.just("Spring").map(s -> s.concat("flux")).subscribe(System.out::println);
```

#### Backpressure

- Subscriber controls the data flow from the Publisher.



| Subscriber                   |      | Publisher    |
| ---------------------------- | ---- | ------------ |
| getAllItems()                | =>   |              |
| subscribe                    | =>   |              |
|                              | <=   | subscription |
| send me one item: request(1) | =>   |              |
|                              | <=   | onNext(Item) |
| request(1)                   | =>   |              |
|                              | <=   | onNext(item) |
| cancel()                     | =>   |              |

#### Reactive programming with DB

- Maven dependency

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
</dependency>
```

- Setting up Mongo
  - Download installer from https://www.mongodb.com/try/download/community 