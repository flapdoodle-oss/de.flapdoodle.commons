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
package de.flapdoodle.commons.eval.example;

import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.exceptions.ParseException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExpressionEvaluatorStringTest extends BaseExpressionEvaluatorTest {

	@Test
	void testStringConcatenation() throws ParseException, EvaluationException {
		assertThat(evaluate("\"Hello\"+\" world\"")).isEqualTo("Hello world");
	}

	@Test
	void testStringAndNumberConcatenation() throws ParseException, EvaluationException {
		assertThat(evaluate("\"Test\"+1")).isEqualTo("Test1");
	}

	@Test
	void testStringAndDoubleNumberConcatenation() throws ParseException, EvaluationException {
		assertThat(evaluate("\"Test\"+1+2")).isEqualTo("Test12");
	}

	@Test
	void testNumberAndStringConcatenation() throws ParseException, EvaluationException {
		assertThat(evaluate("1+\"Test\"")).isEqualTo("1Test");
	}

	@Test
	void testDoubleNumberAndStringConcatenation() throws ParseException, EvaluationException {
		assertThat(evaluate("1+2+\"Test\"")).isEqualTo("3Test");
	}

	@Test
	void testInnerNumberConcatenation() throws ParseException, EvaluationException {
		assertThat(evaluate("\"Start\"+1+1+\"End\"")).isEqualTo("Start11End");
	}

	@Test
	void testInnerTripleNumberConcatenation() throws ParseException, EvaluationException {
		assertThat(evaluate("\"Start\"+1+2+1+\"End\"")).isEqualTo("Start121End");
	}

	@Test
	void testInnerNumberConcatenationWithBraces() throws ParseException, EvaluationException {
		assertThat(evaluate("\"Start\"+(1+1)+\"End\"")).isEqualTo("Start2End");
	}
}
