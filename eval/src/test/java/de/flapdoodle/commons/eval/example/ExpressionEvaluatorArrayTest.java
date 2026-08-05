/*
 * Copyright (C) 2016
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.commons.eval.example;

import de.flapdoodle.commons.eval.core.MapBasedVariableResolver;
import de.flapdoodle.commons.eval.core.VariableResolver;
import de.flapdoodle.commons.eval.core.evaluables.Evaluated;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.exceptions.ParseException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExpressionEvaluatorArrayTest extends BaseExpressionEvaluatorTest {

	@Test
	void testSimpleArray() throws ParseException, EvaluationException {
		Value.ArrayValue array = Value.of(Value::of, Arrays.asList(new BigDecimal(99)));
		MapBasedVariableResolver mapBasedVariableResolver1 = VariableResolver.empty();
		MapBasedVariableResolver mapBasedVariableResolver = mapBasedVariableResolver1.with("a", Evaluated.value((Value<?>) array));
		assertThat(evaluate("a[0]", mapBasedVariableResolver)).isEqualTo("99");
	}

	@Test
	void testMultipleEntriesArray() throws ParseException, EvaluationException {
		Value.ArrayValue array = Value.of(Value::of, new BigDecimal(2), new BigDecimal(4), new BigDecimal(6));
		MapBasedVariableResolver mapBasedVariableResolver1 = VariableResolver.empty();
		MapBasedVariableResolver mapBasedVariableResolver = mapBasedVariableResolver1.with("a", Evaluated.value((Value<?>) array));
		assertThat(evaluate("a[0]+a[1]+a[2]", mapBasedVariableResolver)).isEqualTo("12");
	}

	@Test
	void testExpressionArray() throws ParseException, EvaluationException {
		Value.ArrayValue array = Value.of(Value::of, new BigDecimal(3));
		MapBasedVariableResolver mapBasedVariableResolver1 = VariableResolver.empty();
		MapBasedVariableResolver mapBasedVariableResolver2 = mapBasedVariableResolver1.with("a", Evaluated.value((Value<?>) array));
		Value<?> value = Value.of(new BigDecimal(4));
		MapBasedVariableResolver mapBasedVariableResolver = mapBasedVariableResolver2.and("x", Evaluated.value(value));
		assertThat(evaluate("a[4-x]", mapBasedVariableResolver)).isEqualTo("3");
	}

	@Test
	void testNestedArray() throws ParseException, EvaluationException {
		MapBasedVariableResolver mapBasedVariableResolver1 = VariableResolver.empty();
		Value<?> value = Value.of(Value::of, Arrays.asList(new BigDecimal(3)));
		MapBasedVariableResolver mapBasedVariableResolver2 = mapBasedVariableResolver1.with("a", Evaluated.value(value));
		Value<?> value1 = Value.of(Value::of, new BigDecimal(2), new BigDecimal(4), new BigDecimal(6));
		MapBasedVariableResolver mapBasedVariableResolver3 = mapBasedVariableResolver2.and("b", Evaluated.value(value1));
		Value<?> value2 = Value.of(new BigDecimal(6));
		MapBasedVariableResolver mapBasedVariableResolver = mapBasedVariableResolver3.and("x", Evaluated.value(value2));
		VariableResolver variableResolver = mapBasedVariableResolver;
		assertThat(evaluate("a[b[6-4]-x]", variableResolver)).isEqualTo("3");
	}

	@Test
	void testStringArray() throws ParseException, EvaluationException {
		Value.ArrayValue array = Value.of(Value::of, "Hello", "beautiful", "world");
		MapBasedVariableResolver mapBasedVariableResolver1 = VariableResolver.empty();
		MapBasedVariableResolver mapBasedVariableResolver = mapBasedVariableResolver1.with("a", Evaluated.value((Value<?>) array));
		assertThat(evaluate("a[0] + \" \" + a[1] + \" \" + a[2]", mapBasedVariableResolver)).isEqualTo("Hello beautiful world");
	}

	@Test
	void testBooleanArray() throws ParseException, EvaluationException {
		Value.ArrayValue array = Value.of(Value::of, true, true, false);
		MapBasedVariableResolver mapBasedVariableResolver1 = VariableResolver.empty();
		MapBasedVariableResolver mapBasedVariableResolver = mapBasedVariableResolver1.with("a", Evaluated.value((Value<?>) array));
		assertThat(evaluate("a[0] + \" \" + a[1] + \" \" + a[2]", mapBasedVariableResolver)).isEqualTo("true true false");
	}

	@Test
	void testArrayOfArray() throws EvaluationException, ParseException {
		Value.ArrayValue array = Value.of(
			Value.of(Value::of, new BigDecimal(1), new BigDecimal(2)),
			Value.of(Value::of, new BigDecimal(4), new BigDecimal(8))
		);

		MapBasedVariableResolver mapBasedVariableResolver7 = VariableResolver.empty();
		MapBasedVariableResolver mapBasedVariableResolver3 = mapBasedVariableResolver7.with("a", Evaluated.value((Value<?>) array));
		assertThat(evaluate("a[0][0]", mapBasedVariableResolver3)).isEqualTo("1");
		MapBasedVariableResolver mapBasedVariableResolver6 = VariableResolver.empty();
		MapBasedVariableResolver mapBasedVariableResolver2 = mapBasedVariableResolver6.with("a", Evaluated.value((Value<?>) array));
		assertThat(evaluate("a[0][1]", mapBasedVariableResolver2)).isEqualTo("2");
		MapBasedVariableResolver mapBasedVariableResolver5 = VariableResolver.empty();
		MapBasedVariableResolver mapBasedVariableResolver1 = mapBasedVariableResolver5.with("a", Evaluated.value((Value<?>) array));
		assertThat(evaluate("a[1][0]", mapBasedVariableResolver1)).isEqualTo("4");
		MapBasedVariableResolver mapBasedVariableResolver4 = VariableResolver.empty();
		MapBasedVariableResolver mapBasedVariableResolver = mapBasedVariableResolver4.with("a", Evaluated.value((Value<?>) array));
		assertThat(evaluate("a[1][1]", mapBasedVariableResolver)).isEqualTo("8");
	}

	@Test
	void testMixedArray() throws ParseException, EvaluationException {
		Value.ArrayValue array = Value.of(Value.of("Hello"), Value.of(new BigDecimal(4)), Value.of(true));

		MapBasedVariableResolver mapBasedVariableResolver5 = VariableResolver.empty();
		MapBasedVariableResolver mapBasedVariableResolver2 = mapBasedVariableResolver5.with("a", Evaluated.value((Value<?>) array));
		assertThat(evaluate("a[0]", mapBasedVariableResolver2)).isEqualTo("Hello");
		MapBasedVariableResolver mapBasedVariableResolver4 = VariableResolver.empty();
		MapBasedVariableResolver mapBasedVariableResolver1 = mapBasedVariableResolver4.with("a", Evaluated.value((Value<?>) array));
		assertThat(evaluate("a[1]", mapBasedVariableResolver1)).isEqualTo("4");
		MapBasedVariableResolver mapBasedVariableResolver3 = VariableResolver.empty();
		MapBasedVariableResolver mapBasedVariableResolver = mapBasedVariableResolver3.with("a", Evaluated.value((Value<?>) array));
		assertThat(evaluate("a[2]", mapBasedVariableResolver)).isEqualTo("true");
	}

	@Test
	void stringDataTypeForArray() throws ParseException, EvaluationException {
		MapBasedVariableResolver mapBasedVariableResolver1 = VariableResolver.empty();
		Value<?> value = Value.of("aString");
		MapBasedVariableResolver mapBasedVariableResolver = mapBasedVariableResolver1.with("a", Evaluated.value(value));
		assertThat(evaluate("a[1]", mapBasedVariableResolver)).isEqualTo("S");
	}

	@Test
	void testThrowsUnsupportedDataTypeForArray() {
		assertThatThrownBy(() -> {
			MapBasedVariableResolver mapBasedVariableResolver1 = VariableResolver.empty();
			Value<?> value = Value.of(123);
			MapBasedVariableResolver mapBasedVariableResolver = mapBasedVariableResolver1.with("a", Evaluated.value(value));
			evaluate("a[0]", mapBasedVariableResolver);
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessageContaining("no matching signature found");
	}

	@Test
	void testThrowsUnsupportedDataTypeForIndex() {
		assertThatThrownBy(
			() -> {
				Value.ArrayValue array = Value.of(Value::of, "Hello");
				MapBasedVariableResolver mapBasedVariableResolver1 = VariableResolver.empty();
				MapBasedVariableResolver mapBasedVariableResolver2 = mapBasedVariableResolver1.with("a", Evaluated.value((Value<?>) array));
				Value<?> value = Value.of("anotherString");
				MapBasedVariableResolver mapBasedVariableResolver = mapBasedVariableResolver2.and("b", Evaluated.value(value));
				evaluate("a[b]", mapBasedVariableResolver);
			})
			.isInstanceOf(EvaluationException.class)
			.hasMessageContaining("no matching signature found");
	}
}
