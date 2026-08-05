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

class TokenizerExpressionTest extends BaseParserTest {

	@Test
	void testSingleNumber() throws ParseException {
		assertAllTokensParsedCorrectly("1", Token.of(0, "1", TokenType.NUMBER_LITERAL));
	}

	@Test
	void testSingleVariable() throws ParseException {
		assertAllTokensParsedCorrectly("a", Token.of(0, "a", TokenType.VARIABLE_OR_CONSTANT));
	}

	@Test
	void testSimple() throws ParseException {
		assertAllTokensParsedCorrectly(
			"a+123",
			Token.of(0, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(1, "+", TokenType.INFIX_OPERATOR),
			Token.of(2, "123", TokenType.NUMBER_LITERAL));
	}

	@Test
	void testTwo() throws ParseException {
		assertAllTokensParsedCorrectly(
			"a+123+c",
			Token.of(0, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(1, "+", TokenType.INFIX_OPERATOR),
			Token.of(2, "123", TokenType.NUMBER_LITERAL),
			Token.of(5, "+", TokenType.INFIX_OPERATOR),
			Token.of(6, "c", TokenType.VARIABLE_OR_CONSTANT));
	}

	@Test
	void testUndefinedOperator() {
		assertThatThrownBy(() -> new Tokenizer("a $ b", operatorMap).parse())
			.isEqualTo(new ParseException(2, 2, "$", "Undefined operator '$'"));
	}
}
