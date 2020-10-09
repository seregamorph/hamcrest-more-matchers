package com.github.seregamorph.hamcrest;

import java.io.Serializable;
import java.lang.invoke.MethodHandleInfo;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javax.annotation.Nullable;

public class TestLambdaUtils {

    /**
     * Runtime lambda resolver. If the Function is a simple lambda method reference
     * (e.g. `UserPojo::getName`), it returns the method itself (`UserPojo.getName()`).
     * Original source code found here
     * https://habr.com/ru/post/311788/
     * Related topics and projects:
     * https://github.com/cronn-de/reflection-util
     * https://github.com/jhalterman/typetools
     * https://github.com/Hervian/safety-mirror
     * https://stackoverflow.com/questions/19845213
     * https://stackoverflow.com/questions/21860875
     *
     * @param lambda method reference. It should be {@link Serializable} - this way it can be converted to
     *               {@link SerializedLambda} to extract method signatures
     * @return method reference
     */
    @Nullable
    public static Method unreferenceLambdaMethod(Serializable lambda) {
        SerializedLambda serializedLambda = getSerializedLambda(lambda);
        if (serializedLambda != null
                && (serializedLambda.getImplMethodKind() == MethodHandleInfo.REF_invokeVirtual
                || serializedLambda.getImplMethodKind() == MethodHandleInfo.REF_invokeStatic)) {
            Class<?> cls = implClassForName(serializedLambda.getImplClass());
            Class<?>[] argumentClasses = parseArgumentClasses(serializedLambda.getImplMethodSignature());
            return Stream.of(cls.getDeclaredMethods())
                    .filter(method -> method.getName().equals(serializedLambda.getImplMethodName())
                            && Arrays.equals(method.getParameterTypes(), argumentClasses))
                    .findFirst().orElse(null);
        }
        return null;
    }

    @Nullable
    public static Constructor<?> unreferenceLambdaConstructor(Serializable lambda) {
        SerializedLambda serializedLambda = getSerializedLambda(lambda);
        if (serializedLambda != null
                && (serializedLambda.getImplMethodKind() == MethodHandleInfo.REF_newInvokeSpecial)) {
            Class<?> cls = implClassForName(serializedLambda.getImplClass());
            Class<?>[] argumentClasses = parseArgumentClasses(serializedLambda.getImplMethodSignature());
            return Stream.of(cls.getDeclaredConstructors())
                    .filter(constructor -> Arrays.equals(constructor.getParameterTypes(), argumentClasses))
                    .findFirst().orElse(null);
        }
        return null;
    }

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

    static String getMethodShortReference(Constructor<?> constructor) {
        String parameterTypeNames = Stream.of(constructor.getParameterTypes())
                .map(Class::getSimpleName)
                .collect(Collectors.joining(", "));
        return constructor.getDeclaringClass().getSimpleName()
                + "(" + parameterTypeNames + ")";
    }

    /**
     * Parses impl method signature: returns array of argument classes.
     *
     * @param implMethodSignature value of serializedLambda.getImplMethodSignature()
     *                            (see TestLambdaUtilsTest for examples)
     * @return array of parsed classes
     */
    static Class<?>[] parseArgumentClasses(String implMethodSignature) {
        int parenthesesPos = implMethodSignature.indexOf(')');
        if (!implMethodSignature.startsWith("(") || parenthesesPos <= 0) {
            throw new IllegalStateException("Wrong format of implMethodSignature " + implMethodSignature);
        }
        String argGroup = implMethodSignature.substring(1, parenthesesPos);
        List<Class<?>> classes = new ArrayList<>();
        for (String token : argGroup.split(";")) {
            if (token.isEmpty()) {
                continue;
            }
            classes.add(parseType(token, false));
        }
        return classes.toArray(new Class[0]);
    }

    private static Class<?> parseType(String typeName, boolean allowVoid) {
        if ("Z".equals(typeName)) {
            return boolean.class;
        } else if ("B".equals(typeName)) {
            return byte.class;
        } else if ("C".equals(typeName)) {
            return char.class;
        } else if ("S".equals(typeName)) {
            return short.class;
        } else if ("I".equals(typeName)) {
            return int.class;
        } else if ("J".equals(typeName)) {
            return long.class;
        } else if ("F".equals(typeName)) {
            return float.class;
        } else if ("D".equals(typeName)) {
            return double.class;
        } else if ("V".equals(typeName)) {
            if (allowVoid) {
                return void.class;
            } else {
                throw new IllegalStateException("void (V) type is not allowed");
            }
        } else {
            if (!typeName.startsWith("L")) {
                throw new IllegalStateException("Wrong format of argument type "
                        + "(should start with 'L'): " + typeName);
            }
            String implClassName = typeName.substring(1);
            return implClassForName(implClassName);
        }
    }

    @Nullable
    private static SerializedLambda getSerializedLambda(Serializable lambda) {
        for (Class<?> cl = lambda.getClass(); cl != null; cl = cl.getSuperclass()) {
            try {
                Method m = cl.getDeclaredMethod("writeReplace");
                m.setAccessible(true);
                Object replacement = m.invoke(lambda);
                if (!(replacement instanceof SerializedLambda)) {
                    break;
                }
                return (SerializedLambda) replacement;
            } catch (NoSuchMethodException e) {
                // skip, continue
            } catch (IllegalAccessException | InvocationTargetException | SecurityException e) {
                throw new IllegalStateException("Failed to call writeReplace", e);
            }
        }
        return null;
    }

    private static Class<?> implClassForName(String implClassName) {
        String className = implClassName.replace('/', '.');
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Failed to load class " + implClassName, e);
        }
    }

    private TestLambdaUtils() {
    }

}
