[![Build Status](https://travis-ci.org/mediascience/java-either.svg?branch=master)](https://travis-ci.org/mediascience/java-either)

## Java Either

An Either type models two simultaneous types. An instance of
of a particular Either type is a value of exactly one of the
modeled types.

## Usage

### Include Dependencies

```
<dependency>
  <groupId>com.msiops.ground</groupId>
  <artifactId>ground-either</artifactId>
  <version>0.4</version>
</dependency>
```

### Create an Either

#### A left either:
```
Either<Integer,String> left = Either.left(10);
assert left.isLeft();
assert left.getLeft().equals(10);
```

#### A right either:
```
Either<Integer,String> right = Either.right("uh-oh");
assert !right.isLeft();
assert right.getRight().equals("uh-oh");
```

#### Either from Optional
```
Either<Integer,String> leftO = Either.of(Optional.of(100), "missing");
assert leftO.isLeft();
assert leftO.getLeft().equals(100);

Either<Integer, String> rightO = Either.of(Optional.empty(), "missing");
assert !rightO.isLeft();
assert rightO.getRight().equals("missing");

// or defer the right until it is known to be needed
Either<Integer, String> leftOS = Either.of(Optional.of(101), () -> expensive(12L));
assert leftOS.isLeft();
assert leftOS.getLeft().equals(101);   // expensive(..) is never called!
```

#### Either to capture exception
```
Either<Integer, RuntimeException> leftS = Either.of(() -> Integer.valueOf("AFE03",16));
assert leftS.isLeft();
assert leftS.getLeft().equals(Integer.valueOf("AFE03", 16));

Either<Integer, RuntimeException> rightS Either.of(() -> Integer.valueOf("BOBSYOURUNCLE", 16));
assert !rightS.isLeft();
assert NumberFormatException.class.isAssignableFrom(rightS.getRight());
```

### Use an Either

#### Map it
```
Either<?,?> e = Either.left(10).map(x -> x * x);
assert e.isLeft();
assert e.getLeft().equals(100);

Either<?,?> e = Either.right("huh").map(x -> x * x);
assert !e.isLeft();
assert e.getRight().equals("huh");
```
(flatMap, too!)


#### Convert it to optional
````
Optional<?> p = Either.left(10).optional();
assert p.isPresent();
assert p.get().equals(10);

Optional<?> np = Either.right("whoa").optional();
assert !np.isPresent();
````

(there's optionalRight(), too!)

#### Convert it to a stream
```
Stream<Integer> s = Either.left(10).stream();
assert s.collect(Collectors.toList()).equals(Collections.singletonList(10));

Stream<Integer> e = Either.right("oops").stream();
assert s.collect(Collectors.toList()).equals(Collections.emptyList());
```

#### Unwind it
```
Either<Object,Object> left = Either.left("left");
Either<Object,Object> right = Either.right("right");

assert left.getLeft().equals("left");
// left.getRight();   <-- Throws! Don't do this

assert right.getRight().equals("right");
// right.getLeft();   <-- Throws! Don't do this


assert left.leftOrElse("other").equals("left");
assert left.rightOrElse("other").equals("other");

assert right.rightOrElse("other").equals("right");
assert right.leftOrElse("other").equals("other");

assert left.leftOrElseGet(() -> "other").equals("left");
assert left.rightOrElseGet(() -> "other").equals("other");

assert right.rightOrElseGet(() -> "other").equals("right");
assert right.leftOrElseGet(() -> "other").equals("other");

assert left.leftOrElseNull().equals("left");
assert left.rightOrElseNull() == null;

assert right.rightOrElseNull().equals("right");
assert right.leftOrElseNull() == null;
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

A complete change to the interface, behavior, license, or anything else
after the 1.x series is published will result in a new series, such as
2.x.

## Acknowledgements

This work is based on some ideas from standard libraries for Haskell.
Haskell is great and you should learn it. Really.

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

