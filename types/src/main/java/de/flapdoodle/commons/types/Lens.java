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

import java.util.function.Function;

public interface Lens<M, T> extends View<M, T> {
	M change(M model, T value);

	default M map(M model, Function<T, T> map) {
		return change(model, map.apply(read(model)));
	}

	default <U> Lens<M, U> and(Lens<T, U> next) {
		Lens<M, T> that=this;

		return new Lens<M, U>() {
			@Override
			public U read(M model) {
				return next.read(that.read(model));
			}
			@Override
			public M change(M model, U value) {
				return that.change(model, next.change(that.read(model), value));
			}
		};
	}

	default Function<M, M> change(T value) {
		return model -> change(model, value);
	}

	default Function<M, M> map(Function<T, T> map) {
		return model -> map(model, map);
	}

	static <T, M> CopyOnChangeLens.WithGetter<T, M> ofProperty(Function<M, T> read) {
		return CopyOnChangeLens.ofProperty(read);
	}
}
