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

import de.flapdoodle.commons.eval.core.exceptions.ParseException;
import org.junit.jupiter.api.Test;

class TokenizerFunctionsTest extends BaseParserTest {

	@Test
	void testSimple() throws ParseException {
		assertAllTokensParsedCorrectly(
			"f(x)",
			Token.of(0, "f", TokenType.FUNCTION),
			Token.of(1, "(", TokenType.BRACE_OPEN),
			Token.of(2, "x", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(3, ")", TokenType.BRACE_CLOSE));
	}

	@Test
	void testBlanks() throws ParseException {
		assertAllTokensParsedCorrectly(
			"f (x)",
			Token.of(0, "f", TokenType.FUNCTION),
			Token.of(2, "(", TokenType.BRACE_OPEN),
			Token.of(3, "x", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(4, ")", TokenType.BRACE_CLOSE));
	}

	@Test
	void testUnderscores() throws ParseException {
		assertAllTokensParsedCorrectly(
			"_f_x_(x)",
			Token.of(0, "_f_x_", TokenType.FUNCTION),
			Token.of(5, "(", TokenType.BRACE_OPEN),
			Token.of(6, "x", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(7, ")", TokenType.BRACE_CLOSE));
	}

	@Test
	void testWithNumbers() throws ParseException {
		assertAllTokensParsedCorrectly(
			"f1x2(x)",
			Token.of(0, "f1x2", TokenType.FUNCTION),
			Token.of(4, "(", TokenType.BRACE_OPEN),
			Token.of(5, "x", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(6, ")", TokenType.BRACE_CLOSE));
	}

	@Test
	void testWithMoreParameters() throws ParseException {
		assertAllTokensParsedCorrectly(
			"sum(a, 2, 3)",
			Token.of(0, "sum", TokenType.FUNCTION),
			Token.of(3, "(", TokenType.BRACE_OPEN),
			Token.of(4, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(5, ",", TokenType.COMMA),
			Token.of(7, "2", TokenType.NUMBER_LITERAL),
			Token.of(8, ",", TokenType.COMMA),
			Token.of(10, "3", TokenType.NUMBER_LITERAL),
			Token.of(11, ")", TokenType.BRACE_CLOSE));
	}

	@Test
	void testWithMixedParameters() throws ParseException {
		assertAllTokensParsedCorrectly(
			"TEST(a, \"hello\", 3)",
			Token.of(0, "TEST", TokenType.FUNCTION),
			Token.of(4, "(", TokenType.BRACE_OPEN),
			Token.of(5, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(6, ",", TokenType.COMMA),
			Token.of(8, "hello", TokenType.STRING_LITERAL),
			Token.of(15, ",", TokenType.COMMA),
			Token.of(17, "3", TokenType.NUMBER_LITERAL),
			Token.of(18, ")", TokenType.BRACE_CLOSE));
	}

	@Test
	void testFunctionInParameter() throws ParseException {
		assertAllTokensParsedCorrectly(
			"TEST(a, factorial(x), 3)",
			Token.of(0, "TEST", TokenType.FUNCTION),
			Token.of(4, "(", TokenType.BRACE_OPEN),
			Token.of(5, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(6, ",", TokenType.COMMA),
			Token.of(8, "factorial", TokenType.FUNCTION),
			Token.of(17, "(", TokenType.BRACE_OPEN),
			Token.of(18, "x", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(19, ")", TokenType.BRACE_CLOSE),
			Token.of(20, ",", TokenType.COMMA),
			Token.of(22, "3", TokenType.NUMBER_LITERAL),
			Token.of(23, ")", TokenType.BRACE_CLOSE));
	}

	@Test
	void testFunctionInParameterInFunctionParameter() throws ParseException {
		assertAllTokensParsedCorrectly(
			"sum(a,factorial(min(x,y)),3)",
			Token.of(0, "sum", TokenType.FUNCTION),
			Token.of(3, "(", TokenType.BRACE_OPEN),
			Token.of(4, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(5, ",", TokenType.COMMA),
			Token.of(6, "factorial", TokenType.FUNCTION),
			Token.of(15, "(", TokenType.BRACE_OPEN),
			Token.of(16, "min", TokenType.FUNCTION),
			Token.of(19, "(", TokenType.BRACE_OPEN),
			Token.of(20, "x", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(21, ",", TokenType.COMMA),
			Token.of(22, "y", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(23, ")", TokenType.BRACE_CLOSE),
			Token.of(24, ")", TokenType.BRACE_CLOSE),
			Token.of(25, ",", TokenType.COMMA),
			Token.of(26, "3", TokenType.NUMBER_LITERAL),
			Token.of(27, ")", TokenType.BRACE_CLOSE));
	}
}
