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

class TokenizerArrayTest extends BaseParserTest {

	@Test
	void testArraySimple() throws ParseException {
		assertAllTokensParsedCorrectly(
			"a[1]",
			Token.of(0, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(1, "[", TokenType.ARRAY_OPEN),
			Token.of(2, "1", TokenType.NUMBER_LITERAL),
			Token.of(3, "]", TokenType.ARRAY_CLOSE));
	}

	@Test
	void testArrayNested() throws ParseException {
		assertAllTokensParsedCorrectly(
			"a[b[2] + c[3]]",
			Token.of(0, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(1, "[", TokenType.ARRAY_OPEN),
			Token.of(2, "b", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(3, "[", TokenType.ARRAY_OPEN),
			Token.of(4, "2", TokenType.NUMBER_LITERAL),
			Token.of(5, "]", TokenType.ARRAY_CLOSE),
			Token.of(7, "+", TokenType.INFIX_OPERATOR),
			Token.of(9, "c", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(10, "[", TokenType.ARRAY_OPEN),
			Token.of(11, "3", TokenType.NUMBER_LITERAL),
			Token.of(12, "]", TokenType.ARRAY_CLOSE),
			Token.of(13, "]", TokenType.ARRAY_CLOSE));
	}

	@Test
	void testMissingClosingArray() {
		assertThatThrownBy(() -> new Tokenizer("a[2+4", operatorMap).parse())
			.isEqualTo(new ParseException(1, 5, "a[2+4", "Closing array not found"));
	}

	@Test
	void testUnexpectedClosingArray() {
		assertThatThrownBy(() -> new Tokenizer("a[2+4]]", operatorMap).parse())
			.isEqualTo(new ParseException(6, 6, "]", "Unexpected closing array"));
	}

	@Test
	void testOpenArrayNotAllowedBeginning() {
		assertThatThrownBy(() -> new Tokenizer("[1]", operatorMap).parse())
			.isEqualTo(new ParseException(0, 0, "[", "Array open not allowed here"));
	}

	@Test
	void testOpenArrayNotAllowedAfterOperator() {
		assertThatThrownBy(() -> new Tokenizer("1+[1]", operatorMap).parse())
			.isEqualTo(new ParseException(2, 2, "[", "Array open not allowed here"));
	}

	@Test
	void testOpenArrayNotAllowedAfterBrace() {
		assertThatThrownBy(() -> new Tokenizer("([1]", operatorMap).parse())
			.isEqualTo(new ParseException(1, 1, "[", "Array open not allowed here"));
	}

	@Test
	void testCloseArrayNotAllowedBeginning() {
		assertThatThrownBy(() -> new Tokenizer("]", operatorMap).parse())
			.isEqualTo(new ParseException(0, 0, "]", "Array close not allowed here"));
	}

	@Test
	void testCloseArrayNotAllowedAfterBrace() {
		assertThatThrownBy(() -> new Tokenizer("(]", operatorMap).parse())
			.isEqualTo(new ParseException(1, 1, "]", "Array close not allowed here"));
	}
}
