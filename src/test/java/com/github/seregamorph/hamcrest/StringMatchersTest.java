package com.github.seregamorph.hamcrest;

import static com.github.seregamorph.hamcrest.StringMatchers.matches;
import static org.hamcrest.Matchers.not;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

public class StringMatchersTest {

    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    @Test
    public void testMatchesRegex() {
        collector.checkThat("123", matches("^\\d+$"));
        collector.checkThat("ddd", not(matches("^\\d+$")));
    }

}
