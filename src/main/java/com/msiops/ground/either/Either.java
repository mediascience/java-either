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
package com.msiops.ground.either;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.msiops.footing.functional.FunT1;
import com.msiops.footing.functional.SupplierT;

/**
 * <p>
 * Value that can be of alternative types. An {@link Either} specifies two
 * types, Left and Right, of which the value must be one. A {@link Either} is
 * said to be left or not as reported by its {@link #isLeft()} method. Not-left
 * can also be called right.
 * </p>
 *
 *
 * <p>
 * {@link Either} supports contextual operations such as {@link #map(Function)}
 * and {@link #flatMap(Function)}. For those operations, the left and right
 * variants are treated differently. For the left variant, the map operations
 * transform the value. For the right variant, the map operations are identity.
 * </p>
 *
 * <p>
 * A particular use of {@link Either} is modeling out-of-band values returning
 * from functions. This could be used, for example, to model an expected
 * exceptional condition rather than requiring a caller to handle exceptional
 * divergence.
 * </p>
 *
 *
 *
 * @param <Left>
 *            value type if this is a left instance.
 *
 * @param <Right>
 *            value type if this is a right instance.
 */
public final class Either<Left, Right> {

    /**
     * Construct a left variant from a plain value.
     *
     * @param v
     *            value to put into the context. Must not be null.
     *
     * @param <LL>
     *            left type of constructed instance
     *
     * @param <RR>
     *            right type of constructed instance.
     *
     * @return a left instance.
     */
    public static <LL, RR> Either<LL, RR> left(final LL v) {

        return new Either<>(Objects.requireNonNull(v), (RR) null);

    }

    /**
     * <p>
     * Lift an unchecked function. The resulting function maps to {@link Either}
     * with a right type of {@link RuntimeException}.
     * </p>
     *
     * <p>
     * If the original function diverges with a {@link RuntimeException} when
     * invoked, a right {@link Either} is produced. If it does not diverge, a
     * left {@link Either} is produced. Any other thrown unchecked
     * {@link Throwable} (e.g. @link {@link Error}) from the original function
     * will cause the new function to diverge with the same {@link Throwable}.
     * </p>
     *
     * @param f
     *            function to lift.
     *
     * @param <T>
     *            parameter type of function to lift
     *
     * @param <R>
     *            return type of function to lift
     *
     * @return lifted function.
     */
    public static <T, R> Function<T, Either<R, RuntimeException>> lift(
            final Function<T, R> f) {

        return t -> {
            try {
                return new Either<>(f.apply(t), null);
            } catch (final RuntimeException rtx) {
                return new Either<>(null, rtx);
            }
        };

    }

    /**
     * <p>
     * Lift an checked function. The resulting function maps to {@link Either}
     * with a right type of {@link Exception}.
     * </p>
     *
     * <p>
     * If the original function diverges with a {@link RuntimeException} when
     * invoked, a right {@link Either} is produced. If it does not diverge, a
     * left {@link Either} is produced. Any other thrown unchecked
     * {@link Throwable} (e.g. @link {@link Error}) from the original function
     * will cause the new function to diverge with the same {@link Throwable}.
     * </p>
     *
     * @param f
     *            function to lift.
     *
     * @param <T>
     *            parameter type of function to lift
     *
     * @param <R>
     *            return type of function to lift
     *
     * @return lifted function.
     */
    public static <T, R> Function<T, Either<R, Throwable>> liftChecked(
            final FunT1<T, R> f) {
        return t -> {
            try {
                return new Either<>(f.apply(t), null);
            } catch (final Throwable x) {
                return new Either<>(null, x);
            }
        };

    }

    /**
     * Construct an instance from an {@link Optional}. If present, the optional
     * is mapped to a left variant containing the present value. If not present,
     * the result is the provided right value.
     *
     * @param maybeLeft
     *            if present, a left {@link Either} is produced with the present
     *            value.
     *
     * @param orRight
     *            value of the produced right {@link Either} should the value
     *            not be present.
     *
     * @param <LL>
     *            left type of constructed instance
     *
     * @param <RR>
     *            right type of constructed instance.
     *
     * @return left or right instance depending on the presence of the first
     *         parameter.
     *
     * @throws NullPointerException
     *             if the left value is null or if the left value is not present
     *             and the right value is null.
     */
    public static <LL, RR> Either<LL, RR> of(final Optional<LL> maybeLeft,
            final RR orRight) {

        return maybeLeft.map(v -> new Either<>(v, (RR) null)).orElseGet(
                () -> new Either<>(null, Objects.requireNonNull(orRight)));

    }

