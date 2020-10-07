package com.github.seregamorph.hamcrest;

import java.lang.reflect.Method;
import java.util.function.Predicate;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.TypeSafeMatcher;

public class MoreMatchers {

    /**
     * Matcher that expects non-null value and applies <code>matcher</code> on it. May be useful
     * to avoid possible {@link NullPointerException}.
     *
     * @param matcher matcher
     */
    public static <T> Matcher<T> notNull(Matcher<T> matcher) {
        return new TypeSafeDiagnosingMatcher<T>() {
            @Override
            protected boolean matchesSafely(T value, Description mismatchDescription) {
                mismatchDescription.appendText("is " + value);
                return value != null && matcher.matches(value);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("to be not null and match ")
                        .appendDescriptionOf(matcher);
            }
        };
    }

    /**
     * Matcher for a value extracted by <code>extractor</code>. In case of mismatch <code>extractor</code> is
     * resolved as lambda method reference and added to failure diagnostics.
     *
     * @param extractor value extracting function
     * @param matcher   matcher for extracted value
     */
    public static <U, V> Matcher<U> where(ThrowingFunction<U, V> extractor, Matcher<V> matcher) {
        return new TypeSafeMatcher<U>() {
            @Override
            protected boolean matchesSafely(U item) {
                if (item == null) {
                    return false;
                }
                V target;
                try {
                    target = extractor.apply(item);
                } catch (Exception e) {
                    throw new AssertionError(e);
                }
                return matcher.matches(target);
            }

            @Override
            protected void describeMismatchSafely(U item, Description mismatchDescription) {
                V target;
                try {
                    target = item == null ? null : extractor.apply(item);
                } catch (Exception e) {
                    throw new AssertionError(e);
                }
                matcher.describeMismatch(target, mismatchDescription);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Object that matches ")
                        .appendDescriptionOf(matcher);

                Method methodReference = TestLambdaUtils.unreferenceLambdaMethod(extractor);
                if (methodReference == null) {
                    description.appendText(" after being extracted");
                } else {
                    String shortDescription = TestLambdaUtils.getMethodShortReference(methodReference);
                    description.appendText(" after call " + shortDescription);
                }
            }
        };
    }

    /**
     * Matcher, that fails with expectedDescription diagnostics in case when predicate returns false.
     *
     * @param predicate           predicate to check (expects true)
     * @param expectedDescription diagnostic message
     */
    public static <T> Matcher<T> predicate(Predicate<T> predicate, String expectedDescription) {
        return new TypeSafeMatcher<T>() {

            @Override
            protected boolean matchesSafely(T item) {
                return predicate.test(item);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(expectedDescription);
            }
        };
    }

    private MoreMatchers() {
    }

}
