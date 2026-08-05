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

import de.flapdoodle.commons.eval.core.*;
import de.flapdoodle.commons.eval.core.evaluables.Evaluated;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.exceptions.ParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ExpressionEvaluatorConstantsTest extends BaseExpressionEvaluatorTest {

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"true : true",
			"false : false",
			"PI : "
				+ " 3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679",
			"E : 2.71828182845904523536028747135266249775724709369995957496696762772407663",
		})
	void testDefaultConstants(String expression, String expectedResult)
		throws ParseException, EvaluationException {
		assertThat(evaluate(expression)).isEqualTo(expectedResult);
	}

	@Test
	void testCustomConstantsMixedCase() throws EvaluationException, ParseException {
		Map<String, Evaluated<Value<?>>> constants =
			new HashMap<String, Evaluated<Value<?>>>() {
				{
					put("a", Evaluated.value(Value.of(new BigDecimal("2.5"))));
					put("B", Evaluated.value(Value.of(new BigDecimal("3.9"))));
				}
			};

		MapBasedVariableResolver mapBasedVariableResolver = VariableResolver.empty().withValues(constants);
		ExpressionFactory factory = Defaults.expressionFactory()
				.withConstants(mapBasedVariableResolver);

		Expression expression = factory.parse("a+B");

		VariableResolver variableResolver = VariableResolver.empty();
		assertThat(expression.evaluate(variableResolver).wrapped().toString()).isEqualTo("6.4");
	}

	@Test
	void testOverwriteConstantsWith() throws EvaluationException, ParseException {
		Expression expression = factory.withConstant("e", Evaluated.value(Value.of(9)))
				.parse("e");
		VariableResolver variableResolver = VariableResolver.empty();
		assertThat(expression.evaluate(variableResolver).wrapped().toString()).isEqualTo("9.0");
	}

	@Test
	void testOverwriteConstantsWithValues() throws EvaluationException, ParseException {
		MapBasedVariableResolver mapBasedVariableResolver1 = VariableResolver.empty();
		Value<?> value = Value.of(3);
		MapBasedVariableResolver mapBasedVariableResolver = mapBasedVariableResolver1.with("e", Evaluated.value(value));
		Expression expression = factory.withConstant("e", Evaluated.value(Value.of(6)))
				.parse("e");
		assertThat(expression.evaluate(mapBasedVariableResolver).wrapped().toString()).isEqualTo("6.0");
	}
}
