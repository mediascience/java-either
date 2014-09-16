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
