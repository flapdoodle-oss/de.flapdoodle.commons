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
package de.flapdoodle.commons.grapheval.values.domain;

import de.flapdoodle.commons.grapheval.Value;
import de.flapdoodle.commons.grapheval.calculate.Calculate;
import de.flapdoodle.commons.grapheval.rules.Rules;
import de.flapdoodle.commons.grapheval.values.Named;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HasRulesTest {

	@Test
	void delegatesMustBeCalled() {
		Named<Integer> a = Value.named("A", Integer.class);
		Named<Integer> b = Value.named("B", Integer.class);
		Named<Integer> c = Value.named("C", Integer.class);

		HasRules first =rules -> rules.add(Calculate.value(a)
			.using(b)
			.ifAllSetBy(it -> it + 2));

		HasRules second =rules -> rules.add(Calculate.value(b)
			.using(c)
			.ifAllSetBy(it -> it * 3));

		Rules result = HasRules.addAll(Rules.empty(), first, second);

		assertThat(result.calculations().keys())
			.hasSize(2)
			.containsExactly(a, b);
	}
}