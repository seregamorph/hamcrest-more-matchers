package com.github.seregamorph.hamcrest;

import java.util.Optional;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class OptionalMatchers {

    /**
     * Matcher for {@link Optional} that expects that it presents.
     */
    public static <T> Matcher<Optional<T>> isPresent() {
        return new TypeSafeDiagnosingMatcher<Optional<T>>() {
            @Override
            protected boolean matchesSafely(Optional<T> value, Description mismatchDescription) {
                mismatchDescription.appendText("is " + value);
                return value.isPresent();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("to be present");
            }
        };
    }

    /**
     * Matcher for {@link Optional} that expects that it presents and applies passed <code>matcher</code>
     *
     * @param matcher matcher to validate present optional value
     */
    public static <T> Matcher<Optional<T>> isPresent(Matcher<T> matcher) {
        return new TypeSafeDiagnosingMatcher<Optional<T>>() {
            @Override
            protected boolean matchesSafely(Optional<T> value, Description mismatchDescription) {
                mismatchDescription.appendText("is " + value);
                return value.isPresent() && matcher.matches(value.get());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("to be present and match ")
                        .appendDescriptionOf(matcher);
            }
        };
    }

    /**
     * Matcher that expects empty optional.
     */
    public static <T> Matcher<Optional<T>> isEmpty() {
        return new TypeSafeDiagnosingMatcher<Optional<T>>() {
            @Override
            protected boolean matchesSafely(Optional<T> value, Description mismatchDescription) {
                mismatchDescription.appendText("is " + value);
                return !value.isPresent();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("to be empty");
            }
        };
    }

    private OptionalMatchers() {
    }
}
