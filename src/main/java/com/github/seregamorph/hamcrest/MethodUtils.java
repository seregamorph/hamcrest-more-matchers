package com.github.seregamorph.hamcrest;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class MethodUtils {

    static String getMethodShortReference(Method method) {
        try {
            if (method.isSynthetic()) {
                // probably it is classic lambda
                ClassPool pool = ClassPool.getDefault();
                CtClass ctClass = pool.get(method.getDeclaringClass().getCanonicalName());
                CtMethod ctMethod = ctClass.getDeclaredMethod(method.getName());
                int lineNumber = ctMethod.getMethodInfo().getLineNumber(0);
                return ctClass.getClassFile().getSourceFile() + ":" + lineNumber;
            }
        } catch (NoClassDefFoundError | Exception ignore) {
            // NoClassDefFoundError (missing javassist dependency), javassist.NotFoundException
        }
        // probably it is method reference
        return method.getDeclaringClass().getSimpleName() + "." + method.getName();
    }

    static String getConstructorShortReference(Constructor<?> constructor) {
        String parameterTypeNames = Stream.of(constructor.getParameterTypes())
                .map(Class::getSimpleName)
                .collect(Collectors.joining(", "));
        return constructor.getDeclaringClass().getSimpleName()
                + "(" + parameterTypeNames + ")";
    }

    private MethodUtils() {
    }
}
