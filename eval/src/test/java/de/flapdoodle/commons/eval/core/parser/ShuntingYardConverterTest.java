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

class ShuntingYardConverterTest extends BaseParserTest {

	@Test
	void testSingleNumber() throws ParseException {
		assertASTTreeIsEqualTo("1", "{\"type\":\"NUMBER_LITERAL\",\"value\":\"1\"}");
	}

	@Test
	void testSingleVariable() throws ParseException {
		assertASTTreeIsEqualTo("a", "{\"type\":\"VARIABLE_OR_CONSTANT\",\"value\":\"a\"}");
	}

	@Test
	void testPrefix() throws ParseException {
		assertASTTreeIsEqualTo(
			"-1",
			"{\"type\":\"PREFIX_OPERATOR\",\"value\":\"-\",\"children\":[{\"type\":\"NUMBER_LITERAL\",\"value\":\"1\"}]}");
	}

	@Test
	void testPostfix() throws ParseException {
		assertASTTreeIsEqualTo(
			"1?",
			"{\"type\":\"POSTFIX_OPERATOR\",\"value\":\"?\",\"children\":[{\"type\":\"NUMBER_LITERAL\",\"value\":\"1\"}]}");
	}

	@Test
	void testPrefixPostfix() throws ParseException {
		assertASTTreeIsEqualTo(
			"-1?",
			"{\"type\":\"PREFIX_OPERATOR\",\"value\":\"-\",\"children\":[{\"type\":\"POSTFIX_OPERATOR\",\"value\":\"?\",\"children\":[{\"type\":\"NUMBER_LITERAL\",\"value\":\"1\"}]}]}");
	}

	@Test
	void testSequential() throws ParseException {
		assertASTTreeIsEqualTo(
			"1+2+3-3-2-1",
			"{\"type\":\"INFIX_OPERATOR\",\"value\":\"-\",\"children\":[{\"type\":\"INFIX_OPERATOR\",\"value\":\"-\",\"children\":[{\"type\":\"INFIX_OPERATOR\",\"value\":\"-\",\"children\":[{\"type\":\"INFIX_OPERATOR\",\"value\":\"+\",\"children\":[{\"type\":\"INFIX_OPERATOR\",\"value\":\"+\",\"children\":[{\"type\":\"NUMBER_LITERAL\",\"value\":\"1\"},{\"type\":\"NUMBER_LITERAL\",\"value\":\"2\"}]},{\"type\":\"NUMBER_LITERAL\",\"value\":\"3\"}]},{\"type\":\"NUMBER_LITERAL\",\"value\":\"3\"}]},{\"type\":\"NUMBER_LITERAL\",\"value\":\"2\"}]},{\"type\":\"NUMBER_LITERAL\",\"value\":\"1\"}]}");
	}

	@Test
	void testPrecedence() throws ParseException {
		assertASTTreeIsEqualTo(
			"1+2*3-3^2-1/4",
			"{\"type\":\"INFIX_OPERATOR\",\"value\":\"-\",\"children\":[{\"type\":\"INFIX_OPERATOR\",\"value\":\"-\",\"children\":[{\"type\":\"INFIX_OPERATOR\",\"value\":\"+\",\"children\":[{\"type\":\"NUMBER_LITERAL\",\"value\":\"1\"},{\"type\":\"INFIX_OPERATOR\",\"value\":\"*\",\"children\":[{\"type\":\"NUMBER_LITERAL\",\"value\":\"2\"},{\"type\":\"NUMBER_LITERAL\",\"value\":\"3\"}]}]},{\"type\":\"INFIX_OPERATOR\",\"value\":\"^\",\"children\":[{\"type\":\"NUMBER_LITERAL\",\"value\":\"3\"},{\"type\":\"NUMBER_LITERAL\",\"value\":\"2\"}]}]},{\"type\":\"INFIX_OPERATOR\",\"value\":\"/\",\"children\":[{\"type\":\"NUMBER_LITERAL\",\"value\":\"1\"},{\"type\":\"NUMBER_LITERAL\",\"value\":\"4\"}]}]}");
	}

	@Test
	void testBraces() throws ParseException {
		assertASTTreeIsEqualTo(
			"2*(1/(2+3))",
			"{\"type\":\"INFIX_OPERATOR\",\"value\":\"*\",\"children\":[{\"type\":\"NUMBER_LITERAL\",\"value\":\"2\"},{\"type\":\"INFIX_OPERATOR\",\"value\":\"/\",\"children\":[{\"type\":\"NUMBER_LITERAL\",\"value\":\"1\"},{\"type\":\"INFIX_OPERATOR\",\"value\":\"+\",\"children\":[{\"type\":\"NUMBER_LITERAL\",\"value\":\"2\"},{\"type\":\"NUMBER_LITERAL\",\"value\":\"3\"}]}]}]}");
	}

	@Test
	void testFunctions() throws ParseException {
		assertASTTreeIsEqualTo(
			"max(1,2,3)-min(3,2,sum(1,2,3))",
			"{\"type\":\"INFIX_OPERATOR\",\"value\":\"-\",\"children\":[{\"type\":\"FUNCTION\",\"value\":\"max\",\"children\":[{\"type\":\"NUMBER_LITERAL\",\"value\":\"1\"},{\"type\":\"NUMBER_LITERAL\",\"value\":\"2\"},{\"type\":\"NUMBER_LITERAL\",\"value\":\"3\"}]},{\"type\":\"FUNCTION\",\"value\":\"min\",\"children\":[{\"type\":\"NUMBER_LITERAL\",\"value\":\"3\"},{\"type\":\"NUMBER_LITERAL\",\"value\":\"2\"},{\"type\":\"FUNCTION\",\"value\":\"sum\",\"children\":[{\"type\":\"NUMBER_LITERAL\",\"value\":\"1\"},{\"type\":\"NUMBER_LITERAL\",\"value\":\"2\"},{\"type\":\"NUMBER_LITERAL\",\"value\":\"3\"}]}]}]}");
	}
}
