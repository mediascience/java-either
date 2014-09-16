package sample.com.msiops.ground.either;

/**
 * Run the examples with assertions enabled.
 */
public final class RunExamples {

    private static final Object EXAMPLES;

    static {

        try {
            final ClassLoader loader = ClassLoader.getSystemClassLoader();
            loader.setDefaultAssertionStatus(true);
            final Class<?> clazz = loader
                    .loadClass("sample.com.msiops.ground.either.Examples");
            EXAMPLES = clazz.newInstance();
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException e) {
            throw new RuntimeException("cannot load examples instance", e);
        }

    }

    public static void main(final String[] args) {

        final Examples examples = (Examples) EXAMPLES;

        examples.constructLeft();
        examples.constructRight();
        examples.constructFromOptional();
        examples.constructFromOptionalDeferred();
        examples.captureUncheckedException();
        examples.captureCheckedException();
        examples.map();
        examples.flatMap();
        examples.maybe();
        examples.maybeRight();
        examples.stream();
        examples.streamRight();
        examples.iterate();
        examples.unwind();

    }
}
