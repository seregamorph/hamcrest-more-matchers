package com.github.seregamorph.hamcrest;

import static com.github.seregamorph.hamcrest.MoreMatchers.where;
import static com.github.seregamorph.hamcrest.TestLambdaUtils.parseArgumentClasses;
import static com.github.seregamorph.hamcrest.TestLambdaUtils.unreferenceLambdaConstructor;
import static com.github.seregamorph.hamcrest.TestLambdaUtils.unreferenceLambdaMethod;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Locale;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.val;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

@SuppressWarnings({"CodeBlock2Expr", "Convert2MethodRef"})
public class TestLambdaUtilsTest {

    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    @Test
    public void shouldUnreferenceOverloadedFunctionMethod() {
        // String.toLowerCase()
        ThrowingFunction<String, String> fun = String::toLowerCase;

        val method = unreferenceLambdaMethod(fun);

        assertMethod(method, "toLowerCase");
    }

    @Test
    public void shouldUnreferenceOverloadedBiFunctionMethod() {
        // String.toLowerCase(Locale)
        ThrowingBiFunction<String, Locale, String> fun = String::toLowerCase;

        val method = unreferenceLambdaMethod(fun);

        assertMethod(method, "toLowerCase", Locale.class);
    }

    @Test
    public void shouldUnreferenceOverloadedStaticFunctionMethod() {
        // Integer.parseInt(String)
        ThrowingFunction<String, Integer> fun = Integer::parseInt;

        val method = unreferenceLambdaMethod(fun);

        assertMethod(method, "parseInt", String.class);
    }

    @Test
    public void shouldUnreferenceOverloadedStaticBiFunctionMethod() {
        // Integer.parseInt(String, int)
        ThrowingBiFunction<String, Integer, Integer> fun = Integer::parseInt;

        val method = unreferenceLambdaMethod(fun);

        assertMethod(method, "parseInt", String.class, int.class);
    }

    @Test
    public void shouldNotUnreferenceClosureFunction() {
        ThrowingFunction<String, String> fun = str -> {
            return str.toLowerCase();
        };

        val method = unreferenceLambdaMethod(fun);

        assertMethod(method, startsWith("lambda$shouldNotUnreferenceClosureFunction$"), String.class);
    }

    @Test
    public void shouldParseArgumentClassesWhenNoArgs() {
        val classes = parseArgumentClasses("()Ljava/lang/String;");

        collector.checkThat(classes, equalTo(new Class[0]));
    }

    @Test
    public void shouldParseArgumentClassesWhenSingleArg() {
        val classes = parseArgumentClasses("(Ljava/util/Locale;)Ljava/lang/String;");

        collector.checkThat(classes, equalTo(new Class[]{Locale.class}));
    }

    @Test
    public void shouldParseArgumentClassesWhenTwoArgs() {
        val classes = parseArgumentClasses("(Ljava/lang/String;Ljava/util/Locale;)Ljava/lang/String;");

        collector.checkThat(classes, equalTo(new Class[]{String.class, Locale.class}));
    }

    @Test
    public void shouldUnreferenceBiFunctionBooleanArgument() {
        ThrowingBiConsumer<Pojo, Boolean> fun = Pojo::setBooleanField;

        val method = unreferenceLambdaMethod(fun);

        assertMethod(method, "setBooleanField", boolean.class);
    }

    @Test
    public void shouldUnreferenceBiFunctionByteArgument() {
        ThrowingBiFunction<Pojo, Byte, Pojo> fun = Pojo::setByteField;

        val method = unreferenceLambdaMethod(fun);

        assertMethod(method, "setByteField", byte.class);
    }

    @Test
    public void shouldUnreferenceBiFunctionCharArgument() {
        ThrowingBiFunction<Pojo, Character, Pojo> fun = Pojo::setCharField;

        val method = unreferenceLambdaMethod(fun);

        assertMethod(method, "setCharField", char.class);
    }

    @Test
    public void shouldUnreferenceBiFunctionShortArgument() {
        ThrowingBiFunction<Pojo, Short, Pojo> fun = Pojo::setShortField;

        val method = unreferenceLambdaMethod(fun);

        assertMethod(method, "setShortField", short.class);
    }

    @Test
    public void shouldUnreferenceBiFunctionIntArgument() {
        ThrowingBiFunction<Pojo, Integer, Pojo> fun = Pojo::setIntField;

        val method = unreferenceLambdaMethod(fun);

        assertMethod(method, "setIntField", int.class);
    }

    @Test
    public void shouldUnreferenceBiFunctionLongArgument() {
        ThrowingBiFunction<Pojo, Long, Pojo> fun = Pojo::setLongField;

        val method = unreferenceLambdaMethod(fun);

        assertMethod(method, "setLongField", long.class);
    }

    @Test
    public void shouldUnreferenceBiFunctionFloatArgument() {
        ThrowingBiFunction<Pojo, Float, Pojo> fun = Pojo::setFloatField;

        val method = unreferenceLambdaMethod(fun);

        assertMethod(method, "setFloatField", float.class);
    }

    @Test
    public void shouldUnreferenceBiFunctionDoubleArgument() {
        ThrowingBiFunction<Pojo, Double, Pojo> fun = Pojo::setDoubleField;

        val method = unreferenceLambdaMethod(fun);

        assertMethod(method, "setDoubleField", double.class);
    }

    @Test
    public void shouldNotUnreferenceConstuctor() {
        // new Integer(String)
        ThrowingFunction<String, Integer> fun = Integer::new;

        val method = unreferenceLambdaMethod(fun);

        assertNull(method);
    }

    @Test
    public void shouldUnreferenceFunctionConstuctor() {
        // new Integer(String)
        ThrowingFunction<String, Integer> fun = Integer::new;

        val constructor = unreferenceLambdaConstructor(fun);

        assertConstructor(constructor, String.class);
    }

    private void assertMethod(Method method, String name, Class<?>... parameterTypes) {
        assertMethod(method, equalTo(name), parameterTypes);
    }

    private void assertMethod(Method method, Matcher<String> nameMatcher, Class<?>... parameterTypes) {
        collector.checkThat(method, where(Method::getName, nameMatcher));
        collector.checkThat(method, where(Method::getParameterTypes, equalTo(parameterTypes)));
    }

    private void assertConstructor(Constructor<?> constructor, Class<?>... parameterTypes) {
        collector.checkThat(constructor, where(Constructor::getParameterTypes, equalTo(parameterTypes)));
    }

    @Data
    @Accessors(chain = true)
    private static class Pojo {

        private boolean booleanField;

        private byte byteField;

        private char charField;

        private short shortField;

        private int intField;

        private long longField;

        private float floatField;

        private double doubleField;

        public void setBooleanField(boolean booleanField) {
            this.booleanField = booleanField;
            // intended not returning this
        }

    }

}
