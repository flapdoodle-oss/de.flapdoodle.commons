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

## Lens
                  
.. change nested immutable object properties with lenses:

```java
ImmutableSample sample = Sample.builder()
  .name("Name")
  .child(Sample.Child.builder()
    .name("Child")
    .age(12)
    .build())
  .build();

Lens<Sample, String> name = Lens.ofProperty(Sample::name)
  .changeBy(ImmutableSample::copyOf, ImmutableSample::withName);
Lens<Sample, Sample.Child> child = Lens.ofProperty(Sample::child)
  .changeBy(ImmutableSample::copyOf, ImmutableSample::withChild);

Lens<Sample.Child, Integer> age = Lens.ofProperty(Sample.Child::age)
  .changeBy(ImmutableChild::copyOf, ImmutableChild::withAge);

assertThat(sample.name()).isEqualTo("Name");
assertThat(sample.child().name()).isEqualTo("Child");
assertThat(sample.child().age()).isEqualTo(12);

Sample withNewName = name.change(sample, "new Name");
Sample withNewAge = child.and(age).map(withNewName, it -> it + 6);

assertThat(withNewAge.name()).isEqualTo("new Name");
assertThat(withNewAge.child().name()).isEqualTo("Child");
assertThat(withNewAge.child().age()).isEqualTo(18);
```

.. and chain these changes:

```java
ImmutableSample sample = Sample.builder()
  .name("Name")
  .child(Sample.Child.builder()
    .name("Child")
    .age(12)
    .build())
  .build();

Lens<Sample, String> name = Lens.ofProperty(Sample::name)
  .changeBy(ImmutableSample::copyOf, ImmutableSample::withName);
Lens<Sample, Sample.Child> child = Lens.ofProperty(Sample::child)
  .changeBy(ImmutableSample::copyOf, ImmutableSample::withChild);

Lens<Sample.Child, Integer> age = Lens.ofProperty(Sample.Child::age)
  .changeBy(ImmutableChild::copyOf, ImmutableChild::withAge);

assertThat(sample.name()).isEqualTo("Name");
assertThat(sample.child().name()).isEqualTo("Child");
assertThat(sample.child().age()).isEqualTo(12);

Sample changed = name.change("new Name")
  .andThen(child.and(age).map(it -> it + 6))
  .apply(sample);

assertThat(changed.name()).isEqualTo("new Name");
assertThat(changed.child().name()).isEqualTo("Child");
assertThat(changed.child().age()).isEqualTo(18);
```