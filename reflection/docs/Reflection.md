# Flapdoodle Commons Reflection

Sometimes you can not rely on casting an instance to an expected type. You
have to be sure. With a well crafted TypeInfo these check is simple.

In this first example we check if an object is an instance of List&lt;String&gt;:

```java
List<Object> instance = new ArrayList<>();
TypeInfo<List<String>> typeInfo = TypeInfo.listOf(TypeInfo.of(String.class));

assertThat(typeInfo.isInstance(instance)).isTrue();

instance.add("Hello");
assertThat(typeInfo.isInstance(instance)).isTrue();

instance.add(2);
assertThat(typeInfo.isInstance(instance)).isFalse();
```

but type infos can be nestet, so complex type checks are easy:

```java
List<Pair<String, Map<String, Integer>>> instance = new ArrayList<>();

LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
map.put("Hello", 1);
instance.add(Pair.of("foo", map));

TypeInfo<List<Pair<String, Map<String, Integer>>>> typeInfo = TypeInfo.listOf(
        Pair.typeInfo(
                TypeInfo.of(String.class), TypeInfo.mapOf(
                        TypeInfo.of(String.class), TypeInfo.of(Integer.class)
                )));

Object erased = instance;

assertThat(typeInfo.ifInstance(erased))
        .containsSame(instance);
```