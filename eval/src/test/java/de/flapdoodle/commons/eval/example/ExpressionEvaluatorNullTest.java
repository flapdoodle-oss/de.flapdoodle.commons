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
/*
  Copyright 2012-2023 Udo Klimaschewski

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package de.flapdoodle.commons.eval.example;

import de.flapdoodle.commons.eval.core.Expression;
import de.flapdoodle.commons.eval.core.MapBasedVariableResolver;
import de.flapdoodle.commons.eval.core.VariableResolver;
import de.flapdoodle.commons.eval.core.evaluables.Evaluated;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.exceptions.ParseException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExpressionEvaluatorNullTest extends BaseExpressionEvaluatorTest {

	@Test
	void testNullEquals() throws ParseException, EvaluationException {
		Expression expression = createExpression("a == null");
		MapBasedVariableResolver mapBasedValueResolver1 = VariableResolver.empty();
		MapBasedVariableResolver mapBasedVariableResolver1 = mapBasedValueResolver1.with("a", Evaluated.value((Value<?>) Value.ofNull()));
		assertExpressionHasExpectedResult(expression, mapBasedVariableResolver1, "true");
        MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		Value<?> value = Value.of(99);
		MapBasedVariableResolver mapBasedVariableResolver = mapBasedValueResolver.with("a", Evaluated.value(value));
		assertExpressionHasExpectedResult(expression, mapBasedVariableResolver, "false");
	}

	@Test
	void testNullNotEquals() throws ParseException, EvaluationException {
		Expression expression = createExpression("a != null");
		MapBasedVariableResolver mapBasedValueResolver1 = VariableResolver.empty();
		MapBasedVariableResolver mapBasedVariableResolver1 = mapBasedValueResolver1.with("a", Evaluated.value((Value<?>) Value.ofNull()));
		assertExpressionHasExpectedResult(expression, mapBasedVariableResolver1, "false");
        MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		Value<?> value = Value.of(99);
		MapBasedVariableResolver mapBasedVariableResolver = mapBasedValueResolver.with("a", Evaluated.value(value));
		assertExpressionHasExpectedResult(expression, mapBasedVariableResolver, "true");
	}

	@Test
	void testHandleWithIf() throws EvaluationException, ParseException {
		Expression expression1 = createExpression("if(a != null, a * 5, 1)");
		MapBasedVariableResolver mapBasedValueResolver3 = VariableResolver.empty();
		assertExpressionHasExpectedResult(expression1, mapBasedValueResolver3.with("a", Evaluated.value((Value<?>) Value.ofNull())), "1");
        MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		Value<?> value1 = Value.of(3);
		assertExpressionHasExpectedResult(expression1, mapBasedValueResolver.with("a", Evaluated.value(value1)), "15.0");

		Expression expression2 =
			createExpression("if(a == null, \"Unknown name\", \"The name is \" + a)");
		MapBasedVariableResolver mapBasedValueResolver2 = VariableResolver.empty();
		assertExpressionHasExpectedResult(expression2, mapBasedValueResolver2.with("a", Evaluated.value((Value<?>) Value.ofNull())), "Unknown name");
		MapBasedVariableResolver mapBasedValueResolver1 = VariableResolver.empty();
		Value<?> value = Value.of("Max");
		assertExpressionHasExpectedResult(expression2, mapBasedValueResolver1.with("a", Evaluated.value(value)), "The name is Max");
	}

	@Test
	void testHandleWithIfFailCase() throws EvaluationException, ParseException {
		assertThatThrownBy(() -> {
			MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
			evaluate("if(a == null, a * 5, 1)", mapBasedValueResolver.with("a", Evaluated.value((Value<?>) Value.ofNull())));
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessageContaining("no matching signature found");

		Expression expression2 =
			createExpression("if(a != null, \"Unknown name\", \"The name is \" + a)");
		MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		assertExpressionHasExpectedResult(expression2, mapBasedValueResolver.with("a", Evaluated.value((Value<?>) Value.ofNull())), "The name is null");
	}

	@Test
	void testHandleWithMaps() throws EvaluationException, ParseException {
		Expression expression = createExpression("a == null && b == null");
		Map<String, Evaluated<?>> values = new HashMap<>();
		values.put("a", Evaluated.value(Value.ofNull()));
		values.put("b", Evaluated.value(Value.ofNull()));

		MapBasedVariableResolver mapBasedVariableResolver = VariableResolver.empty().withValues(values);
		assertExpressionHasExpectedResult(expression, mapBasedVariableResolver, "true");
	}

	@Test
	void testFailWithNoHandling() {
		assertThatThrownBy(() -> {
			MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
			MapBasedVariableResolver mapBasedVariableResolver = mapBasedValueResolver.with("a", Evaluated.value((Value<?>) Value.ofNull()));
			evaluate("a * 5", mapBasedVariableResolver);
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessageContaining("no matching signature found");

		assertThatThrownBy(() -> {
			MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
			MapBasedVariableResolver mapBasedVariableResolver = mapBasedValueResolver.with("a", Evaluated.value((Value<?>) Value.ofNull()));
			evaluate("floor(a)", mapBasedVariableResolver);
		}).isInstanceOf(EvaluationException.class);

		assertThatThrownBy(() -> {
			MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
			MapBasedVariableResolver mapBasedVariableResolver = mapBasedValueResolver.with("a", Evaluated.value((Value<?>) Value.ofNull()));
			evaluate("a > 5", mapBasedVariableResolver);
		}).isInstanceOf(EvaluationException.class);
	}

	private void assertExpressionHasExpectedResult(Expression expression, VariableResolver variableResolver, String expectedResult)
		throws EvaluationException, ParseException {
		assertThat(expression.evaluate(variableResolver).wrapped().toString()).isEqualTo(expectedResult);
	}
}
