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
import de.flapdoodle.commons.eval.core.MapBasedVariableResolver;
import de.flapdoodle.commons.eval.core.VariableResolver;
import de.flapdoodle.commons.eval.core.evaluables.Evaluated;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.exceptions.ParseException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExpressionEvaluatorSimpleVariablesTest extends BaseExpressionEvaluatorTest {

	@Test
	void testSingleStringVariable() throws ParseException, EvaluationException {
        MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		Value<?> value = Value.of("hello");
		VariableResolver variableResolver = mapBasedValueResolver.with("a", Evaluated.value(value));
		Expression expression = createExpression("a");
		Object result = expression.evaluate(variableResolver).wrapped();
		assertThat(result).isEqualTo(Value.of("hello"));
	}

	@Test
	void testSingleNumberVariable() throws ParseException, EvaluationException {
		MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		Value<?> value = Value.of(BigDecimal.valueOf(9));
		VariableResolver variableResolver = mapBasedValueResolver.with("a", Evaluated.value(value));
		Expression expression = createExpression("a");
		Object result = expression.evaluate(variableResolver).wrapped();
		assertThat(result).isEqualTo(Value.of(BigDecimal.valueOf(9)));
	}

	@Test
	void testNumbers() throws ParseException, EvaluationException {
		MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		Value<?> value1 = Value.of(BigDecimal.valueOf(9));
		MapBasedVariableResolver mapBasedValueResolver1 = mapBasedValueResolver.with("a", Evaluated.value(value1));
		Value<?> value = Value.of(BigDecimal.valueOf(5));
		VariableResolver variableResolver = mapBasedValueResolver1.with("b", Evaluated.value(value));
		Expression expression = createExpression("(a+b)*(a-b)");
		Object result = expression.evaluate(variableResolver).wrapped();
		assertThat(result).isEqualTo(Value.of(BigDecimal.valueOf(56)));
	}

	@Test
	void testStrings() throws ParseException, EvaluationException {
        MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		Value<?> value2 = Value.of("Hello");
		MapBasedVariableResolver mapBasedValueResolver1 = mapBasedValueResolver.with("prefix", Evaluated.value(value2));
		Value<?> value1 = Value.of(" ");
		MapBasedVariableResolver mapBasedValueResolver2 = mapBasedValueResolver1.with("infix", Evaluated.value(value1));
		Value<?> value = Value.of("world");
		VariableResolver variableResolver = mapBasedValueResolver2.with("postfix", Evaluated.value(value));
		Expression expression = createExpression("prefix+infix+postfix");
		Object result = expression.evaluate(variableResolver).wrapped();
		assertThat(result).isEqualTo(Value.of("Hello world"));
	}

	@Test
	void testStringNumberCombined() throws ParseException, EvaluationException {
        MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		Value<?> value2 = Value.of("Hello");
		MapBasedVariableResolver mapBasedValueResolver2 = mapBasedValueResolver.with("prefix", Evaluated.value(value2));
		Value<?> value1 = Value.of(BigDecimal.valueOf(2));
		MapBasedVariableResolver mapBasedValueResolver1 = mapBasedValueResolver2.with("infix", Evaluated.value(value1));
		Value<?> value = Value.of("world");
		VariableResolver variableResolver = mapBasedValueResolver1.with("postfix", Evaluated.value(value));
		Expression expression = createExpression("prefix+infix+postfix");
		Object result = expression.evaluate(variableResolver).wrapped();
		assertThat(result).isEqualTo(Value.of("Hello2world"));
	}

	@Test
	void testUnknownVariable() {
		assertThatThrownBy(() -> {
			Expression expression = createExpression("a");
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessage("Variable or constant value for 'a' not found");
	}
}
