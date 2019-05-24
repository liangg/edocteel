# CSS Kitchen Food Order Fulfillment System

Author: Liang Guo (liangg.guo@gmail.com)

## Setup

Install _Java 8_ and _gradle_.

```
liang_guo:~/workspace/css{master} $ java -version
java version "1.8.0_191"
Java(TM) SE Runtime Environment (build 1.8.0_191-b12)
Java HotSpot(TM) 64-Bit Server VM (build 25.191-b12, mixed mode)
```

```
liang_guo:~/workspace/css{master} $ gradle -version

------------------------------------------------------------
Gradle 4.10.2
------------------------------------------------------------

Build time:   2018-09-19 18:10:15 UTC
Revision:     b4d8d5d170bb4ba516e88d7fe5647e2323d791dd

Kotlin DSL:   1.0-rc-6
Kotlin:       1.2.61
Groovy:       2.4.15
Ant:          Apache Ant(TM) version 1.9.11 compiled on March 23 2018
JVM:          1.8.0_191 (Oracle Corporation 25.191-b12)
OS:           Mac OS X 10.14.4 x86_64
```

To run the application, run the following commands. You can change log level to `debug`
to see more outputs.

```
cd css
gradle test
gradle jar
java -Dorg.slf4j.simpleLogger.defaultLogLevel=info -jar build/libs/css.jar ./src/main/resources/food_orders.json
```

## Design Overview

The Kitchen application has three scheduled services and one backend. The Kitchen
is center of all components, providing interface for them to interact with each other.
Its main function runs the application, starting all scheduled services at open, and
shuts them down when it closes.

__OrderSource__

The food order submission system. It reads from external source of food order data
and convert them to raw Orders, and submit them to the Kitchen's `OrderProcessor`.

__OrderProcessor__

The raw Order ingestion system. It decouples the order fulfillment backend service
from the incoming food orders. It uses a concurrent BlockingQueue to absorb raw
Orders and route them to the Kitchen's fulfillment backend service, OrderBackend.
In production, it should use be a persistent pub-sub job queue (e.g. Kafka, Kinesis).

__OrderBackend__

The order fulfillment service. It provides the simple business logic of placing
processed food orders to the right shelf, and handling shelf overflow when required.
For each shelved order, it calls Kitchen delivery driver scheduler for driver pickup.

__DriverScheduler__

The order delivery dispatch system. It takes a `DriverOrder`, which has a unique food
order id and the associated food shelf type, and schedules a driver to come for a
pickup and delivery.

### Implementation

__ShelfOrder__

It wraps a raw `Order` with a unique order id and decaying value computation. Per the
requirement, an order's value decay over time, as follows,

```
value = (shelf_life - order_age) - decay_rate * order_age
```

Note, the value is essentially the remaining shelf life time. It decreases over the
elapsing time. It is tracked by fields `value` and `lastValuedAtMilli`. The different
decay rate is factored in the `computeAndSetValue` method.

__Shelf__

A Shelf class for all 4 types of shelves -- `Hot`, `Cold`, `Frozen`, and `Overflow`.
Its public API are defined to differentiate regular shelves and the Overflow shelf.

__IdGenerator__

It's important that every order has a unique order ID. It uses a simple `AtomicLong`
as id source base. In production, it should be a more scalable id generation service
such as Snowflake, or, database auto increment feature.

__MetricsManager__

Metrics counters are important for any reason that one can think of. Here, it is a
set of counters. But, in production, it should wraps metrics reporting to a scalable
service -- counter service, or Datadog etc.

__OrderReader__

A util class that reads an order json data file and parse them into a list of raw
Orders.

### Scheduled Services

Three system components run as scheduled services -- `OrderSource`, `OrderProcessor`,
and `DriverScheduler`. They are all subclasses of `CssScheduler`.

The `OrderSource` interacts with `OrderProcessor` by submitting raw Orders to
`OrderProcessor` through `Kitchen::submitOrder`. The thread synchronization between
them is by the BlockingQueue in `OrderProcessor`.

The `OrderProcessor` interacts with `DriverScheduler` through the Kitchen's `OrderBackend`. 
The OrderBackend interacts with `DriverScheduler` by submitted a `DriverOrder` through
`Kitchen::scheduleDriver`. The thread synchronization is by using `ConcurrentLinkedDeque`
in `DriverScheduler`.

While `Shelf` uses `ConcurrentHashMap` for its core {OrderID, ShelfOrder} map, `OrderBackend`
provides locking synchronization for threading between `OrderProcessor` and `DriverDispatcher`.

### Concurrency

Although `Shelf` uses ConcurrentHashMap, a Reentrant lock in `OrderBackend` is still required
for concurrency correctness because a regular shelf and the Overflow shelf can be both mutated 
on a slow path. Optimization in separating fast path from slow path is possible, but it will
be complex.

In real production, it could use partitioned dataset, possibly as follows,

- Each `OrderBackend` instance has its own set of Shelves.
- Each `Shelf` has its own `Overflow` shelf, instead of `Hot`, `Cold`, and `Frozen` shelves 
  share the same `Overflow` shelf.
