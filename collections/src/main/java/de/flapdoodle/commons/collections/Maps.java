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

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

		public WithMapType(TypeInfo<Map<K, V>> ignored) {
		}

		public WithMatch<K, V> matchKey(Predicate<K> filter) {
			return new WithMatch<K, V>(filter, it -> true);
		}

		public <T> WithMatch<K, V> matchKey(View<K, T> lens, Predicate<T> test) {
			return matchKey(it -> test.test(lens.read(it)));
		}

		public WithMatch<K, V> matchValue(Predicate<V> filter) {
			return new WithMatch<K, V>(it -> true, filter);
		}

		public <T> WithMatch<K, V> matchValue(View<V, T> lens, Predicate<T> test) {
			return matchValue(it -> test.test(lens.read(it)));
		}
	}

	public static class WithMatch<K, V> {

		private final Predicate<K> keyMatch;
		private final Predicate<V> valueMatch;
		public WithMatch(Predicate<K> keyMatch, Predicate<V> valueMatch) {
			this.keyMatch = keyMatch;
			this.valueMatch = valueMatch;
		}

		public WithMap<K, V> mapKey(Function<? super K, K> map) {
			return new WithMap<K, V>(keyMatch, valueMatch, map, Function.identity());
		}

		public <C> WithMap<K, V> mapKey(Lens<K, C> lens, Function<C, C> change) {
			return mapKey(it -> lens.map(it, change));
		}

		public WithMap<K, V> mapValue(Function<? super V, V> map) {
			return new WithMap<K, V>(keyMatch, valueMatch, Function.identity(), map);
		}

		public <C> WithMap<K, V> mapValue(Lens<V, C> lens, Function<C, C> change) {
			return mapValue(it -> lens.map(it, change));
		}
	}

	public static class WithMap<K, V> {

		private final Predicate<K> keyMatch;
		private final Predicate<V> valueMatch;
		private final Function<? super K, K> mapKey;
		private final Function<? super V, V> mapValue;
		public WithMap(
			Predicate<K> keyMatch,
			Predicate<V> valueMatch,
			Function<? super K, K> mapKey,
			Function<? super V, V> mapValue
		) {
			this.keyMatch = keyMatch;
			this.valueMatch = valueMatch;
			this.mapKey = mapKey;
			this.mapValue = mapValue;
		}

		public Function<? super Map<? extends K, ? extends V>, Map<K, V>> toMap() {
			return source -> source.entrySet().stream()
				.collect(Collectors.toMap(
					entry -> keyMatch.test(entry.getKey()) && valueMatch.test(entry.getValue())  ? mapKey.apply(entry.getKey()) : entry.getKey(),
					entry -> keyMatch.test(entry.getKey()) && valueMatch.test(entry.getValue()) ? mapValue.apply(entry.getValue()) : entry.getValue()));
		}
	}
}
