## Flapdoodle Commons Collections
                                 
### GroupBy - create trees from lists

If you have a list with some records:

```java
List<Record> records = Arrays.asList(
  Record.builder().city("Hamburg").street("Hauptstraße").number(2).lastname("Schmidt").build(),
  Record.builder().city("Hamburg").street("Hauptstraße").number(2).lastname("Meier").build(),
  Record.builder().city("Hamburg").street("Nebenstraße").number(3).lastname("Jonson").build(),
  Record.builder().city("Köln").street("Hauptstraße").number(2).lastname("Schüler").build(),
  Record.builder().city("Köln").street("Am Kanal").number(7).lastname("Schneider").build()
);
```

and you want this list grouped by street name and number, you can greate a grouping function by providing
the type of the list, a function to create the grouping entry for each list entry, a function to identify
the grouping attribute and a merge function where the grouping entry and the matching records are combined:

```java
GroupBy<Record, ImmutableAddress, Pair<String, Integer>> groupByAddress = GroupBy.withListOf(Record.class)
  .map(record -> Address.builder().name(record.street()).number(record.number()).build())
  .identifiedBy(address -> Pair.of(address.name(), address.number()))
  .merge(ImmutableAddress::withFamilyNames, Record::lastname);
```

You can now apply this function to the list of records, and get the expected results:

```java
List<? extends Address> groupedByAddress = groupByAddress.apply(records);

assertThat(groupedByAddress)
  .hasSize(3)
  .satisfiesExactly(
    first -> {
      assertThat(first.name()).isEqualTo("Hauptstraße");
      assertThat(first.number()).isEqualTo(2);
      assertThat(first.familyNames()).containsExactly("Schmidt", "Meier", "Schüler");
    },
    second -> {
      assertThat(second.name()).isEqualTo("Nebenstraße");
      assertThat(second.number()).isEqualTo(3);
      assertThat(second.familyNames()).containsExactly("Jonson");
    },
    third -> {
      assertThat(third.name()).isEqualTo("Am Kanal");
      assertThat(third.number()).isEqualTo(7);
      assertThat(third.familyNames()).containsExactly("Schneider");
    });
```
                                    
You see that the city part is ignored, so that names for streets in different cities are grouped together. To fix that,
we can add another layer and group by the city name:

```java
GroupBy<Record, ImmutableCity, String> groupByCity = GroupBy.withListOf(Record.class)
  .map(record -> City.builder().name(record.city()).build())
  .identifiedBy(City::name)
  .merge((city, matchingRecords) -> city.withAddressList(groupByAddress.apply(matchingRecords)));
```

As you can see, we use the first group-by function in the second instance. Now we can apply this function to the list of records:                                 

```java
List<? extends City> groupedByCity = groupByCity.apply(records);

assertThat(groupedByCity)
  .hasSize(2)
  .satisfiesExactly(
    city_a -> {
      assertThat(city_a.name()).isEqualTo("Hamburg");
      assertThat(city_a.addressList())
        .hasSize(2)
        .satisfiesExactly(
          first -> {
            assertThat(first.name()).isEqualTo("Hauptstraße");
            assertThat(first.number()).isEqualTo(2);
            assertThat(first.familyNames()).containsExactly("Schmidt", "Meier");
          },
          second -> {
            assertThat(second.name()).isEqualTo("Nebenstraße");
            assertThat(second.number()).isEqualTo(3);
            assertThat(second.familyNames()).containsExactly("Jonson");
          });
    },
    city_b -> {
      assertThat(city_b.name()).isEqualTo("Köln");
      assertThat(city_b.addressList())
        .hasSize(2)
        .satisfiesExactly(
          first -> {
            assertThat(first.name()).isEqualTo("Hauptstraße");
            assertThat(first.number()).isEqualTo(2);
            assertThat(first.familyNames()).containsExactly("Schüler");
          },
          second -> {
            assertThat(second.name()).isEqualTo("Am Kanal");
            assertThat(second.number()).isEqualTo(7);
            assertThat(second.familyNames()).containsExactly("Schneider");
          });
    });
```

Now the records are grouped by city and then by street name and number.
                                                                       
### TypedMap - different types in a single map

Create an immutable instance of TypedMap. A part of the key is a [TypeInfo](../../reflection/docs/Reflection.md) to ensure type safety:                       

```java
ImmutableTypedMap<String> immutableMap = TypedMap.<String>immutable()
  .add(TypeInfo.of(String.class), "foo", "bar")
  .add(Pair.typeInfo(String.class, Double.class), "bar", Pair.of("x", 2.0));

ImmutableTypedMap<String> copy = immutableMap.add(TypeInfo.of(String.class), "bar", "different type");

assertThat(copy.get(TypeInfo.of(String.class), "foo"))
  .isEqualTo("bar");
assertThat(copy.get(Pair.typeInfo(String.class, Double.class), "bar"))
  .isEqualTo(Pair.of("x", 2.0));
assertThat(copy.get(TypeInfo.of(String.class), "bar"))
  .isEqualTo("different type");
```

Each change to the map creates a copy with the changed data. If you need a mutable version you can just create a mutable typed map:

```java
MutableTypedMap<String> typedMap = TypedMap.mutable();

typedMap.put(TypeInfo.of(String.class), "foo", "bar");
typedMap.put(Pair.typeInfo(String.class, Double.class), "bar", Pair.of("x", 2.0));

assertThat(typedMap.get(TypeInfo.of(String.class), "foo"))
  .isEqualTo("bar");
assertThat(typedMap.get(Pair.typeInfo(String.class, Double.class), "bar"))
  .isEqualTo(Pair.of("x", 2.0));
assertThat(typedMap.get(TypeInfo.of(String.class), "bar"))
  .isNull();
```

.. and you can convert the one into the other and back:

```java
MutableTypedMap<String> typedMap = TypedMap.mutable();

typedMap.put(TypeInfo.of(String.class), "foo", "bar");
typedMap.put(Pair.typeInfo(String.class, Double.class), "bar", Pair.of("x", 2.0));

ImmutableTypedMap<String> copy = typedMap.asImmutable();

assertThat(copy.get(TypeInfo.of(String.class), "foo"))
  .isEqualTo("bar");
assertThat(copy.get(Pair.typeInfo(String.class, Double.class), "bar"))
  .isEqualTo(Pair.of("x", 2.0));

MutableTypedMap<String> mutableAgain = copy.asMutable();
```