    /**
     *
     /** Construct an instance from an {@link Optional}. If present, the
     * optional is mapped to a left variant containing the present value. If not
     * present, the result is the provided right value.
     *
     * @param maybeLeft
     *            if present, a left {@link Either} is produced with the present
     *            value.
     *
     * @param orGetRight
     *            producer for the value of the constructed right {@link Either}
     *            should the value not be present. This will not be invoked if
     *            the first parameter is present.
     *
     * @param <LL>
     *            left type of constructed instance
     *
     * @param <RR>
     *            right type of constructed instance.
     *
     * @return left or right instance depending on the presence of the first
     *         parameter.
     *
     * @throws NullPointerException
     *             if the left value is null or if the left value is not present
     *             and the right supplier is null.
     *
     */
    public static <LL, RR> Either<LL, RR> of(final Optional<LL> maybeLeft,
            final Supplier<RR> orGetRight) {
        return maybeLeft.map(v -> new Either<>(v, (RR) null)).orElseGet(
                () -> new Either<>(null, orGetRight.get()));
    }

    /**
     * <p>
     * Construct from a {@link Supplier}. If the supplier convergences, the
     * constructed instance is a left variant containing the supplied value. If
     * the supplier diverges by throwing, the constructed instance is a right
     * variant containing the thrown exception.
     * </p>
     *
     * @param s
     *            left value supplier.
     *
     * @param <R>
     *            return type of supplier.
     *
     * @return left instance if supplier converges, right instance if it throws
     *         a {@link RuntimeException}.
     */
    public static <R> Either<R, RuntimeException> of(final Supplier<R> s) {

        try {
            return new Either<>(s.get(), null);
        } catch (final RuntimeException rtx) {
            return new Either<>(null, rtx);
        }

    }

    /**
     * <p>
     * Construct from a {@link SupplierT}. If the supplier convergences, the
     * constructed instance is a left variant containing the supplied value. If
     * the supplier diverges by throwing, the constructed instance is a right
     * variant containing the thrown exception.
     * </p>
     *
     * @param s
     *            left value supplier.
     *
     * @param <R>
     *            return type of supplier.
     *
     * @return left instance if supplier converges, right instance if it throws
     *         a {@link Exception}.
     */
    public static <R> Either<R, Throwable> ofChecked(final SupplierT<R> s) {

        try {
            return new Either<>(s.get(), null);
        } catch (final Throwable x) {
            return new Either<>(null, x);
        }

    }

    /**
     * Construct a right variant from a plain value.
     *
     * @param v
     *            value to put into the context. Must not be null.
     *
     * @param <LL>
     *            left type of constructed instance
     *
     * @param <RR>
     *            right type of constructed instance.
     *
     * @return a right instance.
     */
    public static <LL, RR> Either<LL, RR> right(final RR v) {

        return new Either<>((LL) null, Objects.requireNonNull(v));

    }

    /**
     * <p>
     * The left value if this is a left instance, null otherwise. It is an
     * {@link Object} rather than a Left so that this instance can be returned
     * from mapping in the case it is a right instance.
     * </p>
     */
    private final Object left;

    /**
     * The right value if this is a right instance, null otherwise.
     */
    private final Right right;

    private Either(final Left left, final Right right) {

        if (left != null && right != null) {
            throw new AssertionError(
                    "attempt to create with both left and right");
        } else if (left == null && right == null) {
            throw new AssertionError(
                    "attempt to create with neither left nor right");
        }
        this.left = left;
        this.right = right;

    }

    /**
     * <p>
     * An {@link Either} instance is an immutable value object. Two instances
     * are equal if and only if they are the same variant (left or right) and
     * contain the same value as determined by the value's own
     * {@link #equals(Object)} method.
     * </p>
     *
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {

        final boolean rval;
        if (this == obj) {
            rval = true;
        } else if (obj instanceof Either) {
            rval = Objects.equals(this.left, ((Either<?, ?>) obj).left)
                    && Objects.equals(this.right, ((Either<?, ?>) obj).right);
        } else {
            rval = false;
        }
        return rval;
    }

    /**
     * <p>
     * Map the left value according to an {@link Either}-producing function.
     * </p>
     *
     * @param f
     *            <p>
     *            a function that maps a plain value of this instances Left type
     *            to an {@link Either} instance of this instance's type.
     *            </p>
     *
     *            <p>
     *            If this is a left variant, the function is invoked on the
     *            contained value and the result of the function is returned. If
     *            this is a right variant, the function is not invoked and a
     *            right instance containing the current right value, suitably
     *            typed according to the function, is returned. Note that this
     *            method might not create a new instance in the right case. The
     *            old instance may be simply cast with compatible type
     *            parameters and returned.
     *            </p>
     *
     * @param <R>
     *            left type of function return type.
     *
     * @return am instance that has been mapped according to the supplied
     *         function.
     */
    public <R> Either<R, Right> flatMap(
            final Function<? super Left, Either<R, Right>> f) {

        return this.left == null ? fail() : f.apply(extract());
    }

    /**
     * Iterate over the value. If this is a left instance, the value is supplied
     * to the consumer. Otherwise, this method has no observable effect.
     *
     * @param c
     *            consumer. Will not be called if this is a right instance.
     */
    public void forEach(final Consumer<? super Left> c) {

        if (this.left != null) {
            c.accept(extract());
        }

    }

    /**
     * Retrieve the value if this is a left instance.
     *
     * @return the value
     *
     * @throws IllegalStateException
     *             if this is a right instance.
     */
    public Left getLeft() {
        if (this.left == null) {
            throw new IllegalStateException("no left");
        }
        return extract();
    }

