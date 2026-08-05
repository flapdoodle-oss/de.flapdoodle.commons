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

import de.flapdoodle.commons.eval.core.Expression;
import de.flapdoodle.commons.eval.core.VariableResolver;
import de.flapdoodle.commons.eval.core.evaluables.OperatorMap;
import de.flapdoodle.commons.eval.core.evaluables.OperatorMapping;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.exceptions.ParseException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExpressionEvaluatorPowerOfTest extends BaseExpressionEvaluatorTest {

	@Test
	void testPrecedenceDefault() throws ParseException, EvaluationException {
		assertThat(evaluate("-2^2")).isEqualTo("4.0");
	}

	@Test
	void testPrecedenceHigher() throws ParseException, EvaluationException {
		Expression expression = Defaults.expressionFactory()
				.withOperatorMap(OperatorMap.builder()
					.putInfix("^", OperatorMapping.of(100,false,"power"))
					.build()
						.andThen(Defaults.operatorMap()))
				.parse("-2^2");

		VariableResolver variableResolver = VariableResolver.empty();
		assertThat(expression.evaluate(variableResolver).wrapped().toString()).isEqualTo("-4.0");
	}
}
