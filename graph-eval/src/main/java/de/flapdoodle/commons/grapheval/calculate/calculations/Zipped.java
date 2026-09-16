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
package de.flapdoodle.commons.grapheval.calculate.calculations;

import com.google.common.collect.ImmutableSet;
import de.flapdoodle.commons.grapheval.ValueSink;
import de.flapdoodle.commons.grapheval.ValueSource;
import de.flapdoodle.commons.grapheval.calculate.Calculation;
import de.flapdoodle.commons.grapheval.calculate.ValueLookup;
import de.flapdoodle.commons.grapheval.calculate.functions.FN1;
import de.flapdoodle.commons.grapheval.types.HasHumanReadableLabel;
import de.flapdoodle.commons.grapheval.types.Tuple;
import org.immutables.value.Value;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Value.Immutable(builder = false)
public abstract class Zipped<A, B, X> implements Calculation<X>, HasHumanReadableLabel {
	@Value.Parameter
	protected abstract List<Tuple<? extends ValueSource<A>, ? extends ValueSource<B>>> sourceList();

	@Value.Parameter
	protected abstract FN1<List<Tuple<A, B>>, X> zipper();

	@Override
	public Set<ValueSource<?>> sources() {
		return ImmutableSet.copyOf(sourceList().stream()
			.flatMap(pair -> Stream.of(pair.first(), pair.second()))
			.collect(Collectors.toList()));
	}

	@Override
	public X calculate(ValueLookup values) {
		List<Tuple<A, B>> sourceValues = sourceList().stream()
			.map(pair -> pair.mapFirst(values::get).mapSecond(values::get))
			.collect(Collectors.toList());
		return zipper().apply(sourceValues);
	}

	@Override
	public String asHumanReadable() {
		return HasHumanReadableLabel.asHumanReadable(zipper());
	}

	public static <A, B, X> Zipped<A, B, X> with(
		List<? extends Tuple<? extends ValueSource<A>, ? extends ValueSource<B>>> sourceList,
		ValueSink<X> destination,
		FN1<List<Tuple<A, B>>, X> aggregation
	) {
		return ImmutableZipped.of(destination, sourceList, aggregation);
	}
}
