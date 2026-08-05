## Flapdoodle Commons Eval

```java
ExpressionFactory expressionFactory = Defaults.expressionFactory();
Expression expression = expressionFactory.parse("a*2");
VariableResolver variableResolver = VariableResolver.empty()
  .with("a", Evaluated.value(Value.of(2)));
Object result = expression.evaluate(variableResolver).wrapped();

assertThat(result).isEqualTo(Value.of(4.0));
```

### Used Variable Names

```java
ExpressionFactory expressionFactory = Defaults.expressionFactory();
Expression expression = expressionFactory.parse("a*2");
assertThat(expression.usedVariables())
  .containsExactly("a");
```

### Used Variable Names and Hash

If you change only the name of a variable, then the hash may not change.

```java
assertThat(expressionFactory.parse("a*2+b").usedVariablesWithHash())
  .containsExactly(
    MapEntry.entry("a",1546),
    MapEntry.entry("b",47022362)
  );
```

Same expression as above, different variable names:                                                    

```java
assertThat(expressionFactory.parse("X*2+z").usedVariablesWithHash())
  .containsExactly(
    MapEntry.entry("X",1546),
    MapEntry.entry("z",47022362)
  );
```

### Custom Setup

```java
ImmutableTypedEvaluables add = TypedEvaluables.builder()
  .addList(TypedEvaluable.of(BigDecimal.class, BigDecimal.class, BigDecimal.class,
    (valueResolver, evaluationContext, token, first, second) -> first.add(second)))
  .build();

ExpressionFactory expressionFactory = ExpressionFactory.builder()
  .constants(VariableResolver.empty().with("pi", Evaluated.value(BigDecimal.valueOf(3.1415))))
  .evaluatables(TypedEvaluableMap.builder()
    .putMap("add", add)
    .build())
  .operatorMap(OperatorMap.builder()
    .putInfix("+", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_ADDITIVE, "add"))
    .build())
  .arrayAccess(TypedEvaluables.builder()
    .addList(TypedEvaluable.of(String.class, String.class, BigDecimal.class,
      (valueResolver, evaluationContext, token, first, second) -> "" + first.charAt(second.intValue())))
    .build())
  .associateAccess(TypedEvaluables.builder()
    .addList(TypedEvaluable.of(String.class, String.class, BigDecimal.class,
      (valueResolver, evaluationContext, token, first, second) -> "" + first.charAt(second.intValue())))
    .build())
  .propertyAccess(TypedEvaluables.builder()
    .addList(TypedEvaluable.of(String.class, Map.class, String.class,
      (valueResolver, evaluationContext, token, first, second) -> "" + first.get(second)))
    .build())
  .numberAsValue((s, m) -> new BigDecimal(s))
  .stringAsValue(s -> s)
  .exceptionMapper(EvalFailedWithException.mapper())
  .build();

assertThat(expressionFactory.parse("pi")
  .evaluate(VariableResolver.empty()).wrapped())
  .isEqualTo(BigDecimal.valueOf(3.1415));

assertThat(expressionFactory.parse("add(2,3)")
  .evaluate(VariableResolver.empty()).wrapped())
  .isEqualTo(BigDecimal.valueOf(5L));

assertThat(expressionFactory.parse("2+3")
  .evaluate(VariableResolver.empty()).wrapped())
  .isEqualTo(BigDecimal.valueOf(5L));

assertThat(expressionFactory.parse("\"fun\"[1]")
  .evaluate(VariableResolver.empty()).wrapped())
  .isEqualTo("u");

assertThat(expressionFactory.parse("\"fun\"{1}")
  .evaluate(VariableResolver.empty()).wrapped())
  .isEqualTo("u");

MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
Map<Object, Object> value = new LinkedHashMap<>();
value.put("key", "stuff");

assertThat(expressionFactory.parse("map.key")
  .evaluate(mapBasedValueResolver.with("map", Evaluated.value(value))).wrapped())
  .isEqualTo("stuff");

```