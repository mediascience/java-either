[![Build Status](https://travis-ci.org/mediascience/java-either.svg?branch=master)](https://travis-ci.org/mediascience/java-either)

## Java Either

An Either type models two simultaneous types. An instance of
of a particular Either type is a value of exactly one of the
modeled types.

### What Is it Good For?

The motivation behind Either is to signal special cases without
diverging. For example, a function might normally return an Integer
but might sometimes return a String describing a prerequisite violation.

Or consider a function to invoke a remote REST service using JAX-RS.
Under normal conditions, an unmarshalled entity is returned but if
the remote end returns a non-succesful status, the caller might need
the status code to know how to proceed.

Of course, the traditional Java way to model those cases is through
divergence by throwing an exception. In many cases, this is sufficient.
But exceptional-divergence suffers from some drawbacks:

1. you can only signal an exceptional condition with an instance of
Throwable or its sub-classes. You can't, for instance, simply signal with
an integer status code,

1. exceptions unwind the call stack. For certain models, it is more clear
to express the exceptional condition itself as data,

1. exception handlers are bulky and can make code hard to read, and

1. the exceptional divergence model conflicts with some asynchronous
programming styles such as continuation passing or functional reactive
programming. In these styles, the site that handles an exception might
not have any stack relationship to the site that throws it. This makes
it difficult to reason about traditional Java exception handlers.

Another way to think about Either is as a rich Optional. You might think
of a left Either like an Optional with a present value. Then a right
Either is like an empty Optional that carries some additional contextual
information.


## Usage

### Include Dependencies

Either is deployed to Maven Central:
```xml
<dependency>
  <groupId>com.msiops.ground</groupId>
  <artifactId>ground-either</artifactId>
  <version>${v.either}</version>
</dependency>
```

Go to the [project page at Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.msiops.ground%22%20a%3A%22ground-either%22) 
to find the latest version.

### Create an Either

#### A left either:
```java
final Either<Integer, String> left = Either.left(10);

assert left.isLeft();
assert left.getLeft().equals(10);
```

#### A right either:
```java
final Either<Integer, String> right = Either.right("uh-oh");

assert !right.isLeft();
assert right.getRight().equals("uh-oh");
```

#### Either from Optional
```java
final Either<Integer, String> left = Either.of(Optional.of(10),
        "missing");
assert left.isLeft();
assert left.getLeft().equals(10);

final Either<Integer, String> right = Either.of(Optional.empty(),
        "missing");
assert !right.isLeft();
assert right.getRight().equals("missing");
```

#### Either from Optional and right supplier
```java
final Either<Integer, String> left = Either.of(Optional.of(10),
        () -> "missing");
assert left.isLeft();
assert left.getLeft().equals(10);

final Either<Integer, String> right = Either.of(Optional.empty(),
        () -> "missing");
assert !right.isLeft();
assert right.getRight().equals("missing");
```

#### Either to capture exception
```java
final Either<Integer, RuntimeException> left = Either.of(() -> Integer
        .valueOf("AFE03", 16));
assert left.isLeft();
assert left.getLeft().equals(Integer.valueOf("AFE03", 16));

final Either<Integer, RuntimeException> right = Either.of(() -> Integer
        .valueOf("BOBSYOURUNCLE", 16));
assert !right.isLeft();
assert NumberFormatException.class.isInstance(right.getRight());
```

### Use an Either

#### Map it
```java
final Either<Integer, String> left = Either.left(10);
assert left.map(x -> x * x).isLeft();
assert left.map(x -> x * x).getLeft().equals(10 * 10);

final Either<Integer, String> right = Either.right("huh?");
assert !right.map(x -> x * x).isLeft();
assert right.map(x -> x * x).getRight().equals("huh?");
```

#### FlatMap it
```java
final Either<Integer, String> left = Either.left(10);
assert left.flatMap(x -> Either.left(x * x)).equals(
        Either.left(10 * 10));
assert left.flatMap(x -> Either.right("beh")).equals(
        Either.right("beh"));

final Either<Integer, String> right = Either.right("right");
assert right.flatMap(x -> Either.left(x * x)).equals(
        Either.right("right"));
assert right.flatMap(x -> Either.right("beh")).equals(
        Either.right("right"));
```

#### Convert it to optional
```java
final Either<Integer, String> left = Either.left(10);
assert left.maybe().equals(Optional.of(10));

final Either<Integer, String> right = Either.right("missing");
assert right.maybe().equals(Optional.empty());
```

(there's maybeRight(), too!)

#### Convert it to a stream
```java
final Either<Integer, String> left = Either.left(10);
assert left.stream().collect(Collectors.toList())
        .equals(Collections.singletonList(10));

final Either<Integer, String> right = Either.right("oops");
assert right.stream().collect(Collectors.toList()).isEmpty();
```

(there's streamRight, too!)


#### Iterate over it
```java
final Either<Integer, String> left = Either.left(10);
final ArrayList<Integer> accumL = new ArrayList<>();
left.forEach(accumL::add);
assert accumL.equals(Collections.singletonList(10));

final Either<Integer, String> right = Either.right("exceptional");
final ArrayList<String> accumR = new ArrayList<>();
right.forEach(accumL::add);
assert accumR.isEmpty();
```

#### Unwind it
```java
final Either<Integer, String> left = Either.left(10);
final Either<Integer, String> right = Either.right("right");

assert left.getLeft().equals(10);
// left.getRight(); <-- throws, don't do this

assert right.getRight().equals("right");
// right.getLeft(); <-- throws, don't do this

assert left.orElse(99).equals(10);
assert left.rightOrElse("other").equals("other");

assert right.rightOrElse("other").equals("right");
assert right.orElse(99).equals(99);

assert left.orElseGet(() -> 99).equals(10);
assert left.rightOrElseGet(() -> "other").equals("other");

assert right.rightOrElseGet(() -> "other").equals("right");
assert right.orElseGet(() -> 99).equals(99);

assert left.orElseNull().equals(10);
assert left.rightOrElseNull() == null;

assert right.rightOrElseNull().equals("right");
assert right.orElseNull() == null;

assert left.orElseThrow(() -> new RuntimeException()).equals(10);
// right.orElseThrow(() -> new RuntimeException()); <-- throws the
//         supplied exception

assert right.rightOrElseThrow(() -> new RuntimeException()).equals(
        "right");
// left.rightOrElseThrow(() -> new RuntimeException()); <-- throws the
//         supplied exception
```

## Versioning

Releases in the 0.x series are the Wild West. Anything can change between
releases--package names, method signatures, behavior, whatever. But if you
like it as it is right now, all the tests pass so just use it at its current
version and have fun.

The next version series will be 1.x. Every release in that series will be
backward compatible with every lower-numbered release in the same series
except possibly in the case of 1) a bug fix or 2) a correction to an
underspecification.

An incompatible change to the interface, behavior, license, or anything else
after the 1.x series is published will result in a new series, such as
2.x.

## Acknowledgements

This work is based on the Either monad from the standard libraries for Haskell.
[Learning Haskell](http://learnyouahaskell.com/) has improved my coding in all
languages.

Media Science International's support for FOSS and the
sophistication to develop and publish it grows and grows. MSI Yay.

## License

Licensed to Media Science International (MSI) under one or more
contributor license agreements. See the NOTICE file distributed with this
work for additional information regarding copyright ownership. MSI
licenses this file to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance with the
License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations
under the License.

