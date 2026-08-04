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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ListTypeInfoTest {

	@Test
	void typeInfoMustCompareNestedTypes() {
		TypeInfo<List<String>> listOfStrings = TypeInfo.listOf(TypeInfo.of(String.class));

		assertThat(listOfStrings.isInstance(Arrays.asList("foo", "bar")))
			.isTrue();

		assertThat(listOfStrings.isInstance(Arrays.asList("foo", 2)))
			.isFalse();
	}

	@Test
	void superAndExtends() {
		List<Double> testee=new ArrayList<>();

		List<? extends Number> producer=testee;
		List<? super Double> consumer=testee;

		consumer.add(12.3);
		Number element = producer.get(0);

		assertThat(element.getClass()).isEqualTo(Double.class);
		assertThat(element).isEqualTo(12.3);

		TypeInfo<List<Number>> typeInfo = TypeInfo.listOf(TypeInfo.of(Number.class));
		assertThat(typeInfo.isInstance(producer)).isTrue();
		assertThat(typeInfo.isInstance(consumer)).isTrue();

		List<Number> castedList = typeInfo.cast(consumer);
		Number castedElement = castedList.get(0);
		assertThat(castedElement.getClass()).isEqualTo(Double.class);
		assertThat(castedElement).isEqualTo(12.3);

		// as we don't know which other contracts the original instance hat
		// things can go wrong, if we mutate it.
		castedList.add(12);

		assertThatThrownBy(() -> testee.get(1))
				.isInstanceOf(ClassCastException.class)
				.hasMessageContaining("java.lang.Integer cannot be cast to java.lang.Double");
	}

}