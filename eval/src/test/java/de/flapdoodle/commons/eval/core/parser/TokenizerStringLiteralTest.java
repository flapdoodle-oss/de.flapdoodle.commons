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
import de.flapdoodle.commons.eval.core.MapBasedVariableResolver;
import de.flapdoodle.commons.eval.core.VariableResolver;
import de.flapdoodle.commons.eval.core.exceptions.ParseException;
import de.flapdoodle.commons.eval.example.Defaults;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenizerStringLiteralTest extends BaseParserTest {

	@Test
	void testSimpleQuote() throws ParseException {
		assertAllTokensParsedCorrectly(
			"\"Hello, World\"", Token.of(0, "Hello, World", TokenType.STRING_LITERAL));
	}

	@Test
	void testSimpleQuoteLeadingBlanks() throws ParseException {
		assertAllTokensParsedCorrectly(
			"  \t\n \"Hello, World\"", Token.of(5, "Hello, World", TokenType.STRING_LITERAL));
	}

	@Test
	void testSimpleQuoteTrailingBlanks() throws ParseException {
		assertAllTokensParsedCorrectly(
			"\"Hello, World\"  \t\n ", Token.of(0, "Hello, World", TokenType.STRING_LITERAL));
	}

	@Test
	void testEscapeDoubleQuote() throws ParseException {
		assertAllTokensParsedCorrectly(
			"\"Hello, \\\"World\\\"\"", Token.of(0, "Hello, \"World\"", TokenType.STRING_LITERAL));
	}

	@Test
	void testEscapeSingleQuote() throws ParseException {
		assertAllTokensParsedCorrectly(
			"\"Hello, \\'World\\'\"", Token.of(0, "Hello, 'World'", TokenType.STRING_LITERAL));
	}

	@Test
	void testEscapeBackslash() throws ParseException {
		assertAllTokensParsedCorrectly(
			"\"a \\\\ b\"", Token.of(0, "a \\ b", TokenType.STRING_LITERAL));
	}

	@Test
	void testEscapeCharacters() throws ParseException {
		assertAllTokensParsedCorrectly(
			"\" \\t \\r \\n \\f \\b \"", Token.of(0, " \t \r \n \f \b ", TokenType.STRING_LITERAL));
	}

	@Test
	void testUnknownEscapeCharacter() {
		assertThatThrownBy(() -> {
			MapBasedVariableResolver mapBasedVariableResolver = VariableResolver.empty();
			Expression expression = Defaults.expressionFactory().parse("\" \\y \"");
			expression.evaluate(mapBasedVariableResolver).wrapped();
		})
			.isInstanceOf(ParseException.class)
			.hasMessage("Unknown escape character");
	}

	@Test
	void testSimpleQuoteOperation() throws ParseException {
		assertAllTokensParsedCorrectly(
			"\"Hello\" + \" \" + \"World\"",
			Token.of(0, "Hello", TokenType.STRING_LITERAL),
			Token.of(8, "+", TokenType.INFIX_OPERATOR),
			Token.of(10, " ", TokenType.STRING_LITERAL),
			Token.of(14, "+", TokenType.INFIX_OPERATOR),
			Token.of(16, "World", TokenType.STRING_LITERAL));
	}

	@Test
	void testErrorUnmatchedQuoteStart() {
		assertThatThrownBy(() -> new Tokenizer("\"hello", operatorMap).parse())
			.isInstanceOf(ParseException.class)
			.hasMessage("Closing quote not found");
	}

	@Test
	void testErrorUnmatchedQuoteOffset() {
		assertThatThrownBy(() -> new Tokenizer("test \"hello", operatorMap).parse())
			.isInstanceOf(ParseException.class)
			.hasMessage("Closing quote not found");
	}
}
