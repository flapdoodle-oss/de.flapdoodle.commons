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
package de.flapdoodle.commons.eval.core.evaluables;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class SignatureTest {
	@Test
	public void checkSignature() {
		Signature<BigDecimal> testee = Signature.of(BigDecimal.class, Parameter.nullableWith(Number.class));

		assertThat(testee.validateArguments(
			Arrays.asList(Evaluated.value(1.9)))
		).isEmpty();

		assertThat(testee.validateArguments(
			Arrays.asList(Evaluated.ofNull(Double.class)))
		).isEmpty();

		assertThat(testee.validateArguments(
			Arrays.asList(Evaluated.value(BigDecimal.ONE)))
		).isEmpty();

		assertThat(testee.validateArguments(
			Arrays.asList(Evaluated.ofNull(BigDecimal.class)))
		).isEmpty();

		assertThat(testee.validateArguments(
			Arrays.asList(Evaluated.value(""))).get()
		).hasMessageContaining("wrong type: ClassTypeInfo{type=class java.lang.Number} != ClassTypeInfo{type=class java.lang.String} ()");

		assertThat(testee.validateArguments(
			Arrays.asList(Evaluated.ofNull(String.class))).get()
		).hasMessageContaining("wrong nullable type: ClassTypeInfo{type=class java.lang.Number} != ClassTypeInfo{type=class java.lang.String}");
	}

	@Test
	public void checkSignatureTypes() {
		Signature<BigDecimal> testee = Signature.of(BigDecimal.class, Parameter.nullableWith(Number.class));

		assertThat(testee.validateArgumentTypes(
			Arrays.asList(Evaluated.value(1.9).type()))
		).isEmpty();

		assertThat(testee.validateArgumentTypes(
			Arrays.asList(Evaluated.ofNull(Double.class).type()))
		).isEmpty();

		assertThat(testee.validateArgumentTypes(
			Arrays.asList(Evaluated.value(BigDecimal.ONE).type()))
		).isEmpty();

		assertThat(testee.validateArgumentTypes(
			Arrays.asList(Evaluated.ofNull(BigDecimal.class).type()))
		).isEmpty();

		assertThat(testee.validateArgumentTypes(
			Arrays.asList(Evaluated.value("").type())).get()
		).hasMessageContaining("wrong type: ClassTypeInfo{type=class java.lang.Number} != ClassTypeInfo{type=class java.lang.String}");

		assertThat(testee.validateArgumentTypes(
			Arrays.asList(Evaluated.ofNull(String.class).type())).get()
		).hasMessageContaining("wrong type: ClassTypeInfo{type=class java.lang.Number} != ClassTypeInfo{type=class java.lang.String}");
	}
}