# Flapdoodle Commons Types

## Either

```java
Either<String, Integer> either = Either.left("left");

String result = either
  .mapLeft(it -> "<" + it + ">")
  .mapRight(it -> it + 2)
  .map(l -> "left: " + l, r -> "right: " + r);

assertThat(result).isEqualTo("left: <left>");
```

## Pair

```java
Pair<String, String> result = Pair.of("A", 2)
  .mapFirst(it -> "<" + it + ">")
  .mapSecond(it -> it + 2)
  .map(l -> "first: " + l, r -> "second: " + r);

assertThat(result).isEqualTo(Pair.of("first: <A>","second: 4"));
```

## Maybe

```java
Maybe<String> maybe = Maybe.some("value")
  .map(it -> "<" + it + ">")
  .map(it -> null);

assertThat(maybe.hasSome()).isTrue();
assertThat(maybe.get()).isNull();
```

## Try

```java
Function<String, URL> testee = Try.<String, URL, MalformedURLException>function(URL::new)
  .mapException(ex -> new Exception("failed", ex))
  .mapToUncheckedException(ex -> new RuntimeException("to runtime", ex));

assertThatThrownBy(() -> testee.apply("broken"))
  .isInstanceOf(RuntimeException.class)
  .hasMessageContaining("to runtime")
  .hasCauseExactlyInstanceOf(Exception.class)
  .cause()
  .isInstanceOf(Exception.class)
  .hasMessageContaining("failed")
  .hasCauseExactlyInstanceOf(MalformedURLException.class)
  .cause()
  .isInstanceOf(MalformedURLException.class)
  .hasMessageContaining("no protocol: broken");
```