package com.github.seregamorph.hamcrest;

import java.io.Serializable;

/**
 * Note: it is intended that this functional interface extends Serializable.
 *
 * @see TestLambdaUtils#unreferenceLambdaMethod(Serializable)
 */
@FunctionalInterface
public interface ThrowingBiConsumer<T, U> extends Serializable {

    void apply(T arg1, U arg2) throws Exception;
}
