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

class TokenizerImplicitMultiplicationTest extends BaseParserTest {

	@Test
	void testImplicitBraces() throws ParseException {
		assertAllTokensParsedCorrectly(
			"(a+b)(a-b)",
			Token.of(0, "(", TokenType.BRACE_OPEN),
			Token.of(1, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(2, "+", TokenType.INFIX_OPERATOR),
			Token.of(3, "b", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(4, ")", TokenType.BRACE_CLOSE),
			Token.of(5, "*", TokenType.INFIX_OPERATOR),
			Token.of(5, "(", TokenType.BRACE_OPEN),
			Token.of(6, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(7, "-", TokenType.INFIX_OPERATOR),
			Token.of(8, "b", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(9, ")", TokenType.BRACE_CLOSE));
	}

	@Test
	void testImplicitNumberBraces() throws ParseException {
		assertAllTokensParsedCorrectly(
			"2(x)",
			Token.of(0, "2", TokenType.NUMBER_LITERAL),
			Token.of(1, "*", TokenType.INFIX_OPERATOR),
			Token.of(1, "(", TokenType.BRACE_OPEN),
			Token.of(2, "x", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(3, ")", TokenType.BRACE_CLOSE));
	}

	@Test
	void testImplicitNumberNoBraces() throws ParseException {
		assertAllTokensParsedCorrectly(
			"2x",
			Token.of(0, "2", TokenType.NUMBER_LITERAL),
			Token.of(1, "*", TokenType.INFIX_OPERATOR),
			Token.of(1, "x", TokenType.VARIABLE_OR_CONSTANT));
	}

	@Test
	void testImplicitNumberVariable() throws ParseException {
		assertAllTokensParsedCorrectly(
			"2x",
			Token.of(0, "2", TokenType.NUMBER_LITERAL),
			Token.of(1, "*", TokenType.INFIX_OPERATOR),
			Token.of(1, "x", TokenType.VARIABLE_OR_CONSTANT));
	}
}
