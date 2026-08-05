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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static de.flapdoodle.commons.eval.core.parser.TokenType.NUMBER_LITERAL;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenizerNumberLiteralTest extends BaseParserTest {

	@Test
	void testSingleDigit() throws ParseException {
		assertAllTokensParsedCorrectly("7", Token.of(0, "7", NUMBER_LITERAL));
	}

	@Test
	void testMultipleDigit() throws ParseException {
		assertAllTokensParsedCorrectly("888", Token.of(0, "888", NUMBER_LITERAL));
	}

	@Test
	void testBlanks() throws ParseException {
		assertAllTokensParsedCorrectly("\t 123 \r\n", Token.of(2, "123", NUMBER_LITERAL));
	}

	@Test
	void testDecimal() throws ParseException {
		assertAllTokensParsedCorrectly("123.834", Token.of(0, "123.834", NUMBER_LITERAL));
	}

	@Test
	void testDecimalStart() throws ParseException {
		assertAllTokensParsedCorrectly(".9", Token.of(0, ".9", NUMBER_LITERAL));
	}

	@Test
	void testDecimalEnd() throws ParseException {
		assertAllTokensParsedCorrectly("123.", Token.of(0, "123.", NUMBER_LITERAL));
	}

	@Test
	void testHexNumberSimple() throws ParseException {
		assertAllTokensParsedCorrectly("0x0", Token.of(0, "0x0", NUMBER_LITERAL));
	}

	@Test
	void testHexNumberLong() throws ParseException {
		assertAllTokensParsedCorrectly("0x3ABCDEF0", Token.of(0, "0x3ABCDEF0", NUMBER_LITERAL));
		assertAllTokensParsedCorrectly(
			" \t0x3abcdefAbcdef09873EE ", Token.of(2, "0x3abcdefAbcdef09873EE", NUMBER_LITERAL));
	}

	@Test
	void testHexNumberBlank() throws ParseException {
		assertAllTokensParsedCorrectly(
			" \t0x3abcdefAbcdef09873EE ", Token.of(2, "0x3abcdefAbcdef09873EE", NUMBER_LITERAL));
	}

	@Test
	void testSciOK() throws ParseException {
		assertAllTokensParsedCorrectly("2e1", Token.of(0, "2e1", NUMBER_LITERAL));
		assertAllTokensParsedCorrectly("2E1", Token.of(0, "2E1", NUMBER_LITERAL));
		assertAllTokensParsedCorrectly("2e-1", Token.of(0, "2e-1", NUMBER_LITERAL));
		assertAllTokensParsedCorrectly("2E-1", Token.of(0, "2E-1", NUMBER_LITERAL));
		assertAllTokensParsedCorrectly("2e+1", Token.of(0, "2e+1", NUMBER_LITERAL));
		assertAllTokensParsedCorrectly("2E+1", Token.of(0, "2E+1", NUMBER_LITERAL));
	}

	@ParameterizedTest
	@ValueSource(strings = { "2e", "2E", "2e+", "2E+", "2e-", "2E-", "2e.", "2E.", "2ex", "2Ex" })
	void testScientificLiteralsParseException(String expression) {
		assertThatThrownBy(() -> new Tokenizer(expression, operatorMap).parse())
			.isInstanceOf(ParseException.class)
			.hasMessage("Illegal scientific format");
	}
}
