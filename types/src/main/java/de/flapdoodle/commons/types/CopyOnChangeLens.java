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
package de.flapdoodle.commons.types;

import java.util.function.BiFunction;
import java.util.function.Function;

public class CopyOnChangeLens<T, M, IM extends M> implements Lens<M, T> {
	private final Function<M, IM> copy;
	private final Function<M, T> read;
	private final BiFunction<IM, T, M> change;
	private CopyOnChangeLens(
		Function<M, IM> copy,
		Function<M, T> read,
		BiFunction<IM, T, M> change
	) {
		this.copy = copy;
		this.read = read;
		this.change = change;
	}
	@Override
	public T read(M model) {
		return read.apply(model);
	}

	@Override
	public M change(M model, T value) {
		return change.apply(copy.apply(model), value);
	}

	public static <T, M, IM extends M> CopyOnChangeLens<T, M, IM> of(
		Function<M, IM> copy,
		Function<M, T> read,
		BiFunction<IM, T, M> change
	) {
		return new CopyOnChangeLens<T, M, IM>(copy, read, change);
	}

	public static <T, M> CopyOnChangeLens<T, M, M> of(
		Function<M, T> readProperty,
		BiFunction<M, T, M> changeProperty
	) {
		return new CopyOnChangeLens<>(it -> it, readProperty, changeProperty);
	}

	public static <T, M> WithGetter<T, M> ofProperty(Function<M, T> read) {
		return new WithGetter<>(read);
	}

	public static final class WithGetter<T, M> {
		private final Function<M, T> read;
		public WithGetter(Function<M, T> read) {
			this.read = read;
		}

		public <IM extends M> CopyOnChangeLens<T, M, IM> changeBy(
			Function<M, IM> copy,
			BiFunction<IM, T, M> change
		) {
			return of(copy, read, change);
		}

		public CopyOnChangeLens<T, M, M> changeBy(
			BiFunction<M, T, M> change
		) {
			return of(read, change);
		}
	}
}
