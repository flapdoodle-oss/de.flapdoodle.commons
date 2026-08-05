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

import de.flapdoodle.commons.eval.core.Nullable;
import de.flapdoodle.commons.reflection.TypeInfo;
import org.immutables.value.Value;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Value.Immutable
public abstract class Evaluated<T> {
	@Value.Parameter
	public abstract TypeInfo<T> type();
	@Nullable
	@Value.Parameter
	public abstract T wrapped();

	@Value.Derived
	public boolean isNull() {
		return wrapped() == null;
	}

	public static <T> Evaluated<T> value(T value) {
		return ofNullable(TypeInfo.of((Class<T>) value.getClass()), value);
	}

	public static <T> Evaluated<T> ofNullable(Class<T> type, @Nullable T value) {
		return ofNullable(TypeInfo.of(type), value);
	}

	public static <T> Evaluated<T> ofNull(Class<T> type) {
		return ofNullable(TypeInfo.of(type), null);
	}

	public static <T> Evaluated<T> ofNull(TypeInfo<T> type) {
		return ofNullable(type, null);
	}

	public static <T> Evaluated<T> ofNullable(TypeInfo<T> type, @Nullable T value) {
		return ImmutableEvaluated.of(type, value);
	}

	public static List<?> unwrap(List<? extends Evaluated<?>> src) {
		return src.stream().map(it -> it.wrapped()).collect(Collectors.toList());
	}

	public static <T> List<? extends Evaluated<T>> wrap(List<T> src) {
		return src.stream().map(it -> Evaluated.value(it)).collect(Collectors.toList());
	}

	public static <T> List<? extends Evaluated<T>> asList(T ... src) {
		return wrap(Arrays.asList(src));
	}
}
