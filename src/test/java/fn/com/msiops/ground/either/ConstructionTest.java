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
package fn.com.msiops.ground.either;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Test;

import com.msiops.footing.functional.SupplierT;
import com.msiops.ground.either.Either;

public class ConstructionTest {

    @Test
    public void testFromCheckedConvergentLeftSupplier() {

        final SupplierT<?> left = () -> "left";

        final Either<?, ?> e = Either.ofChecked(left);

        assertTrue(e.isLeft());
        assertEquals("left", e.getLeft());

    }

    @Test
    public void testFromCheckedDivergentLeftSupplier() {

        final Exception rightx = new Exception("right");
        final SupplierT<?> left = () -> {
            throw rightx;
        };

        final Either<?, ?> e = Either.ofChecked(left);

        assertFalse(e.isLeft());
        assertEquals(rightx, e.getRight());

    }

    @Test
    public void testFromConvergentLeftSupplier() {

        final Supplier<?> left = () -> "left";

        final Either<?, ?> e = Either.of(left);

        assertTrue(e.isLeft());
        assertEquals("left", e.getLeft());

    }

    @Test(expected = NullPointerException.class)
    public void testFromNotPresentOptionalAndNullRightSupplierIllegal() {

        Either.of(Optional.empty(), (Supplier<?>) null);

    }

    @Test(expected = NullPointerException.class)
    public void testFromNotPresentOptionalAndNullRightValueIllegal() {

        Either.of(Optional.empty(), (Object) null);

    }

    @Test
    public void testFromNotPresentOptionalAndRightSupplier() {

        final Supplier<?> right = () -> "right";

        final Either<?, ?> e = Either.of(Optional.empty(), right);

        assertFalse(e.isLeft());
        assertEquals("right", e.getRight());

    }

    @Test
    public void testFromNotPresentOptionalAndRightValue() {

        final Either<?, ?> e = Either.of(Optional.empty(), "right");

        assertFalse(e.isLeft());
        assertEquals("right", e.getRight());

    }

    @Test(expected = NullPointerException.class)
    public void testFromNullOptionalAndRightValueIllegal() {

        Either.of((Optional<?>) null, "right");

    }

    @Test
    public void testFromPresentOptionalAndNullRightSupplier() {

        assertEquals(Either.left("left"),
                Either.of(Optional.of("left"), (Supplier<?>) null));

    }

    @Test
    public void testFromPresentOptionalAndNullRightValue() {

        assertEquals(Either.left("left"),
                Either.of(Optional.of("left"), (Object) null));

    }

    @Test
    public void testFromPresentOptionalAndRightSupplier() {

        final Supplier<?> right = mock(Supplier.class);

        final Either<?, ?> e = Either.of(Optional.of("left"), right);

        assertTrue(e.isLeft());
        assertEquals("left", e.getLeft());
        /*
         * if optional is present, right supplier must not be invoked.
         */
        verify(right, never()).get();

    }

    @Test
    public void testFromPresentOptionalAndRightValue() {

        final Either<?, ?> e = Either.of(Optional.of("left"), "right");

        assertTrue(e.isLeft());
        assertEquals("left", e.getLeft());

    }

    @Test
    public void testFromUncheckedDivergentLeftSupplier() {

        final RuntimeException rightx = new RuntimeException("right");
        final Supplier<?> left = () -> {
            throw rightx;
        };

        final Either<?, ?> e = Either.of(left);

        assertFalse(e.isLeft());
        assertEquals(rightx, e.getRight());

    }

    @Test(expected = NullPointerException.class)
    public void testLefttNullIllegal() {

        Either.left(null);

    }

    @Test
    public void testLeftValue() {

        final Either<?, ?> e = Either.left("left");
        assertTrue(e.isLeft());
        assertEquals("left", e.getLeft());

    }

    @Test(expected = NullPointerException.class)
    public void testRightNullIllegal() {

        Either.right(null);

    }

    @Test
    public void testRightValue() {

        final Either<?, ?> e = Either.right("right");
        assertFalse(e.isLeft());
        assertEquals("right", e.getRight());

    }

}
