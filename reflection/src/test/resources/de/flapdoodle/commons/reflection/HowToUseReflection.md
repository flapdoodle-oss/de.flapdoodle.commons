# Flapdoodle Commons Reflection

Sometimes you can not rely on casting an instance to an expected type. You
have to be sure. With a well crafted TypeInfo these check is simple.

In this first example we check if an object is an instance of List&lt;String&gt;:

```java
${firstExample}
```

but type infos can be nestet, so complex type checks are easy:

```java
${complexTypeChecksAndCasts}
```
