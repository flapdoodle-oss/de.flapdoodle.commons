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
package de.flapdoodle.commons.grapheval.calculate.functions;

import de.flapdoodle.commons.grapheval.types.HasHumanReadableLabel;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;

class F6Test {

	@Test
	void withLabel() {
		F6<String, String, String, String, String, Double, Integer> testee = F6.withLabel(new StringsToSum(), "label");
		assertThat(testee.apply("2", "3", "4", "5", "6", 7.0)).isEqualTo(27);
		assertThat(HasHumanReadableLabel.asHumanReadable(testee)).isEqualTo("label");
	}

	static class StringsToSum implements F6<String, String, String, String, String, Double, Integer> {

		@Nonnull @Override public Integer apply(@Nonnull String a, @Nonnull String b, @Nonnull String c, @Nonnull String d, @Nonnull String e, @Nonnull Double f) {
			return Integer.parseInt(a) + Integer.parseInt(b) + Integer.parseInt(c) + Integer.parseInt(d) + Integer.parseInt(e) + f.intValue();
		}

		@Override public String toString() {
			return StringsToSum.class.getSimpleName();
		}
	}

}