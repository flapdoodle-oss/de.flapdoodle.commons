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

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class Iterables {
	private Iterables() {
		// no instance
	}

	public static <T> WithItemType<T> of(Class<T> itemType) {
		return new WithItemType<>(TypeInfo.of(itemType));
	}

	public static <T> WithItemType<T> of(TypeInfo<T> itemType) {
		return new WithItemType<>(itemType);
	}

	public static class WithItemType<T> {

		public WithItemType(TypeInfo<T> ignored) {
		}

		public WithMatch<T> match(Predicate<T> test) {
			return new WithMatch<>(test);
		}

		public <V> WithMatch<T> match(View<T, V> lens, Predicate<V> test) {
			return match(it -> test.test(lens.read(it)));
		}

		public WithMap<T> map(Function<T, T> map) {
			return new WithMatch<T>(it -> true).map(map);
		}

		public <C> WithMap<T> map(Lens<T, C> property, Function<C, C> change) {
			return new WithMatch<T>(it -> true).map(property, change);
		}
	}

	public static class WithMatch<T> {

		private final Predicate<T> match;

		public WithMatch(Predicate<T> match) {
			this.match = match;
		}

		public WithMap<T> map(Function<? super T, T> map) {
			return new WithMap<>(match, map);
		}

		public WithMap<T> set(T value) {
			return map(ignore -> value);
		}

		public <C> WithMap<T> map(Lens<T, C> property, Function<? super C, C> change) {
			return map(it -> property.map(it, change));
		}

		public <C> WithMap<T> set(Lens<T, C> property, C value) {
			return map(property, ignore -> value);
		}
	}

	public static class WithMap<T> {

		private final Predicate<T> check;
		private final Function<? super T, T> map;
		public WithMap(Predicate<T> check, Function<? super T, T> map) {
			this.check = check;
			this.map = map;
		}

		public <R> Function<? super Iterable<? extends T>, R> collect(Collector<T, ?, R> collector) {
			return source -> StreamSupport.stream(source.spliterator(), false)
				.map(it -> check.test(it) ? map.apply(it) : it)
				.collect(collector);
		}

		public Function<? super Iterable<? extends T>, List<T>> toList() {
			return collect(Collectors.toList());
		}
	}
}
