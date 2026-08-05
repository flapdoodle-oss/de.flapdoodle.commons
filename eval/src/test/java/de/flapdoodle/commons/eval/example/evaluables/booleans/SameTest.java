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
package de.flapdoodle.commons.eval.example.evaluables.booleans;

import de.flapdoodle.commons.eval.core.Expression;
import de.flapdoodle.commons.eval.core.MapBasedVariableResolver;
import de.flapdoodle.commons.eval.core.VariableResolver;
import de.flapdoodle.commons.eval.core.evaluables.Evaluated;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.exceptions.ParseException;
import de.flapdoodle.commons.eval.example.Defaults;
import de.flapdoodle.commons.eval.example.Value;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class SameTest {

	@Test
	void testInfixEqualsVariables() throws EvaluationException, ParseException {
		Expression expression = Defaults.expressionFactory()
				.parse("a===b");

		BigDecimal value = new BigDecimal("1.4");

		MapBasedVariableResolver mapBasedValueResolver8 = VariableResolver.empty();
		Value<?> value10 = Value.of(value);
		MapBasedVariableResolver mapBasedValueResolver9 = mapBasedValueResolver8.with("a", Evaluated.value(value10));
		Value<?> value9 = Value.of(value);
		VariableResolver variableResolver4 = mapBasedValueResolver9.with("b", Evaluated.value(value9));
		assertThat(expression.evaluate(variableResolver4).wrapped()
			)
			.isEqualTo(Value.TRUE);

		MapBasedVariableResolver mapBasedValueResolver6 = VariableResolver.empty();
		Value<?> value8 = Value.of("Hello");
		MapBasedVariableResolver mapBasedValueResolver7 = mapBasedValueResolver6.with("a", Evaluated.value(value8));
		Value<?> value7 = Value.of("Hello");
		VariableResolver variableResolver3 = mapBasedValueResolver7.with("b", Evaluated.value(value7));
		assertThat(expression.evaluate(variableResolver3).wrapped()
			)
			.isEqualTo(Value.TRUE);

		MapBasedVariableResolver mapBasedValueResolver4 = VariableResolver.empty();
		Value<?> value6 = Value.of("Hello");
		MapBasedVariableResolver mapBasedValueResolver5 = mapBasedValueResolver4.with("a", Evaluated.value(value6));
		Value<?> value5 = Value.of("Goodbye");
		VariableResolver variableResolver2 = mapBasedValueResolver5.with("b", Evaluated.value(value5));
		assertThat(expression.evaluate(variableResolver2).wrapped()
			)
			.isEqualTo(Value.FALSE);

        MapBasedVariableResolver mapBasedValueResolver2 = VariableResolver.empty();
		Value<?> value4 = Value.of(true);
		MapBasedVariableResolver mapBasedValueResolver3 = mapBasedValueResolver2.with("a", Evaluated.value(value4));
		Value<?> value3 = Value.of(true);
		VariableResolver variableResolver1 = mapBasedValueResolver3.with("b", Evaluated.value(value3));
		assertThat(expression.evaluate(variableResolver1).wrapped()
			)
			.isEqualTo(Value.TRUE);

        MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		Value<?> value2 = Value.of(false);
		MapBasedVariableResolver mapBasedValueResolver1 = mapBasedValueResolver.with("a", Evaluated.value(value2));
		Value<?> value1 = Value.of(true);
		VariableResolver variableResolver = mapBasedValueResolver1.with("b", Evaluated.value(value1));
		assertThat(expression.evaluate(variableResolver).wrapped()
			)
			.isEqualTo(Value.FALSE);
	}

}