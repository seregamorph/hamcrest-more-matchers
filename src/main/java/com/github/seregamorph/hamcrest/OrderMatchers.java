package com.github.seregamorph.hamcrest;

import static java.util.Comparator.naturalOrder;

import java.util.Comparator;
import java.util.Iterator;
import javax.annotation.Nullable;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class OrderMatchers {

    /**
     * Checks that collection is ordered by comparator. Does not allow equal (by compare) elements.
     *
     * @param comparator comparator to use
     */
    public static <T> Matcher<Iterable<T>> strictOrdered(Comparator<? super T> comparator,
                                                                   String comparatorDescription) {
        return ordered(comparator, false, comparatorDescription);
    }

    /**
     * Checks that collection is ordered by comparator. Does not allow equal (by compare) elements.
     *
     * @param comparator comparator to use
     */
    public static <T> Matcher<Iterable<T>> strictOrdered(Comparator<? super T> comparator) {
        return strictOrdered(comparator, null);
    }

    /**
     * Checks that collection is naturally ordered. Does not allow equal (by compare) elements.
     */
    public static <T extends Comparable<? super T>> Matcher<Iterable<T>> strictOrdered() {
        return strictOrdered(naturalOrder(), "natural comparator");
    }

    /**
     * Checks that collection is ordered by comparator. Allows equal (by compare) elements.
     *
     * @param comparator comparator to use
     */
    public static <T> Matcher<Iterable<T>> softOrdered(Comparator<? super T> comparator,
                                                       String comparatorDescription) {
        return ordered(comparator, true, comparatorDescription);
    }

    /**
     * Checks that collection is ordered by comparator. Allows equal (by compare) elements.
     *
     * @param comparator comparator to use
     */
    public static <T> Matcher<Iterable<T>> softOrdered(Comparator<? super T> comparator) {
        return softOrdered(comparator, null);
    }

    /**
     * Checks that collection is naturally ordered. Allows equal (by compare) elements.
     */
    public static <T extends Comparable<? super T>> Matcher<Iterable<T>> softOrdered() {
        return softOrdered(naturalOrder(), "natural comparator");
    }

    private static <T> Matcher<Iterable<T>> ordered(Comparator<? super T> comparator, boolean allowEqual,
                                                    @Nullable String comparatorDescription) {
        return new TypeSafeDiagnosingMatcher<Iterable<T>>() {

            @Override
            protected boolean matchesSafely(Iterable<T> item, Description mismatchDescription) {
                Iterator<? extends T> iterator = item.iterator();
                if (!iterator.hasNext()) {
                    return true;
                }

                T first = iterator.next();
                while (iterator.hasNext()) {
                    T next = iterator.next();
                    int result = comparator.compare(first, next);
                    if (result == 0 && !allowEqual) {
                        mismatchDescription.appendText("Found equal elements " + first + " and " + next);
                        return false;
                    } else if (result > 0) {
                        mismatchDescription.appendText("Found unordered elements " + first + " and " + next);
                        return false;
                    }
                    first = next;
                }

                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText((allowEqual ? "Softly" : "Strictly") + " ordered by "
                        + (comparatorDescription == null ? "comparator" : comparatorDescription));
            }
        };
    }

    private OrderMatchers() {
    }
}
