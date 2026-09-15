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
package de.flapdoodle.commons.collections;

import de.flapdoodle.commons.types.Lens;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class IterablesTest {
	@Test
	void changeItemInList() {
		Lens<Child, String> name = Lens.ofProperty(Child::name)
			.changeBy(ImmutableChild::copyOf, ImmutableChild::withName);
		Lens<Child, Integer> age = Lens.ofProperty(Child::age)
			.changeBy(ImmutableChild::copyOf, ImmutableChild::withAge);

		Function<? super Iterable<? extends Child>, List<Child>> changeItem = Iterables.of(Child.class)
			.filter(age, it -> it>18)
			.map(name, n -> n+"(old)")
			.toList();

		List<? extends Child> source = Arrays.asList(
			Child.builder().name("A").age(10).build(),
			Child.builder().name("B").age(20).build()
		);

		List<Child> changed = changeItem.apply(source);

		assertThat(changed)
			.hasSize(2)
			.containsExactly(
				Child.builder().name("A").age(10).build(),
				Child.builder().name("B(old)").age(20).build()
			);
	}

}