    /**
     * Retrieve the value if this is a right instance.
     *
     * @return the value
     *
     * @throws IllegalStateException
     *             if this is a left instance.
     */
    public Right getRight() {
        if (this.right == null) {
            throw new IllegalStateException("no right");
        }
        return this.right;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.left, this.right);
    }

    /**
     * Test the variant type.
     *
     * @return true iff this is a left instance, false iff this is a right
     *         instance.
     */
    public boolean isLeft() {
        return this.left != null;
    }

    /**
     * <p>
     * Map left according to a mapping function.
     * </p>
     *
     * @param f
     *            <p>
     *            a function that maps a plain value of this instances Left type
     *            to some other type.
     *            </p>
     *
     *            <p>
     *            If this is a left variant, the function is invoked on the
     *            contained value and a new left variant containing the produced
     *            value is returned. If this is a right variant, the function is
     *            not invoked and a right instance containing the current right
     *            value, suitably typed according to the function, is returned.
     *            Note that this method might not create a new instance in the
     *            right case. The old instance may be simply cast with
     *            compatible type parameters and returned.
     *            </p>
     *
     * @param <R>
     *            the function return type.
     *
     * @return am instance that has been mapped according to the supplied
     *         function.
     */
    public <R> Either<R, Right> map(final Function<? super Left, R> f) {

        return this.left == null ? fail() : apply(f);

    }

    /**
     * Convert to an optional of the Left type.
     *
     * @return An optional that is present if this is a left variant, empty if
     *         not.
     *
     */
    public Optional<Left> maybe() {
        return this.left == null ? Optional.empty() : Optional.of(extract());
    }

    /**
     * Retrieve the value or an alternative. If this is a left instance, the
     * contained value is returned, otherwise a provided alternative is
     * returned.
     *
     * @param other
     *            alternative value.
     *
     * @return the contained value if this is a left instance, the alternative
     *         otherwise.
     *
     * @throws NullPointerException
     *             if this is a right value and the provided alternative is
     *             null.
     */
    public Left orElse(final Left other) {

        return this.left == null ? Objects.requireNonNull(other) : extract();

    }

    /**
     * Retrieve the value or compute an alternative. If this is a left instance,
     * the contained value is returned, otherwise an alternative is computed
     * from the provided supplier.
     *
     * @param other
     *            alternative value supplier. Required if this is a right
     *            instance. The supplier is not invoked if this is a left
     *            instance. If invoked, the supplier must not return null.
     *
     * @return the contained value if this is a left instance, the computed
     *         alternative otherwise.
     *
     * @throws NullPointerException
     *             if this is a right instance and the supplier parameter is
     *             null or if the supplier is invoked and it returns null.
     */
    public Left orElseGet(final Supplier<? extends Left> other) {

        return this.left == null ? Objects.requireNonNull(other.get())
                : extract();

    }

    /**
     * Retrieve the left value or an alternative. If this is a left value, the
     * contained value is returned. Otherwise, a null reference to the Left type
     * is returned.
     *
     * @return contained value if this is a left instance, null otherwise.
     *
     */
    public Left orElseNull() {

        return this.left == null ? null : extract();
    }

    /**
     * Retrieve the value or die trying.
     *
     * @param genx
     *            throwable supplier. If this is a right instance, this supplier
     *            will be invoked and its return value thrown. If this is a left
     *            instance, the supplier will not be invoked. Must not be null
     *            if this is a right instance. The supplier must not return null
     *            if this is a right instance.
     *
     * @param <X>
     *            type of exception to throw if this is a right instance.
     *
     * @return the value if this is a left instance.
     *
     * @throws X
     *             if this is a right instance.
     *
     * @throws NullPointerException
     *             if this is a right instance and supplier is null or this is a
     *             right instance and the supplier returns null.
     */
    public <X extends Throwable> Left orElseThrow(final Supplier<X> genx)
            throws X {

        if (this.left == null) {
            throw genx.get();
        }

        return extract();

    }

    /**
     * Convert to a {@link Stream} of the Left type.
     *
     * @return A stream containing only the value if this is a left insance. An
     *         empty stream if this is a right instance.
     */
    public Stream<Left> stream() {

        return this.left == null ? Stream.empty() : Stream.of(extract());

    }

    public Either<Right, Left> swap() {

        return this.left == null ? left(this.right) : right(extract());

    }

    @Override
    public String toString() {

        return this.left != null ? ("left={" + String.valueOf(this.left) + "}")
                : ("right={" + String.valueOf(this.right) + "}");

    }

    @SuppressWarnings("unchecked")
    private <R> Either<R, Right> apply(final Function<? super Left, R> x) {
        return new Either<>(x.apply((Left) this.left), null);
    }

    @SuppressWarnings("unchecked")
    private Left extract() {
        return (Left) this.left;
    }

    @SuppressWarnings("unchecked")
    private <R> Either<R, Right> fail() {
        return (Either<R, Right>) this;
    }

}
