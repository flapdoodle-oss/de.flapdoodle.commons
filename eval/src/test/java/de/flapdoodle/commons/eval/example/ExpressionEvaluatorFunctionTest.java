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

import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.exceptions.ParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class ExpressionEvaluatorFunctionTest extends BaseExpressionEvaluatorTest {

	@Test
	void testSingleParameterFunction() throws ParseException, EvaluationException {
		assertThat(evaluate("factorial(3)")).isEqualTo("6");
	}

	@Test
	void testDualParameterFunction() throws ParseException, EvaluationException {
		assertThat(evaluate("round(3.123, 2)")).isEqualTo("3.12");
	}

	@Test
	void testNestedFunctions() throws ParseException, EvaluationException {
		assertThat(evaluate("factorial(round(3.95, 0))")).isEqualTo("24");
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = { "max(5) : 5", "max(4,6,3,8) : 8" })
	void testVarArgFunction(String expressionString, String expectedResult)
		throws ParseException, EvaluationException {
		assertThat(evaluate(expressionString)).isEqualTo(expectedResult);
	}
}
