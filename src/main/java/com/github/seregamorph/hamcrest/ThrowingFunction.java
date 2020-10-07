package com.github.seregamorph.hamcrest;

import java.io.Serializable;

/**
 * Note: it is intended that this functional interface extends Serializable.
 *
 * @see TestLambdaUtils#unreferenceLambdaMethod(Serializable)
 */
@FunctionalInterface
public interface ThrowingFunction<T, R> extends Serializable {

    R apply(T t) throws Exception;
}
