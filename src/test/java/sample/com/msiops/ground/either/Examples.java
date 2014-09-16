package sample.com.msiops.ground.either;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import com.msiops.ground.either.Either;

public final class Examples {

    public void captureCheckedException() {

        final Either<URI, Exception> left = Either.ofChecked(() -> new URI(
                "my:uri"));
        assert left.isLeft();
        assert left.getLeft().equals(URI.create("my:uri"));

        final Either<URI, Exception> right = Either.ofChecked(() -> new URI(
                "::90::uri"));
        assert !right.isLeft();
        assert URISyntaxException.class.isInstance(right.getRight());

        System.out.println("Capture checked OK");

    }

    public void captureUncheckedException() {

        final Either<Integer, RuntimeException> left = Either.of(() -> Integer
                .valueOf("AFE03", 16));
        assert left.isLeft();
        assert left.getLeft().equals(Integer.valueOf("AFE03", 16));

        final Either<Integer, RuntimeException> right = Either.of(() -> Integer
                .valueOf("BOBSYOURUNCLE", 16));
        assert !right.isLeft();
        assert NumberFormatException.class.isInstance(right.getRight());

        System.out.println("Capture unchecked OK");

    }

    public void constructFromOptional() {

        final Either<Integer, String> left = Either.of(Optional.of(10),
                "missing");
        assert left.isLeft();
        assert left.getLeft().equals(10);

        final Either<Integer, String> right = Either.of(Optional.empty(),
                "missing");
        assert !right.isLeft();
        assert right.getRight().equals("missing");

        System.out.println("Construct from Optional OK");

    }

    public void constructFromOptionalDeferred() {

        final Either<Integer, String> left = Either.of(Optional.of(10),
                () -> "missing");
        assert left.isLeft();
        assert left.getLeft().equals(10);

        final Either<Integer, String> right = Either.of(Optional.empty(),
                () -> "missing");
        assert !right.isLeft();
        assert right.getRight().equals("missing");

        System.out.println("Construct from Optional (deferred right) OK");

    }

    public void constructLeft() {

        final Either<Integer, String> left = Either.left(10);

        assert left.isLeft();
        assert left.getLeft().equals(10);

        System.out.println("Construct left OK");

    }

    public void constructRight() {

        final Either<Integer, String> right = Either.right("uh-oh");

        assert !right.isLeft();
        assert right.getRight().equals("uh-oh");

        System.out.println("Construct right OK");

    }

    public void flatMap() {

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

        System.out.println("FlatMap OK");

    }

    public void iterate() {

        final Either<Integer, String> left = Either.left(10);
        final ArrayList<Integer> accumL = new ArrayList<>();
        left.forEach(accumL::add);
        assert accumL.equals(Collections.singletonList(10));

        final Either<Integer, String> right = Either.right("exceptional");
        final ArrayList<String> accumR = new ArrayList<>();
        right.forEach(accumL::add);
        assert accumR.isEmpty();

        System.out.println("Iterate OK");

    }

    public void map() {

        final Either<Integer, String> left = Either.left(10);
        assert left.map(x -> x * x).isLeft();
        assert left.map(x -> x * x).getLeft().equals(10 * 10);

        final Either<Integer, String> right = Either.right("huh?");
        assert !right.map(x -> x * x).isLeft();
        assert right.map(x -> x * x).getRight().equals("huh?");

        System.out.println("Map OK");

    }

    public void maybe() {

        final Either<Integer, String> left = Either.left(10);
        assert left.maybe().equals(Optional.of(10));

        final Either<Integer, String> right = Either.right("missing");
        assert right.maybe().equals(Optional.empty());

        System.out.println("Maybe OK");

    }

    public void maybeRight() {

        final Either<Integer, String> left = Either.left(10);
        assert left.maybeRight().equals(Optional.empty());

        final Either<Integer, String> right = Either.right("missing");
        assert right.maybeRight().equals(Optional.of("missing"));

        System.out.println("MaybeRight OK");

    }

    public void stream() {

        final Either<Integer, String> left = Either.left(10);
        assert left.stream().collect(Collectors.toList())
                .equals(Collections.singletonList(10));

        final Either<Integer, String> right = Either.right("oops");
        assert right.stream().collect(Collectors.toList()).isEmpty();

        System.out.println("MaybeRight OK");

    }

    public void streamRight() {

        final Either<Integer, String> left = Either.left(10);
        assert left.streamRight().collect(Collectors.toList()).isEmpty();

        final Either<Integer, String> right = Either.right("oops");
        assert right.streamRight().collect(Collectors.toList())
                .equals(Collections.singletonList("oops"));

        System.out.println("MaybeRight OK");

    }

    public void unwind() {

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
        // supplied exception

        assert right.rightOrElseThrow(() -> new RuntimeException()).equals(
                "right");
        // left.rightOrElseThrow(() -> new RuntimeException()); <-- throws the
        // supplied exception

        System.out.println("Unwind OK");

    }

}
