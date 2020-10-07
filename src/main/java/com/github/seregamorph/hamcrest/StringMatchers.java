package com.github.seregamorph.hamcrest;

import java.util.regex.Pattern;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class StringMatchers {

    /**
     * Match string pattern.
     *
     * @param pattern
     */
    public static Matcher<String> matches(Pattern pattern) {
        return new TypeSafeDiagnosingMatcher<String>() {

            @Override
            protected boolean matchesSafely(String value, Description mismatchDescription) {
                mismatchDescription.appendText("actual was `" + value + "`");
                java.util.regex.Matcher matcher = pattern.matcher(value);
                return matcher.matches();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("to match " + pattern);
            }
        };
    }

    /**
     * Match string regex.
     *
     * @param regex
     */
    public static Matcher<String> matches(String regex) {
        Pattern pattern = Pattern.compile(regex);
        return matches(pattern);
    }

    private StringMatchers() {
    }
}
