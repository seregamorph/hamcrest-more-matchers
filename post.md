![](https://habrastorage.org/webt/d3/f7/s9/d3f7s912i_9vv8vreovq1jcnziy.png "Src: https://cguntur.me/category/technology/java/java-8/")
В таких языках программирования, как C#, Kotlin, Groovy, Scala есть возможность расширять класс путем добавления нового функционала, при этом не требуется наследование или изменение самого изначального класса. Это реализовано с помощью специальных выражений, называемых расширения. Java, в отличие от этих языков, не имеет такой возможности из коробки и даже не планирует в ближайших релизах. Благодаря [Lombok](https://projectlombok.org/features/experimental/ExtensionMethod) это стало возможным. Методы расширения были реализованы в Lombok еще 8 лет назад (с поддержкой Eclipse), но для многих все упиралось в поддержку [плагином в IDEA](https://github.com/mplushnikov/lombok-intellij-plugin) (код компилировался, но IDE его не распознавала как валидный). Начиная с текущей версии [2021.1 EAP](https://www.jetbrains.com/idea/nextversion/) Lombok плагин будет предустановлен в IDEA, и теперь он поддерживает методы расширения lombok (спасибо [akozlova](https://github.com/akozlova), [NekoCaffeine](https://github.com/NekoCaffeine) и [mplushnikov](https://github.com/mplushnikov)).
Рассмотрим пример классического статического импорта:
```java
import static org.apache.commons.lang3.StringUtils.capitalize;

public class ExtensionMethods {
    public static void main(String[] args) {
        String str = "test";
        String capitalized = capitalize(str);
        // "Test"
        System.out.println(capitalized);
    }
}
```
при переходе на метод расширения код станет выглядеть так:
```java
import lombok.experimental.ExtensionMethod;
import org.apache.commons.lang3.StringUtils;

@ExtensionMethod(StringUtils.class)
public class ExtensionMethods {
    public static void main(String[] args) {
        String str = "test";
        String capitalized = str.capitalize();
        // "Test"
        System.out.println(capitalized);
    }
}
```
<cut/>
То есть заворачивания аргументов в скобки заменяются на цепочки вызовов. Во многих ситуациях это может облегчить чтение кода, особенно когда цепочки длинные.
Фактически это просто синтаксический сахар. Код при компиляции будет заменен на вызов статического метода. Первый аргумент статического метода и станет объектом "`this`".

##null-значения
В отличие от обычных instance-методов, методы расширения могут работать и с null-значениями, т.е. подобный вызов вполне допустим:
```java
import org.apache.commons.lang3.StringUtils;

@ExtensionMethod(StringUtils.class)
public class MethodExtensions {
    public static void main(String[] args) throws Exception {
        String nullStr = null;
        // "isEmpty=true"
        System.out.println("isEmpty=" + nullStr.trimToEmpty().isEmpty());
    }
}
```

##Еще примеры
Можно добавить в проект на JDK 8 метод, который появится только в JDK 11:
```java
@UtilityClass
public class CollectionExtensions {
    public static <T> T[] toArray(Collection<T> list, IntFunction<T[]> generator) {
        return list.stream().toArray(generator);
    }
}

@ExtensionMethod(CollectionExtensions.class)
public class MethodExtensions {
    public static void main(String[] args) throws Exception {
        List<Integer> list = Arrays.asList(1, 2, 3);
        // toArray(IntFunction<T[]>) добавлен только в Java 11
        Integer[] array = list.toArray(Integer[]::new);
        // "[1, 2, 3]"
        System.out.println(Arrays.toString(array));
    }
}
```

Или добавить более лаконичный вызов `Stream.collect(toList()):`
```java
@UtilityClass
public class StreamExtensions {
    public static <T> List<T> toList(Stream<T> stream) {
        return stream.collect(Collectors.toList());
    }
}

@ExtensionMethod(CollectionExtensions.class)
public class MethodExtensions {
    public static void main(String[] args) throws Exception {
        List<Integer> list = Arrays.asList(3, 1, 2);
        List<Integer> sorted = list.stream()
                .sorted()
                .toList();

        // "[1, 2, 3]"
        System.out.println(sorted);
    }
}
```

##Настройка проекта

- Установите последнюю версию [IDEA EAP](https://www.jetbrains.com/idea/nextversion/), важно: EAP версии не стабильны, зато бесплатны. Плагин доступен и в Ultimate, и в Community Edition.
- Добавьте зависимость lombok: [maven](https://projectlombok.org/setup/maven)

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.16</version>
    <scope>provided</scope>
</dependency>
```
либо для [gradle](https://projectlombok.org/setup/gradle):
```
compileOnly 'org.projectlombok:lombok:1.18.16'
annotationProcessor 'org.projectlombok:lombok:1.18.16'
testCompileOnly 'org.projectlombok:lombok:1.18.16'
testAnnotationProcessor 'org.projectlombok:lombok:1.18.16'
```

- Убедитесь, что включена опция проекта `Build, Execution, Deployment` -> `Compiler` -> `Annotations processor` -> `Enable annotation processing`
- Добавьте аннотацию `@ExtensionMethod` на класс (откуда будет вызов), перечисляя все утилитные классы, из которых необходимо импортировать вызовы.
