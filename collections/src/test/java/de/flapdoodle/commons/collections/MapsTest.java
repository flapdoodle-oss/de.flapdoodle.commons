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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

class MapsTest {
	@Test
	void changeItemInMap() {
		Map<String, Child> sourceMap=new LinkedHashMap<>();
		sourceMap.put("A", Child.builder().name("A").age(10).build());
		sourceMap.put("B", Child.builder().name("B").age(20).build());

		Lens<Child, String> name = Lens.ofProperty(Child::name)
			.changeBy(ImmutableChild::copyOf, ImmutableChild::withName);
		Lens<Child, Integer> age = Lens.ofProperty(Child::age)
			.changeBy(ImmutableChild::copyOf, ImmutableChild::withAge);

		Function<? super Map<? extends String,? extends Child>, Map<String, Child>> testee = Maps.of(String.class, Child.class)
			.matchValue(age, it -> it >18)
			.mapValue(name, it -> it+"(old)")
			.toMap();

		Map<String, Child> changed = testee.apply(sourceMap);

		Assertions.assertThat(changed)
			.hasSize(2)
			.containsEntry("A", Child.builder().name("A").age(10).build())
			.containsEntry("B", Child.builder().name("B(old)").age(20).build());

	}

}