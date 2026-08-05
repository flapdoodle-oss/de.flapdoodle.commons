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
package de.flapdoodle.commons.eval.example.evaluables;

import de.flapdoodle.commons.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.commons.eval.core.evaluables.TypedEvaluableMap;
import de.flapdoodle.commons.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.commons.eval.example.Value;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TypedEvaluableMapTest {

	@Test
	void findMatchingEntry() {
		TypedEvaluableMap testee = TypedEvaluableMap.builder()
			.putMap("foo", TypedEvaluables.builder()
				.addList(TypedEvaluable.of(Value.NumberValue.class, Value.NumberValue.class,
					(valueResolver, evaluationContext, token, argument) -> Value.of(argument.wrapped().add(BigDecimal.ONE))))
				.build())
			.build();

		assertThat(testee.find("foo", 1)).isPresent();
		assertThat(testee.find("foo", 2)).isEmpty();
		assertThat(testee.find("fo", 1)).isEmpty();
	}
}