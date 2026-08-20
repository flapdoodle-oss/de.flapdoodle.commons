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

import com.google.common.base.Preconditions;
import de.flapdoodle.commons.grapheval.types.HasHumanReadableLabel;
import org.immutables.value.Value;

import javax.annotation.Nullable;

import static de.flapdoodle.commons.grapheval.types.HasHumanReadableLabel.asHumanReadable;

@FunctionalInterface
public interface FN6<A, B, C, D, E, F, R> {
	@Nullable R apply(@Nullable A a, @Nullable B b, @Nullable C c, @Nullable D d, @Nullable E e, @Nullable F f);

	@Value.Immutable
	abstract class FN6WithLabel<A, B, C, D, E, F, R> implements FN6<A, B, C, D, E, F, R>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract FN6<A, B, C, D, E, F, R> delegate();

		@Value.Parameter
		protected abstract String label();

		@Nullable
		@Override
		@Value.Auxiliary
		public R apply(@Nullable A a, @Nullable B b, @Nullable C c, @Nullable D d, @Nullable E e, @Nullable F f) {
			return delegate().apply(a, b, c, d, e, f);
		}

		@Override
		public String asHumanReadable() {
			return label();
		}

	}

	static <A, B, C, D, E, F, R> FN6<A, B, C, D, E, F, R> withLabel(FN6<A, B, C, D, E, F, R> delegate, String label) {
		return ImmutableFN6WithLabel.of(delegate, label);
	}

	@Value.Immutable
	abstract class FN6wrapF6<A, B, C, D, E, F, R> implements FN6<A, B, C, D, E, F, R>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract F6<A, B, C, D, E, F, R> delegate();

		@Nullable
		@Override
		@Value.Auxiliary
		public R apply(@Nullable A a, @Nullable B b, @Nullable C c, @Nullable D d, @Nullable E e, @Nullable F f) {
			return (a!=null && b!=null && c!=null && d!=null && e!=null && f!=null)
				? delegate().apply(a, b, c, d, e, f)
				: null;
		}

		@Override
		public String asHumanReadable() {
			return HasHumanReadableLabel.asHumanReadable(delegate());
		}
	}

	static <A, B, C, D, E, F, R> FN6<A, B, C, D, E, F, R> mapOnlyIfNotNull(F6<A, B, C, D, E, F, R> delegate) {
		return ImmutableFN6wrapF6.of(delegate);
	}

	@Value.Immutable
	abstract class FN6checkNull<A, B, C, D, E, F, R> implements FN6<A, B, C, D, E, F, R>, HasHumanReadableLabel {
		@Value.Parameter
		protected abstract F6<A, B, C, D, E, F, R> delegate();

		@Value.Parameter
		protected abstract String a();

		@Value.Parameter
		protected abstract String b();

		@Value.Parameter
		protected abstract String c();

		@Value.Parameter
		protected abstract String d();

		@Value.Parameter
		protected abstract String e();

		@Value.Parameter
		protected abstract String f();

		@Nullable
		@Override
		@Value.Auxiliary
		public R apply(@Nullable A a, @Nullable B b, @Nullable C c, @Nullable D d, @Nullable E e, @Nullable F f) {
			Preconditions.checkNotNull(a,"%s: %s is null", asHumanReadable(), a());
			Preconditions.checkNotNull(b,"%s: %s is null", asHumanReadable(), b());
			Preconditions.checkNotNull(c,"%s: %s is null", asHumanReadable(), c());
			Preconditions.checkNotNull(d,"%s: %s is null", asHumanReadable(), d());
			Preconditions.checkNotNull(e,"%s: %s is null", asHumanReadable(), e());
			Preconditions.checkNotNull(f,"%s: %s is null", asHumanReadable(), f());
			return Preconditions.checkNotNull(delegate().apply(a, b, c, d, e, f), "%s: result is null", asHumanReadable());
		}

		@Override
		public String asHumanReadable() {
			return HasHumanReadableLabel.asHumanReadable(delegate());
		}
	}

	static <A, B, C, D, E, F, R> FN6<A, B, C, D, E, F, R> checkNull(F6<A, B, C, D, E, F, R> delegate, Object labelA, Object labelB, Object labelC, Object labelD, Object labelE, Object labelF) {
		return ImmutableFN6checkNull.of(delegate, asHumanReadable(labelA), asHumanReadable(labelB), asHumanReadable(labelC), asHumanReadable(labelD), asHumanReadable(labelE), asHumanReadable(labelF));
	}

}
