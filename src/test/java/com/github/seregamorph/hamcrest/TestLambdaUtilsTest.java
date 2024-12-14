package com.github.seregamorph.hamcrest;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Locale;

import static com.github.seregamorph.hamcrest.MoreMatchers.where;
import static com.github.seregamorph.hamcrest.TestLambdaUtils.parseArgumentClasses;
import static com.github.seregamorph.hamcrest.TestLambdaUtils.unreferenceLambdaConstructor;
import static com.github.seregamorph.hamcrest.TestLambdaUtils.unreferenceLambdaMethod;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertNull;

@SuppressWarnings({"CodeBlock2Expr", "Convert2MethodRef"})
public class TestLambdaUtilsTest {

    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    @Test
    public void shouldUnreferenceOverloadedFunctionMethod() {
        // String.toLowerCase()
        ThrowingFunction<String, String> fun = String::toLowerCase;

        var method = unreferenceLambdaMethod(fun);

        assertMethod(method, "toLowerCase");
    }

    @Test
    public void shouldUnreferenceOverloadedBiFunctionMethod() {
        // String.toLowerCase(Locale)
        ThrowingBiFunction<String, Locale, String> fun = String::toLowerCase;

        var method = unreferenceLambdaMethod(fun);

        assertMethod(method, "toLowerCase", Locale.class);
    }

    @Test
    public void shouldUnreferenceOverloadedStaticFunctionMethod() {
        // Integer.parseInt(String)
        ThrowingFunction<String, Integer> fun = Integer::parseInt;

        var method = unreferenceLambdaMethod(fun);

        assertMethod(method, "parseInt", String.class);
    }

    @Test
    public void shouldUnreferenceOverloadedStaticBiFunctionMethod() {
        // Integer.parseInt(String, int)
        ThrowingBiFunction<String, Integer, Integer> fun = Integer::parseInt;

        var method = unreferenceLambdaMethod(fun);

        assertMethod(method, "parseInt", String.class, int.class);
    }

    @Test
    public void shouldNotUnreferenceClosureFunction() {
        ThrowingFunction<String, String> fun = str -> {
            return str.toLowerCase();
        };

        var method = unreferenceLambdaMethod(fun);

        assertMethod(method, startsWith("lambda$shouldNotUnreferenceClosureFunction$"), String.class);
    }

    @Test
    public void shouldParseArgumentClassesWhenNoArgs() {
        var classes = parseArgumentClasses("()Ljava/lang/String;");

        collector.checkThat(classes, equalTo(new Class[0]));
    }

    @Test
    public void shouldParseArgumentClassesWhenSingleArg() {
        var classes = parseArgumentClasses("(Ljava/util/Locale;)Ljava/lang/String;");

        collector.checkThat(classes, equalTo(new Class[]{Locale.class}));
    }

    @Test
    public void shouldParseArgumentClassesWhenTwoArgs() {
        var classes = parseArgumentClasses("(Ljava/lang/String;Ljava/util/Locale;)Ljava/lang/String;");

        collector.checkThat(classes, equalTo(new Class[]{String.class, Locale.class}));
    }

    @Test
    public void shouldUnreferenceBiFunctionBooleanArgument() {
        ThrowingBiConsumer<Pojo, Boolean> fun = Pojo::setBooleanField;

        var method = unreferenceLambdaMethod(fun);

        assertMethod(method, "setBooleanField", boolean.class);
    }

    @Test
    public void shouldUnreferenceBiFunctionByteArgument() {
        ThrowingBiFunction<Pojo, Byte, Pojo> fun = Pojo::setByteField;

        var method = unreferenceLambdaMethod(fun);

        assertMethod(method, "setByteField", byte.class);
    }

    @Test
    public void shouldUnreferenceBiFunctionCharArgument() {
        ThrowingBiFunction<Pojo, Character, Pojo> fun = Pojo::setCharField;

        var method = unreferenceLambdaMethod(fun);

        assertMethod(method, "setCharField", char.class);
    }

    @Test
    public void shouldUnreferenceBiFunctionShortArgument() {
        ThrowingBiFunction<Pojo, Short, Pojo> fun = Pojo::setShortField;

        var method = unreferenceLambdaMethod(fun);

        assertMethod(method, "setShortField", short.class);
    }

    @Test
    public void shouldUnreferenceBiFunctionIntArgument() {
        ThrowingBiFunction<Pojo, Integer, Pojo> fun = Pojo::setIntField;

        var method = unreferenceLambdaMethod(fun);

        assertMethod(method, "setIntField", int.class);
    }

    @Test
    public void shouldUnreferenceBiFunctionLongArgument() {
        ThrowingBiFunction<Pojo, Long, Pojo> fun = Pojo::setLongField;

        var method = unreferenceLambdaMethod(fun);

        assertMethod(method, "setLongField", long.class);
    }

    @Test
    public void shouldUnreferenceBiFunctionFloatArgument() {
        ThrowingBiFunction<Pojo, Float, Pojo> fun = Pojo::setFloatField;

        var method = unreferenceLambdaMethod(fun);

        assertMethod(method, "setFloatField", float.class);
    }

    @Test
    public void shouldUnreferenceBiFunctionDoubleArgument() {
        ThrowingBiFunction<Pojo, Double, Pojo> fun = Pojo::setDoubleField;

        var method = unreferenceLambdaMethod(fun);

        assertMethod(method, "setDoubleField", double.class);
    }

    @Test
    public void shouldNotUnreferenceConstuctor() {
        // new Integer(String)
        ThrowingFunction<String, Integer> fun = Integer::new;

        var method = unreferenceLambdaMethod(fun);

        assertNull(method);
    }

    @Test
    public void shouldUnreferenceFunctionConstuctor() {
        // new Integer(String)
        ThrowingFunction<String, Integer> fun = Integer::new;

        var constructor = unreferenceLambdaConstructor(fun);

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

    private static class Pojo {

        private boolean booleanField;

        private byte byteField;

        private char charField;

        private short shortField;

        private int intField;

        private long longField;

        private float floatField;

        private double doubleField;

        public boolean isBooleanField() {
            return booleanField;
        }

        public void setBooleanField(boolean booleanField) {
            this.booleanField = booleanField;
            // intended not returning this
        }

        public byte getByteField() {
            return byteField;
        }

        public Pojo setByteField(byte byteField) {
            this.byteField = byteField;
            return this;
        }

        public char getCharField() {
            return charField;
        }

        public Pojo setCharField(char charField) {
            this.charField = charField;
            return this;
        }

        public short getShortField() {
            return shortField;
        }

        public Pojo setShortField(short shortField) {
            this.shortField = shortField;
            return this;
        }

        public int getIntField() {
            return intField;
        }

        public Pojo setIntField(int intField) {
            this.intField = intField;
            return this;
        }

        public long getLongField() {
            return longField;
        }

        public Pojo setLongField(long longField) {
            this.longField = longField;
            return this;
        }

        public float getFloatField() {
            return floatField;
        }

        public Pojo setFloatField(float floatField) {
            this.floatField = floatField;
            return this;
        }

        public double getDoubleField() {
            return doubleField;
        }

        public Pojo setDoubleField(double doubleField) {
            this.doubleField = doubleField;
            return this;
        }
    }
}
