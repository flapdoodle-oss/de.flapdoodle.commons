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
import javax.annotation.Nullable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FN6Test {

	@Test
	void withLabel() {
		FN6<String, String, String, String, String, Double, Integer> testee = FN6.withLabel(new NullableStringsToSum(), "label");
		assertThat(testee.apply("2", "3","4", "5", "6", 7.0)).isEqualTo(27);
		assertThat(testee.apply(null, "x","y", "z", "!", 1232.0)).isNull();
		assertThat(HasHumanReadableLabel.asHumanReadable(testee)).isEqualTo("label");
	}

	@Test
	void mapOnlyIfNotNull() {
		FN6<String, String, String, String, String, Double, Integer> testee = FN6.mapOnlyIfNotNull(new StringsToSum());
		assertThat(testee.apply("2","3","4", "5", "6", 7.0)).isEqualTo(27);
		assertThat(testee.apply(null, "x", "y", "z", "!", 121.0)).isNull();
		assertThat(HasHumanReadableLabel.asHumanReadable(testee)).isEqualTo("StringsToSum");
	}

	@Test
	void checkNull() {
		FN6<String, String, String, String, String, Double, Integer> testee = FN6.checkNull(new StringsToSum(),"a", "b", "c", "d", "e", "f");
		assertThat(testee.apply("2","3", "4", "5","6", 7.0)).isEqualTo(27);
		assertThatThrownBy(() -> testee.apply(null,"x", "y", "z", "!", 123.0))
			.isInstanceOf(NullPointerException.class)
			.hasMessage("StringsToSum: a is null");
		assertThatThrownBy(() -> testee.apply("x",null, "y", "z", "!", 123.0))
			.isInstanceOf(NullPointerException.class)
			.hasMessage("StringsToSum: b is null");
		assertThatThrownBy(() -> testee.apply("x","y", null, "z", "!", 123.0))
			.isInstanceOf(NullPointerException.class)
			.hasMessage("StringsToSum: c is null");
		assertThatThrownBy(() -> testee.apply("x","y", "z", null, "!", 123.0))
			.isInstanceOf(NullPointerException.class)
			.hasMessage("StringsToSum: d is null");
		assertThatThrownBy(() -> testee.apply("x","y", "z", "!", "!", null))
			.isInstanceOf(NullPointerException.class)
			.hasMessage("StringsToSum: f is null");
	}

	static class StringsToSum implements F6<String, String, String, String, String, Double, Integer> {
		@Nonnull @Override public Integer apply(@Nonnull String a, @Nonnull String b, @Nonnull String c, @Nonnull String d, @Nonnull String e, @Nonnull Double f) {
			return Integer.parseInt(a) + Integer.parseInt(b) + Integer.parseInt(c) + Integer.parseInt(d) + Integer.parseInt(e) + f.intValue();
		}
		@Override public String toString() {
			return StringsToSum.class.getSimpleName();
		}
	}

	static class NullableStringsToSum implements FN6<String, String, String, String, String, Double, Integer> {
		@Nullable @Override public Integer apply(@Nullable String a, @Nullable String b, @Nullable String c, @Nullable String d, @Nullable String e, @Nullable Double f) {
			return (a!=null && b!=null && c!=null && d!=null && e!=null && f!=null) ? Integer.parseInt(a) + Integer.parseInt(b) + Integer.parseInt(c) + Integer.parseInt(d) + Integer.parseInt(e) + f.intValue() : null;
		}
		@Override public String toString() {
			return NullableStringsToSum.class.getSimpleName();
		}
	}
}