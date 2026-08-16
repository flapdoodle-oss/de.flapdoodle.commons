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
package de.flapdoodle.commons.grapheval.explain;

import de.flapdoodle.commons.grapheval.Unvalidated;
import de.flapdoodle.commons.grapheval.Value;
import de.flapdoodle.commons.grapheval.calculate.Calculation;
import de.flapdoodle.commons.grapheval.types.HasHumanReadableLabel;
import de.flapdoodle.commons.grapheval.validation.Validation;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

public abstract class RuleVertex {

	@Immutable
	public static abstract class ValueVertex<T> extends RuleVertex implements HasHumanReadableLabel {
		@Parameter
		public abstract Value<T> value();

		@Override
		public String asHumanReadable() {
			Value<?> realValue = value() instanceof Unvalidated
				? ((Unvalidated<?>) value()).wrapped()
				: value();

			return HasHumanReadableLabel.asHumanReadable(realValue);
		}
	}

	@Immutable
	public static abstract class CalculationVertex<T> extends RuleVertex implements HasHumanReadableLabel {
		@Parameter
		public abstract Calculation<T> calculation();

		@Override
		public String asHumanReadable() {
			return HasHumanReadableLabel.asHumanReadable(calculation());
		}
	}

	@Immutable
	public static abstract class ValidationVertex<T> extends RuleVertex implements HasHumanReadableLabel {
		@Parameter
		public abstract Validation<T> calculation();

		@Override
		public String asHumanReadable() {
			return HasHumanReadableLabel.asHumanReadable(calculation());
		}
	}

	public static <T> RuleVertex value(Value<? extends T> value) {
		return ImmutableValueVertex.of(value);
	}

	public static <T> RuleVertex calculation(Calculation<T> calculation) {
		return ImmutableCalculationVertex.of(calculation);
	}

	public static <T> RuleVertex validation(Validation<T> validation) {
		return ImmutableValidationVertex.of(validation);
	}
}
