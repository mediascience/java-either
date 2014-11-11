/**
 * Licensed under the Apache License, Version 2.0 (the "License") under
 * one or more contributor license agreements. See the NOTICE file
 * distributed with this work for information regarding copyright
 * ownership. You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sample.com.msiops.ground.either;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import com.msiops.ground.either.Either;

public enum Example implements Runnable {

    CAPTURE_CHECKED_EXCEPTION {
        @Override
        public void run() {
            final Either<URI, Throwable> left = Either.ofChecked(() -> new URI(
                    "my:uri"));
            assert left.isLeft();
            assert left.getLeft().equals(URI.create("my:uri"));

            final Either<URI, Throwable> right = Either
                    .ofChecked(() -> new URI("::90::uri"));
            assert !right.isLeft();
            assert URISyntaxException.class.isInstance(right.getRight());
        }
    },

    CAPTURE_UNCHECKED_EXCEPTION {
        @Override
        public void run() {
            final Either<Integer, RuntimeException> left = Either
                    .of(() -> Integer.valueOf("AFE03", 16));
            assert left.isLeft();
            assert left.getLeft().equals(Integer.valueOf("AFE03", 16));

            final Either<Integer, RuntimeException> right = Either
                    .of(() -> Integer.valueOf("BOBSYOURUNCLE", 16));
            assert !right.isLeft();
            assert NumberFormatException.class.isInstance(right.getRight());
        }
    },

    CONSTRUCT_FROM_OPTIONAL {
        @Override
        public void run() {
            final Either<Integer, String> left = Either.of(Optional.of(10),
                    "missing");
            assert left.isLeft();
            assert left.getLeft().equals(10);

            final Either<Integer, String> right = Either.of(Optional.empty(),
                    "missing");
            assert !right.isLeft();
            assert right.getRight().equals("missing");

        }
    },

    CONSTRUCT_FROM_OPTIONAL_DEFERRED {
        @Override
        public void run() {

            final Either<Integer, String> left = Either.of(Optional.of(10),
                    () -> "missing");
            assert left.isLeft();
            assert left.getLeft().equals(10);

            final Either<Integer, String> right = Either.of(Optional.empty(),
                    () -> "missing");
            assert !right.isLeft();
            assert right.getRight().equals("missing");

        }
    },

    CONSTRUCT_LEFT {
        @Override
        public void run() {

            final Either<Integer, String> left = Either.left(10);

            assert left.isLeft();
            assert left.getLeft().equals(10);

        }
    },

    CONSTRUCT_RIGHT {
        @Override
        public void run() {

            final Either<Integer, String> right = Either.right("uh-oh");

            assert !right.isLeft();
            assert right.getRight().equals("uh-oh");

        }
    },

    FLAT_MAP {
        @Override
        public void run() {

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

        }
    },

    ITERATE {
        @Override
        public void run() {

            final Either<Integer, String> left = Either.left(10);
            final ArrayList<Integer> accumL = new ArrayList<>();
            left.forEach(accumL::add);
            assert accumL.equals(Collections.singletonList(10));

            final Either<Integer, String> right = Either.right("exceptional");
            final ArrayList<String> accumR = new ArrayList<>();
            right.forEach(accumL::add);
            assert accumR.isEmpty();

        }
    },

    MAP {
        @Override
        public void run() {

            final Either<Integer, String> left = Either.left(10);
            assert left.map(x -> x * x).isLeft();
            assert left.map(x -> x * x).getLeft().equals(10 * 10);

            final Either<Integer, String> right = Either.right("huh?");
            assert !right.map(x -> x * x).isLeft();
            assert right.map(x -> x * x).getRight().equals("huh?");

        }
    },

    MAYBE {
        @Override
        public void run() {

            final Either<Integer, String> left = Either.left(10);
            assert left.maybe().equals(Optional.of(10));

            final Either<Integer, String> right = Either.right("missing");
            assert right.maybe().equals(Optional.empty());

        }
    },

    STREAM {
        @Override
        public void run() {

            final Either<Integer, String> left = Either.left(10);
            assert left.stream().collect(Collectors.toList())
                    .equals(Collections.singletonList(10));

            final Either<Integer, String> right = Either.right("oops");
            assert right.stream().collect(Collectors.toList()).isEmpty();

        }
    },

    SWAP {
        @Override
        public void run() {

            final Either<Integer, String> left = Either.left(10);
            assert !left.swap().isLeft();
            assert left.swap().getRight().equals(10);

            final Either<Integer, String> right = Either.right("right");
            assert right.swap().isLeft();
            assert right.swap().getLeft().equals("right");

        }
    },

    UNWIND {
        @Override
        public void run() {

            final Either<Integer, String> left = Either.left(10);
            final Either<Integer, String> right = Either.right("right");

            assert left.getLeft().equals(10);
            // left.getRight(); <-- throws, don't do this

            assert right.getRight().equals("right");
            // right.getLeft(); <-- throws, don't do this

            assert left.orElse(99).equals(10);
            assert right.orElse(99).equals(99);

            assert left.orElseGet(() -> 99).equals(10);
            assert right.orElseGet(() -> 99).equals(99);

            assert left.orElseNull().equals(10);
            assert right.orElseNull() == null;

            assert left.orElseThrow(() -> new RuntimeException()).equals(10);
            // right.orElseThrow(() -> new RuntimeException()); <-- throws the
            // supplied exception

        }
    }

}
