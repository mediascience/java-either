/**
 * Licensed to Media Science International (MSI) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. MSI
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package fn.com.msiops.ground.either;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.msiops.ground.either.Either;
import com.msiops.ground.either.FunctionX;

public class UsageTest {

    @Test
    public void testCheckedConvergentLift() {

        final FunctionX<Integer, ?, ?> f = x -> x * x;

        final Function<Integer, ?> lf = Either.liftChecked(f);

        /*
         * notice that we only need to catch an exception because we are
         * invoking the fn here. This points out the purpose of the checked
         * lift--no exception handler or throws clause is needed.
         */
        final Object r;
        try {
            r = f.apply(10);
        } catch (final Exception e) {
            throw new AssertionError("invalid test, f should not throw");
        }

        assertEquals(Either.left(r), lf.apply(10));

    }

    @Test
    public void testCheckedDivergentLift() {

        final Exception rightx = new Exception("timmy's down a well!");

        final FunctionX<Object, ?, ?> f = v -> {
            throw rightx;
        };

        final Function<Object, ?> lf = Either.liftChecked(f);

        assertEquals(Either.right(rightx), lf.apply(10));

    }

    @Test
    public void testFlatMapLeftToLeft() {

        final Function<Integer, Either<Object, Object>> f = x -> Either.left(x
                * x);

        assertEquals(f.apply(10), Either.left(10).flatMap(f));

    }

    @Test
    public void testFlatMapLeftToRight() {

        final Either<Object, Object> fresult = Either.right("right");

        final Function<Object, Either<Object, Object>> f = v -> fresult;

        assertEquals(fresult, Either.left("left").flatMap(f));

    }

    @Test
    public void testFlatMapRightToLeft() {

        @SuppressWarnings("unchecked")
        final Function<Object, Either<Object, Object>> f = mock(Function.class);

        assertEquals(Either.right("right"),
                Either.<Object, Object> right("right").flatMap(f));
        /*
         * function must not be called
         */
        verify(f, never()).apply(any());

    }

    @Test
    public void testFlatMapRightToRight() {

        @SuppressWarnings("unchecked")
        final Function<Object, Either<Object, String>> f = mock(Function.class);

        assertEquals(Either.right("right"), Either.right("right").flatMap(f));
        /*
         * function must not be called
         */
        verify(f, never()).apply(any());

    }

    @Test(expected = IllegalStateException.class)
    public void testGetLeftFromRight() {

        Either.right("right").getLeft();

    }

    @Test(expected = IllegalStateException.class)
    public void testGetRightFromLeft() {

        Either.left("left").getRight();

    }

    @Test
    public void testMapLeft() {

        final Function<Integer, ?> f = x -> x * x;

        assertEquals(Either.left(f.apply(10)), Either.left(10).map(f));

    }

    @Test
    public void testMapRight() {

        @SuppressWarnings("unchecked")
        final Function<Integer, ?> f = mock(Function.class);

        assertEquals(Either.right("right"),
                Either.<Integer, Object> right("right").map(f));
        /*
         * function must not be called
         */
        verify(f, never()).apply(any());

    }

    @Test
    public void testOptionalFromLeft() {

        final Either<?, ?> e = Either.left("left");

        assertEquals(Optional.of("left"), e.optional());

    }

    @Test
    public void testOptionalFromRight() {

        final Either<?, ?> e = Either.right("right");

        assertEquals(Optional.empty(), e.optional());

    }

    @Test
    public void testOptionalRightFromLeft() {

        final Either<?, ?> e = Either.left("left");

        assertEquals(Optional.empty(), e.optionalRight());

    }

    @Test
    public void testOptionalRightFromRight() {

        final Either<?, ?> e = Either.right("right");

        assertEquals(Optional.of("right"), e.optionalRight());

    }

    @Test
    public void testStreamFromLeft() {

        final Stream<String> s = Either.left("left").stream();

        final List<String> collected = s.collect(Collectors.toList());

        assertEquals(Arrays.asList("left"), collected);
    }

    @Test
    public void testStreamFromRight() {

        final Stream<String> s = Either.<String, Object> right("right")
                .stream();

        final List<String> collected = s.collect(Collectors.toList());

        assertEquals(Collections.emptyList(), collected);
    }

    @Test
    public void testUncheckedConvergentLift() {

        final Function<Integer, ?> f = x -> x * x;

        final Function<Integer, ?> lf = Either.lift(f);

        assertEquals(Either.left(f.apply(10)), lf.apply(10));

    }

    @Test
    public void testUncheckedDivergentLift() {

        final RuntimeException rightx = new RuntimeException(
                "Red Lectroids from planet 10 by way of the 8th dimension!");

        final Function<Object, ?> f = v -> {
            throw rightx;
        };

        final Function<Object, ?> lf = Either.lift(f);

        assertEquals(Either.right(rightx), lf.apply(10));

    }
}
