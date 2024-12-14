# Additional matchers for Hamcrest Library

## "Where" (a.k.a. "when") extracting expression
Accepts a function that extracts the value to be matched via passed matcher. May be convenient to use in nested expressions like collection hasItem matching and also for null safety. Also in case of mismatch it gives diagnostics if the extraction function was a method reference. For example:
```java
import static com.github.seregamorph.hamcrest.MoreMatchers.where;

@Test
public void whereShouldFindMatchingItem() {
    List<SamplePojo> list = Arrays.asList(
            new SamplePojo()
                    .setName("name1"),
            new SamplePojo()
                    .setName("name2")
    );

    // success
    assertThat(list, hasItem(where(SamplePojo::getName, equalTo("name1"))));
    // fails with diagnostics:
    // java.lang.AssertionError: 
    // Expected: every item is Object that matches "name1" after call SamplePojo.getName
    //     but: an item was "name2"
    assertThat(list, everyItem(where(SamplePojo::getName, equalTo("name1"))));
}

public class SamplePojo {
    private String name;

    public String getName() {
        return name;
    }

    public SamplePojo setName(String name) {
        this.name = name;
        return this;
    }
}
```
The method reference resolution works fine in Java 8, 11 and 17.

# Collection order (sort) matchers
There are two matchers that validate that the given iterable: `strictOrdered()` (does not allow equal elements in sequence) and `softOrdered()` (allows equal elements in sequence).

```java
import static com.github.seregamorph.hamcrest.OrderMatchers.*;

@Test
public void softOrderedEqualShouldSuccess() {
    // success
    assertThat(Arrays.asList(1, 1, 2), softOrdered());
    // fails with diagnostics:
    // java.lang.AssertionError: 
    // Expected: Strictly ordered by natural comparator
    //     but: Found equal elements 1 and 1
    assertThat(Arrays.asList(1, 1, 2), strictOrdered());
}
```

Or it can be nested:
```java
import static com.github.seregamorph.hamcrest.OrderMatchers.*;
import static org.hamcrest.Matchers.*;

@Test
public void nestedCollectionShouldMatchOrderedItem() {
    List<List<Integer>> nested = Arrays.asList(
            Arrays.asList(3, 2, 1),
            Arrays.asList(1, 2, 3)
    );

    assertThat(nested, hasItem(softOrdered()));
    assertThat(nested, hasItem(strictOrdered()));
    // fails with diagnostics:
    // java.lang.AssertionError: 
    // Expected: every item is Strictly ordered by natural comparator
    //     but: an item Found unordered elements 3 and 2

    assertThat(nested, everyItem(strictOrdered()));
}

```
It is also [MockMvc](https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/testing.html#spring-mvc-test-server) friendly
```java
mockMvc.perform(get("/api/v3/array-ordered-by-id-endpoint"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].id", strictOrdered()));
```

## How to use the library
Library is available on [Maven Central](https://search.maven.org/artifact/com.github.seregamorph/hamcrest-more-matchers), you can add it in Maven
```xml
<dependency>
    <groupId>com.github.seregamorph</groupId>
    <artifactId>hamcrest-more-matchers</artifactId>
    <version>1.0</version>
    <scope>test</scope>
</dependency>
```
or Gradle
```
testImplementation("com.github.seregamorph:hamcrest-more-matchers:1.0")
```

