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
package de.flapdoodle.commons.types;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LensTest {

	@Test
	void changeSample() {
		ImmutableSample sample = Sample.builder()
			.name("Name")
			.child(Sample.Child.builder()
				.name("Child")
				.age(12)
				.build())
			.build();

		Lens<Sample, String> name = Lens.ofProperty(Sample::name)
			.changeBy(ImmutableSample::copyOf, ImmutableSample::withName);
		Lens<Sample, Sample.Child> child = Lens.ofProperty(Sample::child)
			.changeBy(ImmutableSample::copyOf, ImmutableSample::withChild);

		Lens<Sample.Child, Integer> age = Lens.ofProperty(Sample.Child::age)
			.changeBy(ImmutableChild::copyOf, ImmutableChild::withAge);

		assertThat(sample.name()).isEqualTo("Name");
		assertThat(sample.child().name()).isEqualTo("Child");
		assertThat(sample.child().age()).isEqualTo(12);

		Sample changed = child.and(age).change(name.change(sample, "new Name"), 18);

		assertThat(changed.name()).isEqualTo("new Name");
		assertThat(changed.child().name()).isEqualTo("Child");
		assertThat(changed.child().age()).isEqualTo(18);
	}
}