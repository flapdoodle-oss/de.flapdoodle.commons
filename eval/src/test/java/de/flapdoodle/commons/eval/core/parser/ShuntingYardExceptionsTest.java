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
package de.flapdoodle.commons.eval.core.parser;

import de.flapdoodle.commons.eval.core.Expression;
import de.flapdoodle.commons.eval.core.VariableResolver;
import de.flapdoodle.commons.eval.core.exceptions.ParseException;
import de.flapdoodle.commons.eval.example.Defaults;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static de.flapdoodle.commons.eval.core.parser.TokenType.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShuntingYardExceptionsTest extends BaseParserTest {

	static {
		Assertions.setMaxStackTraceElementsDisplayed(20);
	}

	@Test
	void testUnexpectedToken() {
		List<Token> tokens = Arrays.asList(Token.of(1, "x", FUNCTION_PARAM_START));
		assertThatThrownBy(new ShuntingYardConverter("x", tokens, operatorMap, evaluatables)::toAbstractSyntaxTree)
			.isInstanceOf(ParseException.class)
			.hasMessage("Unexpected token of type 'FUNCTION_PARAM_START'");
	}

	@Test
	void testMissingPrefixOperand() {
		List<Token> tokens = Arrays.asList(Token.of(1, "-", PREFIX_OPERATOR));
		assertThatThrownBy(new ShuntingYardConverter("-", tokens, operatorMap, evaluatables)::toAbstractSyntaxTree)
			.isInstanceOf(ParseException.class)
			.hasMessage("Missing operand for operator");
	}

	@Test
	void testMissingSecondInfixOperand() {
		List<Token> tokens =
			Arrays.asList(
				Token.of(1, "2", VARIABLE_OR_CONSTANT),
				Token.of(2, "*", INFIX_OPERATOR));
		assertThatThrownBy(new ShuntingYardConverter("2*", tokens, operatorMap, evaluatables)::toAbstractSyntaxTree)
			.isInstanceOf(ParseException.class)
			.hasMessage("Missing second operand for operator");
	}

	@Test
	void testEmptyExpression() {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse("");
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(ParseException.class)
			.hasMessage("Empty expression");
	}

	@Test
	void testEmptyExpressionBraces() {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse("()");
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(ParseException.class)
			.hasMessage("Empty expression");
	}

	@Test
	void testComma() {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse(",");
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(ParseException.class)
			.hasMessage("Empty expression");
	}

	@Test
	void testDoubleStructureOperator() {
		List<Token> tokens =
			Arrays.asList(Token.of(1, ".", STRUCTURE_SEPARATOR), Token.of(2, ".", STRUCTURE_SEPARATOR));
		assertThatThrownBy(new ShuntingYardConverter("..", tokens, operatorMap, evaluatables)::toAbstractSyntaxTree)
			.isInstanceOf(ParseException.class)
			.hasMessage("Missing operand for operator");
	}

	@Test
	void testStructureFollowsPostfixOperator() {
		List<Token> tokens =
			Arrays.asList(Token.of(1, ".", STRUCTURE_SEPARATOR), Token.of(2, "!", POSTFIX_OPERATOR));
		assertThatThrownBy(new ShuntingYardConverter("..", tokens, operatorMap, evaluatables)::toAbstractSyntaxTree)
			.isInstanceOf(ParseException.class)
			.hasMessage("Missing operand for operator");
	}

	@Test
	void testStructureFollowsTwoPostfixOperators() {
		List<Token> tokens =
			Arrays.asList(
				Token.of(1, ".", STRUCTURE_SEPARATOR),
				Token.of(2, "!", POSTFIX_OPERATOR),
				Token.of(2, "!", POSTFIX_OPERATOR));
		assertThatThrownBy(new ShuntingYardConverter("..", tokens, operatorMap, evaluatables)::toAbstractSyntaxTree)
			.isInstanceOf(ParseException.class)
			.hasMessage("Missing operand for operator");
	}

	@Test
	void testFunctionNotEnoughParameters() {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse("round()");
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(ParseException.class)
			.hasMessage("could not find evaluatable 'round' with 0 arguments");
	}

	@Test
	void testFunctionNotEnoughParametersForVarArgs() {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse("min()");
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(ParseException.class)
			.hasMessage("could not find evaluatable 'min' with 0 arguments");
	}

	@Test
	void testFunctionTooManyParameters() {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse("round(1,2,3,4)");
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(ParseException.class)
			.hasMessage("could not find evaluatable 'round' with 4 arguments");
	}

	@ParameterizedTest
	@ValueSource(
		strings = {
			"Hello, World",
			"Hello round(1,2) + (1 + 1)",
			"Hello round(1,2)",
			"Hello 1 + (1 + 1)",
			"Hello 1 + 1",
			"Hello World",
			"Hello 1",
			"1 2",
			"e.1",
			"E.1"
		})
	void testTooManyOperands(String expressionString) {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse(expressionString);
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(ParseException.class)
			.hasMessage("Too many operands");
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = { "(x+y)*(a-) : 9", "a** : 2", "5+, : 2" })
	void testInvalidTokenAfterInfixOperator(String expressionString, int position) {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse(expressionString);
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(ParseException.class)
			.hasMessage("Unexpected token after infix operator")
			.extracting("startPosition")
			.isEqualTo(position);
	}
}
