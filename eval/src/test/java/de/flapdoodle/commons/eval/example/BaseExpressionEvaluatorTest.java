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

import java.math.BigDecimal;

public abstract class BaseExpressionEvaluatorTest {

	final ExpressionFactory factory =
			TestConfigurationProvider.StandardFactoryWithAdditionalTestOperators;

	protected String evaluate(String expressionString) throws ParseException, EvaluationException {
		Expression expression = createExpression(expressionString);
		VariableResolver variableResolver = VariableResolver.empty();
		return expression.evaluate(variableResolver).wrapped().toString();
	}

	protected String evaluate(String expressionString, VariableResolver variableResolver) throws ParseException, EvaluationException {
		Expression expression = createExpression(expressionString);
		return expression.evaluate(variableResolver).wrapped().toString();
	}

	Expression createExpression(String expressionString) throws ParseException, EvaluationException {
		return factory.parse(expressionString);
	}

	protected static Value.NumberValue numberValueOf(String doubleAsString) {
		return Value.of(new BigDecimal(doubleAsString));
	}
}
