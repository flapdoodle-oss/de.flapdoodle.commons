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

.. and some reflection helper

```java
Object result = Either.<String, Integer>left("left")
  .mapLeft(it -> "<" + it + ">")
  .mapRight(it -> it + 2);

TypeInfo<Either<String, Integer>> typeInfo = Either.typeInfo(String.class, Integer.class);

assertThat(typeInfo.isInstance(result)).isTrue();
Either<String, Integer> casted = typeInfo.cast(result);
assertThat(casted).isEqualTo(Either.left("<left>"));
```

## Pair

```java
Pair<String, String> result = Pair.of("A", 2)
  .mapFirst(it -> "<" + it + ">")
  .mapSecond(it -> it + 2)
  .map(l -> "first: " + l, r -> "second: " + r);

assertThat(result).isEqualTo(Pair.of("first: <A>","second: 4"));
```

.. and some reflection helper

```java
Object result = Pair.of("A", 2)
  .mapFirst(it -> "<" + it + ">")
  .mapSecond(it -> it + 2);

TypeInfo<Pair<String, Integer>> typeInfo = Pair.typeInfo(String.class, Integer.class);
assertThat(typeInfo.isInstance(result)).isTrue();
Pair<String, Integer> casted = typeInfo.cast(result);
assertThat(casted).isEqualTo(Pair.of("<A>", 4));
```

## Maybe

```java
Maybe<String> maybe = Maybe.some("value")
  .map(it -> "<" + it + ">")
  .map(it -> null);

assertThat(maybe.hasSome()).isTrue();
assertThat(maybe.get()).isNull();
```

.. and some reflection helper

```java
Object result = Maybe.some("value")
  .map(it -> "<" + it + ">");

TypeInfo<Maybe<String>> typeInfo = Maybe.typeInfo(String.class);

assertThat(typeInfo.isInstance(result)).isTrue();
Maybe<String> casted = typeInfo.cast(result);
assertThat(casted).isEqualTo(Maybe.some("<value>"));
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