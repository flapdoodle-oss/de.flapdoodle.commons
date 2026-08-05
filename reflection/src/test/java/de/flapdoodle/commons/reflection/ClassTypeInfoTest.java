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
package de.flapdoodle.commons.reflection;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClassTypeInfoTest {

	@Test
	void typeMustMatchInstance() {
		TypeInfo<Number> testee = TypeInfo.of(Number.class);

		assertThat(testee.isInstance(13)).isTrue();
		assertThat(testee.isInstance(1.0)).isTrue();
		assertThat(testee.isInstance("test")).isFalse();

		assertThat(testee.isAssignable(TypeInfo.of(Double.class))).isTrue();

		Object value = 13f;

		Number valueAsNumber = testee.cast(value);
		assertThat(valueAsNumber).isInstanceOf(Float.class);
	}

	@Test
	void howAboutNull() {
		TypeInfo<Number> testee = TypeInfo.of(Number.class);

		assertThat(Number.class.isInstance(null)).isFalse();
		assertThat(testee.isInstance(null)).isFalse();

		assertThatThrownBy(() -> testee.cast(null))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("type mismatch: null is not a");
	}
}