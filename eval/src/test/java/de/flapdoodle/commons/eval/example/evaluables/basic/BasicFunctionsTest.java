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
package de.flapdoodle.commons.eval.example.evaluables.basic;

import de.flapdoodle.commons.eval.core.Expression;
import de.flapdoodle.commons.eval.core.ImmutableExpressionFactory;
import de.flapdoodle.commons.eval.core.VariableResolver;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.exceptions.ParseException;
import de.flapdoodle.commons.eval.example.BaseEvaluationTest;
import de.flapdoodle.commons.eval.example.Defaults;
import de.flapdoodle.commons.eval.example.Value;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BasicFunctionsTest extends BaseEvaluationTest {

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"factorial(0) : 1",
			"factorial(1) : 1",
			"factorial(2) : 2",
			"factorial(3) : 6",
			"factorial(5) : 120",
			"factorial(10) : 3628800",
			"factorial(20) : 2432902008176640000"
		})
	void testFactorial(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asNumber(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"if(true, 4/2, 4/0) : 2",
			"if(true, 4/if(false, 5/0, 2*2), 4/0) : 1",
			"if(true, 6/if(false, 5/0, 2*if(true, 3, 6/0)), 4/0) : 1"
		})
	void testIf(String expression, String expectedResult) throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asNumber(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"max(99) : 99",
			"max(2,1) : 2",
			"max(1,9,-5,6,3,7) : 9",
			"max(17,88,77,66,609,1567,1876534) : 1876534"
		})
	void testMax(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asNumber(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"min(99) : 99",
			"min(2,1) : 1",
			"min(1,9,-5,6,3,7) : -5",
			"min(17,88,77,66,609,1567,1876534) : 17"
		})
	void testMin(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asNumber(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"round(1.1,0) : 1",
			"round(1.5,0) : 2",
			"round(2.34,1) : 2.3",
			"round(2.35,1) : 2.4",
			"round(2.323789,2) : 2.32",
			"round(2.324789,2) : 2.32"
		})
	void testRoundHalfEven(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asNumber(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
			delimiter = ':',
			value = {
					"round(1.1,\"up\") : 2",
					"round(1.9,\"down\") : 1",
					"round(1.5,\"half-up\") : 2",
					"round(1.5,\"half-down\") : 1",
					"round(1.1,\"ceiling\") : 2",
					"round(1.9,\"floor\") : 1",
			})
	void testRound(String expression, String expectedResult)
			throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asNumber(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
			delimiter = ':',
			value = {
					"round(2.323789,0,\"half-down\") : 2",
					"round(2.323789,1,\"half-down\") : 2.3",
					"round(2.323789,2,\"half-down\") : 2.32",
					"round(2.323789,3,\"half-down\") : 2.324"
			})
	void testPrecisionRound(String expression, String expectedResult)
			throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asNumber(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"round(1.1,0) : 2",
			"round(1.5,0) : 2",
			"round(2.34,1) : 2.4",
			"round(2.35,1) : 2.4",
			"round(2.323789,2) : 2.33",
			"round(2.324789,2) : 2.33"
		})
	void testRoundUp(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		ImmutableExpressionFactory factory = Defaults.expressionFactory()
				.withMathContext(new MathContext(32, RoundingMode.UP));
		assertExpressionHasExpectedResult(expression, expectedResult, factory);
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"sum(1) : 1",
			"sum(1,2,3,4) : 10",
			"sum(1,-1) : 0",
			"sum(1,10,100,1000,10000) : 11111",
			"sum(1,2,3,-3,-2,5) : 6"
		})
	void testSum(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asNumber(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"sqrt(0) : 0",
			"sqrt(1) : 1",
			"sqrt(2) : 1.41421356237309504880168872420969807856967187537694807317667973799073",
			"sqrt(4) : 2",
			"sqrt(5) : 2.23606797749978969640917366873127623544061835961152572427089724541052",
			"sqrt(10) : 3.16227766016837933199889354443271853371955513932521682685750485279259",
			"sqrt(365) : 19.10497317454280017916829575249669141539647233176799736525808213487",
			"sqrt(236769) : 486.58914907753543122473972072155030396245230523850016876894122736411182"
		})
	void testSqrt(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@Test
	void testSqrtNegative() {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse("sqrt(-1)");
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessage("value is not >= 0: -1");
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
//        "NOT(0) : true",
//        "NOT(1) : false",
//        "NOT(20) : false",
//        "NOT(\"true\") : false",
//        "NOT(\"false\") : true",
//        "NOT(2-4/2) : true",
			"not(true) : false",
			"not(false) : true",
		})
	void testBooleanNegation(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asBoolean(expectedResult));
	}

	@Test
	void testRandom() throws EvaluationException, ParseException {
		Expression expression1 = Defaults.expressionFactory().parse("random()");
		VariableResolver variableResolver1 = VariableResolver.empty();
		Object r1 = expression1.evaluate(variableResolver1).wrapped();
		Expression expression = Defaults.expressionFactory().parse("random()");
		VariableResolver variableResolver = VariableResolver.empty();
		Object r2 = expression.evaluate(variableResolver).wrapped();

		assertThat(r1).isNotEqualTo(r2);
	}

	@Test
	void testRandomInRange() throws EvaluationException, ParseException {
		Expression expression1 = Defaults.expressionFactory().parse("random(1.0, 2.0)");
		VariableResolver variableResolver = VariableResolver.empty();
		Value.NumberValue r1 = (Value.NumberValue) expression1.evaluate(variableResolver).wrapped();

		assertThat(r1).isGreaterThanOrEqualTo(Value.of(BigDecimal.ONE));
		assertThat(r1).isLessThanOrEqualTo(Value.of(BigDecimal.valueOf(2.0)));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"abs(0) : 0",
			"abs(1) : 1",
			"abs(-1) : 1",
			"abs(20) : 20",
			"abs(-20) : 20",
			"abs(2.12345) : 2.12345",
			"abs(-2.12345) : 2.12345"
		})
	void testAbs(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asNumber(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"floor(0) : 0",
			"floor(1) : 1",
			"floor(-1) : -1",
			"floor(20) : 20",
			"floor(-20) : -20",
			"floor(2.12345) : 2",
			"floor(-2.12345) : -3",
			"floor(-2.97345) : -3"
		})
	void testFloor(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asNumber(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"ceiling(0) : 0",
			"ceiling(1) : 1",
			"ceiling(-1) : -1",
			"ceiling(20) : 20",
			"ceiling(-20) : -20",
			"ceiling(2.12345) : 3",
			"ceiling(-2.12345) : -2",
			"ceiling(-2.97345) : -2"
		})
	void testCeiling(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asNumber(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"log(1) : 0",
			"log(10) : 2.302585092994046",
			"log(2.12345) : 0.7530421244614831",
			"log(1567) : 7.356918242356021"
		})
	void testLog(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@Test
	void testLogNegative() {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse("log(-1)");
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessage("value is not > 0: -1");
	}

	@Test
	void testLogZero() {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse("log(0)");
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessage("value is not > 0: 0");
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"log10(1) : 0",
			"log10(10) : 1",
			"log10(2.12345) : 0.3270420392943239",
			"log10(1567) : 3.1950689964685903"
		})
	void testLog10(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@Test
	void testLog10Negative() {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse("log10(-1)");
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessage("value is not > 0: -1");
	}

	@Test
	void testLog10Zero() {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse("log10(0)");
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessage("value is not > 0: 0");
	}
}
