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
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
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

    @Test
    public void testForEachLeft() {

        @SuppressWarnings("unchecked")
        final Consumer<Object> c = mock(Consumer.class);

        Either.left("v").forEach(c);

        verify(c).accept("v");

    }

    @Test
    public void testForEachRight() {

        @SuppressWarnings("unchecked")
        final Consumer<Object> c = mock(Consumer.class);

        Either.right("r").forEach(c);

        verify(c, never()).accept(any());

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
    public void testLeftOrElseFromRight() {

        final Either<Object, ?> right = Either.right("right");

        assertEquals("other", right.orElse("other"));

    }

    @Test
    public void testLeftOrElseGetFromLeft() {

        @SuppressWarnings("unchecked")
        final Supplier<Object> other = mock(Supplier.class);

        final Either<Object, ?> left = Either.left("left");

        assertEquals("left", left.orElseGet(other));
        /*
         * function must no be called
         */
        verify(other, never()).get();

    }

    @Test
    public void testLeftOrElseGetFromRight() {

        final Either<Object, ?> right = Either.right("right");

        assertEquals("other", right.orElseGet(() -> "other"));

    }

    @Test
    public void testLeftOrElseGetNullSupplierFromLeft() {

        final Either<Object, ?> left = Either.left("left");

        assertEquals("left", left.orElseGet(null));

    }

    @Test(expected = NullPointerException.class)
    public void testLeftOrElseGetNullSupplierFromRight() {

        Either.right("right").orElseGet(null);

    }

    @Test
    public void testLeftOrElseGetSuppliesNullFromLeft() {

        final Either<Object, ?> left = Either.left("left");

        assertEquals("left", left.orElseGet(() -> null));
    }

    @Test(expected = NullPointerException.class)
    public void testLeftOrElseGetSuppliesNullFromRight() {

        Either.right("right").orElseGet(() -> null);

    }

    @Test
    public void testLeftOrElseNullFromLeft() {

        assertEquals("left", Either.left("left").orElseNull());

    }

    @Test
    public void testLeftOrElseNullFromRight() {
        assertNull(Either.right("right").orElseNull());

    }

    @Test
    public void testLeftOrElseNullValueFromLeft() {

        final Either<Object, ?> left = Either.left("left");

        assertEquals("left", left.orElse(null));
    }

    @Test(expected = NullPointerException.class)
    public void testLeftOrElseNullValueFromRight() {

        Either.right("right").orElse(null);

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
    public void testMaybeFromLeft() {

        final Either<?, ?> e = Either.left("left");

        assertEquals(Optional.of("left"), e.maybe());

    }

    @Test
    public void testMaybeFromRight() {

        final Either<?, ?> e = Either.right("right");

        assertEquals(Optional.empty(), e.maybe());

    }

    @Test
    public void testMaybeRightFromLeft() {

        final Either<?, ?> e = Either.left("left");

        assertEquals(Optional.empty(), e.maybeRight());

    }

    @Test
    public void testMaybeRightFromRight() {

        final Either<?, ?> e = Either.right("right");

        assertEquals(Optional.of("right"), e.maybeRight());

    }

    @Test
    public void testOrElseFromLeft() {

        final Either<Object, ?> left = Either.left("left");

        assertEquals("left", left.orElse("other"));
    }

    @Test
    public void testOrElseThrowFromLeft() {

        @SuppressWarnings("unchecked")
        final Supplier<RuntimeException> gen = mock(Supplier.class);

        assertEquals("v",
                Either.left("v").orElseThrow(() -> new RuntimeException()));
        /*
         * supplier must not be invoked
         */
        verify(gen, never()).get();

    }

    @Test
    public void testOrElseThrowFromLeftNullSupplier() {

        assertEquals("v", Either.left("v").orElseThrow(null));

    }

    @Test
    public void testOrElseThrowFromLeftSuppliesNull() {

        assertEquals("v", Either.left("v").orElseThrow(() -> null));

    }

    @Test(expected = RuntimeException.class)
    public void testOrElseThrowFromRight() {

        Either.right("r").orElseThrow(() -> new RuntimeException());

    }

    @Test(expected = NullPointerException.class)
    public void testOrElseThrowFromRightNullSupplier() {

        Either.right("r").orElseThrow(null);

    }

    @Test(expected = NullPointerException.class)
    public void testOrElseThrowFromRightSuppliesNull() {

        Either.right("r").orElseThrow(() -> null);

    }

    @Test
    public void testRightOrElseFromLeft() {

        final Either<?, Object> left = Either.left("left");

        assertEquals("other", left.rightOrElse("other"));

    }

    @Test
    public void testRightOrElseFromRight() {

        final Either<?, Object> right = Either.right("right");

        assertEquals("right", right.rightOrElse("other"));

    }

    @Test
    public void testRightOrElseGetFromLeft() {

        final Either<?, Object> left = Either.left("left");

        assertEquals("other", left.rightOrElseGet(() -> "other"));
    }

    @Test
    public void testRightOrElseGetFromRight() {
        @SuppressWarnings("unchecked")
        final Supplier<Object> other = mock(Supplier.class);

        final Either<?, Object> right = Either.right("right");

        assertEquals("right", right.rightOrElseGet(other));
        /*
         * function must no be called
         */
        verify(other, never()).get();
    }

    @Test(expected = NullPointerException.class)
    public void testRightOrElseGetNullSupplierFromLeft() {

        Either.left("left").rightOrElseGet(null);

    }

    @Test
    public void testRightOrElseGetNullSupplierFromRight() {

        final Either<?, Object> right = Either.right("right");

        assertEquals("right", right.rightOrElseGet(null));

    }

    @Test(expected = NullPointerException.class)
    public void testRightOrElseGetSuppliesNullFromLeft() {

        Either.left("left").rightOrElseGet(() -> null);

    }

    @Test
    public void testRightOrElseGetSuppliesNullFromRight() {

        final Either<?, Object> right = Either.right("right");

        assertEquals("right", right.rightOrElseGet(() -> null));
    }

    @Test
    public void testRightOrElseNullFromLeft() {

        assertNull(Either.left("left").rightOrElseNull());

    }

    @Test
    public void testRightOrElseNullFromRight() {

        assertEquals("right", Either.right("right").rightOrElseNull());
    }

    @Test(expected = NullPointerException.class)
    public void testRightOrElseNullValueFromLeft() {

        Either.left("left").rightOrElse(null);
    }

    @Test
    public void testRightOrElseNullValueFromRight() {

        final Either<?, Object> right = Either.right("right");

        assertEquals("right", right.rightOrElse(null));

    }

    @Test(expected = RuntimeException.class)
    public void testRightOrElseThrowFromLeft() {

        Either.left("v").rightOrElseThrow(() -> new RuntimeException());

    }

    @Test(expected = NullPointerException.class)
    public void testRightOrElseThrowFromLeftNullSupplier() {

        Either.left("v").rightOrElseThrow(null);

    }

    @Test(expected = NullPointerException.class)
    public void testRightOrElseThrowFromLeftSuppliesNull() {

        Either.left("v").rightOrElseThrow(() -> null);

    }

    @Test
    public void testRightOrElseThrowFromRight() {

        @SuppressWarnings("unchecked")
        final Supplier<RuntimeException> gen = mock(Supplier.class);

        assertEquals("r",
                Either.right("r")
                        .rightOrElseThrow(() -> new RuntimeException()));
        /*
         * supplier must not be invoked
         */
        verify(gen, never()).get();

    }

    public void testRightOrElseThrowFromRightNullSupplier() {

        assertEquals("r", Either.right("r").orElseThrow(null));

    }

    public void testRightOrElseThrowFromRightSuppliesNull() {

        assertEquals("r", Either.right("r").orElseThrow(() -> null));

    }

    @Test
    public void testStreamFromLeft() {

        final Stream<?> s = Either.left("left").stream();

        assertEquals(Arrays.asList("left"), s.collect(Collectors.toList()));
    }

    @Test
    public void testStreamFromRight() {

        final Stream<?> s = Either.<String, Object> right("right").stream();

        assertTrue(s.collect(Collectors.toList()).isEmpty());
    }

    @Test
    public void testStreamRightFromLeft() {

        final Stream<?> s = Either.left("left").streamRight();

        assertTrue(s.collect(Collectors.toList()).isEmpty());

    }

    @Test
    public void testStreamRightFromRight() {

        final Stream<?> s = Either.right("right").streamRight();

        assertEquals(Arrays.asList("right"), s.collect(Collectors.toList()));

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
