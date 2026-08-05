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
package de.flapdoodle.commons.eval.example.evaluables.booleans;

import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.exceptions.ParseException;
import de.flapdoodle.commons.eval.example.BaseEvaluationTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class GreaterTest extends BaseEvaluationTest {

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"1>1 : false",
			"2>1 : true",
			"2.1>2.0 : true",
			"2.0>2.0 : false",
			"21.679>21.678 : true",
			"\"abd\">\"abc\" : true",
			"\"abc\">\"xyz\" : false",
			"\"ABC\">\"abc\" : false",
			"\"9\">\"5\" : true",
			"-4>-5 :true",
			"localDateTime(2022,10,30)>localDateTime(2022,10,30) : false",
			"localDateTime(2022,10,30)>localDateTime(2022,10,28) : true",
			"localDateTime(2022,10,30)>localDateTime(2022,10,31) : false",
			"parseDuration(\"P2D\")>parseDuration(\"PT24H\") : true"
		})
	void testInfixGreaterLiterals(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asBoolean(expectedResult));
	}
}
