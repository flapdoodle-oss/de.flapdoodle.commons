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

		private final TypeInfo<T> itemType;
		public WithItemType(TypeInfo<T> itemType) {
			this.itemType = itemType;
		}

		public WithFilter<T> filter(Predicate<T> test) {
			return new WithFilter<>(test);
		}

		public <V> WithFilter<T> filter(View<T, V> lens, Predicate<V> test) {
			return filter(it -> test.test(lens.read(it)));
		}

		public WithMap<T> map(Function<T, T> map) {
			return new WithFilter<T>(it -> true).map(map);
		}

		public <C> WithMap<T> map(Lens<T, C> property, Function<C, C> change) {
			return new WithFilter<T>(it -> true).map(property, change);
		}
	}

	public static class WithFilter<T> {

		private final Predicate<T> check;

		public WithFilter(Predicate<T> check) {
			this.check = check;
		}

		public WithMap<T> map(Function<? super T, T> map) {
			return new WithMap<>(check, map);
		}

		public <C> WithMap<T> map(Lens<T, C> property, Function<? super C, C> change) {
			return map(it -> property.map(it, change));
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
