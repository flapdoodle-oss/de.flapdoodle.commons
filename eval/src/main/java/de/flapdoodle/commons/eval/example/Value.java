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
package de.flapdoodle.commons.eval.example;

import de.flapdoodle.commons.eval.core.Nullable;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Value<T> {
	@Deprecated
	public static final BooleanValue TRUE = Value.of(Boolean.TRUE);
	@Deprecated
	public static final BooleanValue FALSE = Value.of(Boolean.FALSE);

	@org.immutables.value.Value.Parameter
	public abstract T wrapped();

	@Override
	@org.immutables.value.Value.Auxiliary
	public String toString() {
		return wrapped().toString();
	}

	@org.immutables.value.Value.Immutable
	public static abstract class NullValue extends Value<Void> {

		@Override
		@Nullable
		public abstract Void wrapped();

		@Override
		public String toString() {
			return getClass().getSimpleName();
		}
	}

	@org.immutables.value.Value.Immutable
	public static abstract class StringValue extends Value<String> implements Comparable<StringValue> {
		@org.immutables.value.Value.Auxiliary
		@Override
		public int compareTo(StringValue other) {
			return wrapped().compareTo(other.wrapped());
		}
	}

	@org.immutables.value.Value.Immutable
	public static abstract class NumberValue extends Value<BigDecimal> implements Comparable<NumberValue> {
		@org.immutables.value.Value.Auxiliary
		@Override
		public int compareTo(NumberValue other) {
			return wrapped().compareTo(other.wrapped());
		}
	}

	@org.immutables.value.Value.Immutable
	public static abstract class BooleanValue extends Value<Boolean> implements Comparable<BooleanValue> {
		@org.immutables.value.Value.Auxiliary
		@Override
		public int compareTo(BooleanValue other) {
			return wrapped().compareTo(other.wrapped());
		}
	}

	@org.immutables.value.Value.Immutable
	public static abstract class DateTimeValue extends Value<Instant> implements Comparable<DateTimeValue> {
		@org.immutables.value.Value.Auxiliary
		@Override
		public int compareTo(DateTimeValue other) {
			return wrapped().compareTo(other.wrapped());
		}
	}

	@org.immutables.value.Value.Immutable
	public static abstract class DurationValue extends Value<Duration>  implements Comparable<DurationValue> {
		@org.immutables.value.Value.Auxiliary
		@Override
		public int compareTo(DurationValue other) {
			return wrapped().compareTo(other.wrapped());
		}
	}

	@org.immutables.value.Value.Immutable
	public static abstract class ArrayValue extends Value<Values> {
	}

	@org.immutables.value.Value.Immutable
	public static abstract class MapValue extends Value<ValueMap> {
	}

	@org.immutables.value.Value.Immutable
	public static abstract class FailedWithException<T> extends Value<T> {
		public abstract EvaluationException exception();
		@Nullable
		@Override
		@org.immutables.value.Value.Lazy
		public T wrapped() {
			throw new EvaluationException.AsRuntimeException(exception());
		}

		@Override
		@org.immutables.value.Value.Auxiliary
		public String toString() {
			return "FailedWithException(exception="+exception()+")";
		}
	}

	public static StringValue of(String value) {
		return ImmutableStringValue.of(value);
	}

	public static NumberValue of(BigDecimal value) {
		return ImmutableNumberValue.of(value);
	}

	public static NumberValue of(double value) {
		// new BigDecimal(Double.toString(value), mathContext); ???
		if (Double.isNaN(value)) throw new IllegalArgumentException("is "+value);
		if (Double.isInfinite(value)) throw new IllegalArgumentException("is "+value);
		return Value.of(BigDecimal.valueOf(value));
	}

	private static NullValue NULL = ImmutableNullValue.builder().build();

	public static NullValue ofNull() {
		return NULL;
	}

	public static BooleanValue of(Boolean value) {
		return ImmutableBooleanValue.of(value);
	}

	public static DateTimeValue of(Instant value) {
		return ImmutableDateTimeValue.of(value);
	}

	public static DurationValue of(Duration value) {
		return ImmutableDurationValue.of(value);
	}

	public static ArrayValue of(Values value) {
		return ImmutableArrayValue.of(value);
	}

	public static MapValue of(ValueMap value) {
		return ImmutableMapValue.of(value);
	}

	public static <T> MapValue of(Function<T, Value<?>> mapper, Map<String, T> map) {
		return of(ValueMap.of(map.entrySet().stream()
			.collect(Collectors.toMap(Map.Entry::getKey, entry -> mapper.apply(entry.getValue())))));
	}

	public static ArrayValue of(Collection<? extends Value<?>> array) {
		return ImmutableArrayValue.of(Values.of(array));
	}

	public static <T> ArrayValue of(Function<T, Value<?>> mapper, Collection<? extends T> array) {
		return of(array.stream().map(mapper).collect(Collectors.toList()));
	}

	public static <T> ArrayValue of(Function<T, Value<?>> mapper, T... array) {
		return of(Stream.of(array).map(mapper).collect(Collectors.toList()));
	}

	public static ArrayValue of(Value<?>... array) {
		return of(Values.of(Arrays.asList(array)));
	}

	public static Value<?> failedWith(EvaluationException rx) {
		return ImmutableFailedWithException.builder()
			.exception(rx)
			.build();
	}
}
