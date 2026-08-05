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

import static org.assertj.core.api.Assertions.assertThat;

class ExpressionEvaluatorCombinedTest extends BaseExpressionEvaluatorTest {

	@Test
	void testOrderPositionExample() throws ParseException, EvaluationException {
		ImmutableValueMap position = ValueMap.builder()
			.putValues("article", Value.of(3114))
			.putValues("amount", Value.of(3))
			.putValues("price", Value.of(new BigDecimal("14.95")))
			.build();

		ImmutableValueMap order = ValueMap.builder()
			.putValues("id", Value.of(12345))
			.putValues("name", Value.of("Mary"))
			.putValues("positions", Value.of(Value::of, position))
			.build();

		MapBasedVariableResolver mapBasedVariableResolver1 = VariableResolver.empty();
		Value<?> value = Value.of(order);
		MapBasedVariableResolver mapBasedVariableResolver2 = mapBasedVariableResolver1.with("order", Evaluated.value(value));
		Value<?> value1 = Value.of(0);
		MapBasedVariableResolver mapBasedVariableResolver = mapBasedVariableResolver2.and("x", Evaluated.value(value1));
		VariableResolver variableResolver = mapBasedVariableResolver;
		assertThat(evaluate("order.positions[x].amount * order.positions[x].price", variableResolver)).isEqualTo("44.850");
	}
}
