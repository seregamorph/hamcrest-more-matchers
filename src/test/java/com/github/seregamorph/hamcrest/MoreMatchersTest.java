package com.github.seregamorph.hamcrest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

import static com.github.seregamorph.hamcrest.MoreMatchers.predicate;
import static com.github.seregamorph.hamcrest.MoreMatchers.where;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

public class MoreMatchersTest {

    private static final String ARGUMENT = "str";
    private static final String SUCCESS_MESSAGE = "Should have valid length";
    private static final String FAIL_MESSAGE = "Should have invalid length";

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    @Test
    public void whereShouldGiveReferenceMethodDiagnosticsNotNull() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected: Object that matches not null after call SamplePojo.getName\n"
                + "     but: was null");

        var user = new SamplePojo();
        collector.checkThat(user, where(SamplePojo::getName, notNullValue()));
    }

    @Test
    public void whereShouldFindMatchingItem() {
        List<SamplePojo> list = Arrays.asList(
                new SamplePojo()
                        .setName("name1"),
                new SamplePojo()
                        .setName("name2")
        );

        assertThat(list, hasItem(where(SamplePojo::getName, equalTo("name1"))));
        assertThat(list, everyItem(where(SamplePojo::getName, startsWith("name"))));
    }

    @Test
    public void whereShouldBeNullSafe() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected: Object that matches \"name\" after call SamplePojo.getName\n"
                + "     but: was null");

        SamplePojo pojo = null;

        assertThat(pojo, where(SamplePojo::getName, equalTo("name")));
    }

    @SuppressWarnings({"Convert2MethodRef", "CodeBlock2Expr"})
    @Test
    public void whereShouldGiveReferenceLambdaMethodDiagnostics() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage(allOf(
                startsWith("\nExpected: Object that matches not null after call MoreMatchersTest.java:"),
                endsWith("\n     but: was null")
        ));

        var pojo = new SamplePojo();
        collector.checkThat(pojo, where(sample -> {
            return sample.getName();
        }, notNullValue()));
    }

    @Test
    public void whereShouldGiveReferenceMethodDiagnosticsNull() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected: Object that matches null after call SamplePojo.getName\n"
                + "     but: was \"value\"");

        var user = new SamplePojo()
                .setName("value");
        collector.checkThat(user, where(SamplePojo::getName, nullValue()));
    }

    @Test
    public void predicateShouldSuccess() {
        collector.checkThat(ARGUMENT, predicate(str -> str.length() == ARGUMENT.length(), SUCCESS_MESSAGE));
    }

    @Test
    public void predicateShouldFail() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage(FAIL_MESSAGE);

        collector.checkThat(ARGUMENT, predicate(str -> str.length() != ARGUMENT.length(), FAIL_MESSAGE));
    }

    @Test
    public void containsShouldMatchWhere() {
        List<SamplePojo> list = Arrays.asList(
                new SamplePojo().setName("foo"),
                new SamplePojo().setName("bar")
        );

        assertThat(list, contains(
                where(SamplePojo::getName, is("foo")),
                where(SamplePojo::getName, is("bar"))
        ));
    }

    @Test
    public void containsShouldNotMatchWhere() {
        expectedException.expect(AssertionError.class);

        List<SamplePojo> list = Arrays.asList(
                new SamplePojo().setName("wrong"),
                new SamplePojo().setName("bar")
        );

        assertThat(list, contains(
                where(SamplePojo::getName, is("foo")),
                where(SamplePojo::getName, is("bar"))
        ));
    }

    @Test
    public void shouldMatchConstructorExtractor() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected: every item is Object that matches a value greater than <5> after call Integer(String)\n"
                + "     but: an item <1> was less than <5>");

        List<String> list = Arrays.asList("1", "10");

        collector.checkThat(list, everyItem(where(Integer::new, greaterThan(5))));
    }

    private static class SamplePojo {

        private String name;

        public String getName() {
            return name;
        }

        public SamplePojo setName(String name) {
            this.name = name;
            return this;
        }
    }
}
