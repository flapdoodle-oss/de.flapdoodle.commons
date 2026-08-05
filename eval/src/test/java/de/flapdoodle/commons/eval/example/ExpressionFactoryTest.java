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

import de.flapdoodle.commons.eval.core.ExpressionFactory;
import de.flapdoodle.commons.eval.core.MapBasedVariableResolver;
import de.flapdoodle.commons.eval.core.VariableResolver;
import de.flapdoodle.commons.eval.core.evaluables.Evaluated;
import org.junit.jupiter.api.Test;

import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ExpressionFactoryTest {

	@Test
	void testDefaultSetup() {
		ExpressionFactory factory = Defaults.expressionFactory();

		assertThat(factory.operatorMap())
			.isSameAs(Defaults.operatorMap());
		assertThat(factory.evaluatables())
			.isSameAs(Defaults.evaluatables());
		assertThat(factory.constants())
			.isSameAs(Defaults.constants());
	}

	@Test
	void testCustomMathContext() {
		ExpressionFactory factory =
			Defaults.expressionFactory().withMathContext(MathContext.DECIMAL32);

		assertThat(factory.mathContext()).isEqualTo(MathContext.DECIMAL32);
	}

	@Test
	void testCustomConstants() {
		Map<String, Evaluated<?>> constants =
			new HashMap<String, Evaluated<?>>() {
				{
					put("A", Evaluated.value(Value.of("a")));
					put("B", Evaluated.value(Value.of("b")));
				}
			};
		MapBasedVariableResolver valueResolver = VariableResolver.empty()
			.withValues(constants);
		ExpressionFactory factory =
			Defaults.expressionFactory().withConstants(valueResolver);

		assertThat(factory.constants().get("A").wrapped()).isEqualTo(Value.of("a"));
	}
}
