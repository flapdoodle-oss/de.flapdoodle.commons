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
package de.flapdoodle.commons.grapheval.calculate.functions;

import de.flapdoodle.commons.grapheval.types.HasHumanReadableLabel;
import org.immutables.value.Value;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface F6<A, B, C, D, E, F, R> {
	@Nonnull R apply(@Nonnull A a, @Nonnull B b, @Nonnull C c, @Nonnull D d, @Nonnull E e, @Nonnull F f);

	@Value.Immutable
	abstract class F6WithLabel<A, B, C, D, E, F, R> implements F6<A, B, C, D, E, F, R>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract F6<A, B, C, D, E, F, R> delegate();

		@Value.Parameter
		protected abstract String label();

		@Override
		@Value.Auxiliary
		public R apply(A a, B b, C c, D d, E e, F f) {
			return delegate().apply(a, b, c, d, e, f);
		}

		@Override
		public String asHumanReadable() {
			return label();
		}
	}

	static <A, B, C, D, E, F, R> F6<A, B, C, D, E, F, R> withLabel(F6<A, B, C, D, E, F, R> delegate, String label) {
		return ImmutableF6WithLabel.of(delegate, label);
	}

}
