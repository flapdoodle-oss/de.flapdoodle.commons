## Flapdoodle Commons Collections
                                 
### GroupBy - create trees from lists

If you have a list with some records:

```java
${groupBy.records}
```

and you want this list grouped by street name and number, you can greate a grouping function by providing
the type of the list, a function to create the grouping entry for each list entry, a function to identify
the grouping attribute and a merge function where the grouping entry and the matching records are combined:

```java
${groupBy.group-by-address}
```

You can now apply this function to the list of records, and get the expected results:

```java
${groupBy.records-grouped-by-address}
```
                                    
You see that the city part is ignored, so that names for streets in different cities are grouped together. To fix that,
we can add another layer and group by the city name:

```java
${groupBy.group-by-city}
```

As you can see, we use the first group-by function in the second instance. Now we can apply this function to the list of records:                                 

```java
${groupBy.records-grouped-by-city}
```

Now the records are grouped by city and then by street name and number.
                                                                       
### TypedMap - different types in a single map

Create an immutable instance of TypedMap. A part of the key is a [TypeInfo](../../reflection/docs/Reflection.md) to ensure type safety:                       

```java
${immutableTypedMap}
```

Each change to the map creates a copy with the changed data. If you need a mutable version you can just create a mutable typed map:

```java
${mutableTypedMap}
```

.. and you can convert the one into the other and back:

```java
${typeMapMutableToImmutable}
```
