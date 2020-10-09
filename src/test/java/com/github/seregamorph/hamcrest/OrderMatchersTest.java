package com.github.seregamorph.hamcrest;

import static com.github.seregamorph.hamcrest.OrderMatchers.softOrdered;
import static com.github.seregamorph.hamcrest.OrderMatchers.strictOrdered;
import static java.util.Collections.singleton;
import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

public class OrderMatchersTest {

    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void strictOrderedSingleShouldSuccess() {
        collector.checkThat(singleton(1), strictOrdered());
    }

    @Test
    public void strictOrderedEqualShouldFail() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Found equal elements 1 and 1");

        collector.checkThat(Arrays.asList(1, 1), strictOrdered());
    }

    @Test
    public void softOrderedEqualShouldSuccess() {
        collector.checkThat(Arrays.asList(1, 1, 2), softOrdered());
    }

    @Test
    public void orderedEqualShouldFailIfIncorrectOrdered() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Found unordered elements 2 and 0");

        collector.checkThat(Arrays.asList(1, 2, 0), strictOrdered());
    }

    @Test
    public void orderedShouldSuccessIfCorrectOrdered() {
        collector.checkThat(Arrays.asList(1, 2), strictOrdered());
    }

    @Test
    public void reverseOrderedShouldSuccessIfCorrectOrdered() {
        collector.checkThat(Arrays.asList(2, 1), strictOrdered(reverseOrder()));
    }

    @Test
    public void nestedCollectionShouldMatchOrderedItem() {
        List<Iterable<Integer>> nested = Arrays.asList(
                Arrays.asList(3, 2, 1),
                Arrays.asList(1, 2, 3)
        );

        assertThat(nested, hasItem(strictOrdered()));
        assertThat(nested, hasItem(softOrdered()));
    }

    @Test
    public void stringsShouldMatchOrderByLength() {
        List<String> list = Arrays.asList("abc", "ab", "a");

        assertThat(list, strictOrdered(comparing(String::length).reversed()));
    }

}
