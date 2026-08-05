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

import de.flapdoodle.commons.eval.core.evaluables.OperatorMap;
import de.flapdoodle.commons.eval.core.evaluables.OperatorMapping;
import de.flapdoodle.commons.eval.core.evaluables.Precedence;
import de.flapdoodle.commons.eval.core.exceptions.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TokenizerLiteralOperatorsTest extends BaseParserTest {

	@BeforeEach
	public void setup() {
		factory =
			factory.withOperatorMap(OperatorMap.builder()
				.putInfix("AND", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_AND,"--and--"))
				.putInfix("OR", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_OR,"--or--"))
				.putPrefix("NOT", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_UNARY,"--not--"))
				.putPostfix("DENIED", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_UNARY,"--denied--"))
				.build());
	}

	@Test
	void testAndOrNot() throws ParseException {
		assertAllTokensParsedCorrectly(
			"NOT a AND b DENIED OR NOT(c)",
			Token.of(0, "NOT", TokenType.PREFIX_OPERATOR),
			Token.of(4, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(6, "AND", TokenType.INFIX_OPERATOR),
			Token.of(10, "b", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(12, "DENIED", TokenType.POSTFIX_OPERATOR),
			Token.of(19, "OR", TokenType.INFIX_OPERATOR),
			Token.of(22, "NOT", TokenType.PREFIX_OPERATOR),
			Token.of(25, "(", TokenType.BRACE_OPEN),
			Token.of(26, "c", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(27, ")", TokenType.BRACE_CLOSE));
	}

}
