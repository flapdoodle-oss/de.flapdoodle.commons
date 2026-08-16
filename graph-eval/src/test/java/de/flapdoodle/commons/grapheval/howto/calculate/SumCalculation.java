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
package de.flapdoodle.commons.grapheval.howto.calculate;

import com.google.common.collect.ImmutableSet;
import de.flapdoodle.commons.grapheval.Value;
import de.flapdoodle.commons.grapheval.ValueSource;
import de.flapdoodle.commons.grapheval.calculate.Calculation;
import de.flapdoodle.commons.grapheval.calculate.ValueLookup;
import org.immutables.value.Value.Immutable;

import java.util.Set;

@Immutable
public abstract class SumCalculation implements Calculation<Integer> {
	public abstract ValueSource<Integer> a();
	public abstract ValueSource<Integer> b();

	@Override
	public abstract Value<Integer> destination();

	@Override
	@org.immutables.value.Value.Lazy
	public Set<? extends ValueSource<?>> sources() {
		return ImmutableSet.of(a(), b());
	}

	@Override
	public Integer calculate(ValueLookup values) {
		Integer a = values.get(a());
		Integer b = values.get(b());
		return (a != null && b != null)
			? a + b
			: null;
	}

	public static ImmutableSumCalculation.Builder builder() {
		return ImmutableSumCalculation.builder();
	}
}
