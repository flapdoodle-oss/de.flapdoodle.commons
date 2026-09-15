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
package de.flapdoodle.commons.collections;

import de.flapdoodle.commons.reflection.TypeInfo;
import de.flapdoodle.commons.types.Lens;
import de.flapdoodle.commons.types.View;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class Maps {
	private Maps() {
		// no instance
	}

	public static <K, V> WithMapType<K, V> of(TypeInfo<Map<K,V>> mapType) {
		return new WithMapType<K, V>(mapType);
	}

	public static <K, V> WithMapType<K, V> of(Class<K> keyType, Class<V> valueType) {
		return of(TypeInfo.mapOf(TypeInfo.of(keyType), TypeInfo.of(valueType)));
	}

	public static class WithMapType<K, V> {

		private final TypeInfo<Map<K, V>> mapType;
		public WithMapType(TypeInfo<Map<K, V>> mapType) {
			this.mapType = mapType;
		}

		public WithFilter<K, V> filterKey(Predicate<K> filter) {
			return new WithFilter<K, V>(filter, it -> true);
		}

		public <T> WithFilter<K, V>filterKey(View<K, T> lens, Predicate<T> test) {
			return filterKey(it -> test.test(lens.read(it)));
		}

		public WithFilter<K, V> filterValue(Predicate<V> filter) {
			return new WithFilter<K, V>(it -> true, filter);
		}

		public <T> WithFilter<K, V>filterValue(View<V, T> lens, Predicate<T> test) {
			return filterValue(it -> test.test(lens.read(it)));
		}
	}

	public static class WithFilter<K, V> {

		private final Predicate<K> keyFilter;
		private final Predicate<V> valueFilter;
		public WithFilter(Predicate<K> keyFilter, Predicate<V> valueFilter) {
			this.keyFilter = keyFilter;
			this.valueFilter = valueFilter;
		}

		public WithMap<K, V> mapKey(Function<? super K, K> map) {
			return new WithMap<K, V>(keyFilter, valueFilter, map, Function.identity());
		}

		public <C> WithMap<K, V> mapKey(Lens<K, C> lens, Function<C, C> change) {
			return mapKey(it -> lens.map(it, change));
		}

		public WithMap<K, V> mapValue(Function<? super V, V> map) {
			return new WithMap<K, V>(keyFilter, valueFilter, Function.identity(), map);
		}

		public <C> WithMap<K, V> mapValue(Lens<V, C> lens, Function<C, C> change) {
			return mapValue(it -> lens.map(it, change));
		}
	}

	public static class WithMap<K, V> {

		private final Predicate<K> keyFilter;
		private final Predicate<V> valueFilter;
		private final Function<? super K, K> mapKey;
		private final Function<? super V, V> mapValue;
		public WithMap(
			Predicate<K> keyFilter,
			Predicate<V> valueFilter,
			Function<? super K, K> mapKey,
			Function<? super V, V> mapValue
		) {
			this.keyFilter = keyFilter;
			this.valueFilter = valueFilter;
			this.mapKey = mapKey;
			this.mapValue = mapValue;
		}

		public Function<? super Map<? extends K, ? extends V>, Map<K, V>> toMap() {
			return source -> source.entrySet().stream()
				.collect(Collectors.toMap(
					entry -> keyFilter.test(entry.getKey()) && valueFilter.test(entry.getValue())  ? mapKey.apply(entry.getKey()) : entry.getKey(),
					entry -> keyFilter.test(entry.getKey()) && valueFilter.test(entry.getValue()) ? mapValue.apply(entry.getValue()) : entry.getValue()));
		}
	}
}
