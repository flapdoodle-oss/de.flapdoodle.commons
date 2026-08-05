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

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenizerPrefixPostfixTest extends BaseParserTest {

	@Test
	void testPrefixSingle() throws ParseException {
		assertAllTokensParsedCorrectly(
			"++a",
			Token.of(0, "++", TokenType.PREFIX_OPERATOR),
			Token.of(2, "a", TokenType.VARIABLE_OR_CONSTANT));
	}

	@Test
	void testPostfixSingle() throws ParseException {
		assertAllTokensParsedCorrectly(
			"a++",
			Token.of(0, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(1, "++", TokenType.POSTFIX_OPERATOR));
	}

	@Test
	void testPostfixAsPrefixThrowsException() {
		assertThatThrownBy(new Tokenizer("?a", operatorMap)::parse)
			.isInstanceOf(ParseException.class)
			.hasMessage("Undefined operator '?'");
	}

	@Test
	void testPrefixAndPostfix() throws ParseException {
		// note: if this is supported, depends on the operator and what type it expects as operand
		assertAllTokensParsedCorrectly(
			"++a++",
			Token.of(0, "++", TokenType.PREFIX_OPERATOR),
			Token.of(2, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(3, "++", TokenType.POSTFIX_OPERATOR));
	}

	@Test
	void testPrefixWithInfixAndPostfix() throws ParseException {
		assertAllTokensParsedCorrectly(
			"++a+a++",
			Token.of(0, "++", TokenType.PREFIX_OPERATOR),
			Token.of(2, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(3, "+", TokenType.INFIX_OPERATOR),
			Token.of(4, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(5, "++", TokenType.POSTFIX_OPERATOR));
	}

	@Test
	void testPrefixWithBraces() throws ParseException {
		assertAllTokensParsedCorrectly(
			"(++a)+(a++)",
			Token.of(0, "(", TokenType.BRACE_OPEN),
			Token.of(1, "++", TokenType.PREFIX_OPERATOR),
			Token.of(3, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(4, ")", TokenType.BRACE_CLOSE),
			Token.of(5, "+", TokenType.INFIX_OPERATOR),
			Token.of(6, "(", TokenType.BRACE_OPEN),
			Token.of(7, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(8, "++", TokenType.POSTFIX_OPERATOR),
			Token.of(10, ")", TokenType.BRACE_CLOSE));
	}

	@Test
	void testPrefixWithFunction() throws ParseException {
		assertAllTokensParsedCorrectly(
			"++max(++a,a++,b++)++",
			Token.of(0, "++", TokenType.PREFIX_OPERATOR),
			Token.of(2, "max", TokenType.FUNCTION),
			Token.of(5, "(", TokenType.BRACE_OPEN),
			Token.of(6, "++", TokenType.PREFIX_OPERATOR),
			Token.of(8, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(9, ",", TokenType.COMMA),
			Token.of(10, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(11, "++", TokenType.POSTFIX_OPERATOR),
			Token.of(13, ",", TokenType.COMMA),
			Token.of(14, "b", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(15, "++", TokenType.POSTFIX_OPERATOR),
			Token.of(17, ")", TokenType.BRACE_CLOSE),
			Token.of(18, "++", TokenType.POSTFIX_OPERATOR));
	}

	@Test
	void testPrefixWithStringLiteral() throws ParseException {
		assertAllTokensParsedCorrectly(
			"++\"hello\"++",
			Token.of(0, "++", TokenType.PREFIX_OPERATOR),
			Token.of(2, "hello", TokenType.STRING_LITERAL),
			Token.of(9, "++", TokenType.POSTFIX_OPERATOR));
	}

	@Test
	void testPrefixWithUnaryAndBinary() throws ParseException {
		assertAllTokensParsedCorrectly(
			"-a - -b++",
			Token.of(0, "-", TokenType.PREFIX_OPERATOR),
			Token.of(1, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(3, "-", TokenType.INFIX_OPERATOR),
			Token.of(5, "-", TokenType.PREFIX_OPERATOR),
			Token.of(6, "b", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(7, "++", TokenType.POSTFIX_OPERATOR));
	}
}
