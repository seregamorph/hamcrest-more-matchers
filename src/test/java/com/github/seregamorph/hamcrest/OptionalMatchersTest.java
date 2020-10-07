package com.github.seregamorph.hamcrest;

import static com.github.seregamorph.hamcrest.OptionalMatchers.isEmpty;
import static com.github.seregamorph.hamcrest.OptionalMatchers.isPresent;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

public class OptionalMatchersTest {

    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    @Test
    public void checkEmpty() {
        collector.checkThat(Optional.empty(), isEmpty());
        collector.checkThat(Optional.of(1), not(isEmpty()));
    }

    @Test
    public void checkPresent() {
        collector.checkThat(Optional.empty(), not(isPresent()));
        collector.checkThat(Optional.empty(), not(isPresent(equalTo(1))));
        collector.checkThat(Optional.of(1), isPresent());
        collector.checkThat(Optional.of(1), isPresent(equalTo(1)));
        collector.checkThat(Optional.of(1), not(isPresent(equalTo(1L))));
    }
}
