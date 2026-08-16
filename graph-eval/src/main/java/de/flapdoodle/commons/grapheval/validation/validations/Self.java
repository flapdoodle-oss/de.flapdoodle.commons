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
package de.flapdoodle.commons.grapheval.validation.validations;

import com.google.common.collect.ImmutableSet;
import de.flapdoodle.commons.grapheval.ValueSource;
import de.flapdoodle.commons.grapheval.types.HasHumanReadableLabel;
import de.flapdoodle.commons.grapheval.validation.ErrorMessage;
import de.flapdoodle.commons.grapheval.validation.ValidatedValueLookup;
import de.flapdoodle.commons.grapheval.validation.Validation;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Value.Immutable(builder = false)
public abstract class Self<X> implements Validation<X>, HasHumanReadableLabel {

	@Value.Parameter
	protected abstract V0<X> validation();

	@Override
	public Set<ValueSource<?>> sources() {
		return ImmutableSet.of();
	}

	@Override
	public List<ErrorMessage> validate(Optional<X> unvalidatedValue, ValidatedValueLookup values) {
		return validation().validate(unvalidatedValue);
	}

	@Override
	public String asHumanReadable() {
		return HasHumanReadableLabel.asHumanReadable(validation());
	}

	public static <X> Self<X> with(
		de.flapdoodle.commons.grapheval.Value<X> destination,
		V0<X> validation
	) {
		return ImmutableSelf.of(destination, validation);
	}
}
