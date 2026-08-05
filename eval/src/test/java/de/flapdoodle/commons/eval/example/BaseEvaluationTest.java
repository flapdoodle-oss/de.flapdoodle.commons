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
import de.flapdoodle.commons.eval.core.ExpressionFactory;
import de.flapdoodle.commons.eval.core.VariableResolver;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.exceptions.ParseException;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.data.Percentage;

import java.math.BigDecimal;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public abstract class BaseEvaluationTest {

	protected static Value.BooleanValue asBoolean(String value) {
		switch (value) {
			case "true": return Value.of(true);
			case "false": return Value.of(false);
		}
		throw new IllegalArgumentException("is not a boolean: "+value);
	}

	protected static Value.NumberValue asNumber(String value) {
		return Value.of(new BigDecimal(value));
	}

	protected static Value.DurationValue asDuration(String value) {
		return Value.of(Duration.parse(value));
	}

	protected static Value.StringValue asString(String value) {
		return Value.of(value);
	}
	
	protected void assertExpressionHasExpectedResult(String expression, Value<?> expectedResult)
		throws EvaluationException, ParseException {
		Expression expression1 = TestConfigurationProvider.StandardFactoryWithAdditionalTestOperators.parse(expression);
		VariableResolver variableResolver = VariableResolver.empty();
		assertThat(
			expression1.evaluate(variableResolver).wrapped()
		).isEqualTo(expectedResult);
	}

	protected void assertExpressionHasExpectedResult(String expression, Value.NumberValue expectedResult)
		throws EvaluationException, ParseException {
		Expression expression1 = TestConfigurationProvider.StandardFactoryWithAdditionalTestOperators.parse(expression);
		VariableResolver variableResolver = VariableResolver.empty();
		assertThat(
			expression1.evaluate(variableResolver).wrapped())
			.isInstanceOf(Value.NumberValue.class)
			.asInstanceOf(InstanceOfAssertFactories.type(Value.NumberValue.class))
			.extracting(Value::wrapped, InstanceOfAssertFactories.BIG_DECIMAL)
			.isCloseTo(expectedResult.wrapped(), Percentage.withPercentage(0.99999));
	}

	protected void assertExpressionHasExpectedResult(
		String expression, String expectedResult, ExpressionFactory expressionFactory)
		throws EvaluationException, ParseException {
		assertThat(evaluate(expression, expressionFactory).toString())
			.isEqualTo(expectedResult);
	}

	protected void assertExpressionThrowsException(
		String expression, String message, ExpressionFactory factory) {
		assertThatThrownBy(() -> evaluate(expression, factory)).hasMessage(message);
	}

	private Object evaluate(String expressionString, ExpressionFactory factory)
		throws EvaluationException, ParseException {
		Expression expression = factory.parse(expressionString);

		return expression.evaluate(VariableResolver.empty()).wrapped();
	}

	protected static Value.NumberValue numberValueOf(String doubleAsString) {
		return asNumber(doubleAsString);
	}
}
