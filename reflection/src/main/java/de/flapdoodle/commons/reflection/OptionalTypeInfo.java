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
package de.flapdoodle.commons.reflection;

import de.flapdoodle.commons.checks.Preconditions;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public abstract class OptionalTypeInfo<T> implements TypeInfo<Optional<T>> {

	@Value.Parameter
	public abstract TypeInfo<T> value();

	@Override
	public boolean isInstance(Object instance) {
		return instance instanceof Optional && ((Optional<?>) instance)
			.map(it -> value().isInstance(it))
			.orElse(true);
	}

	@Override
	public boolean isAssignable(TypeInfo<?> other) {
		return other instanceof OptionalTypeInfo && value().isAssignable(((OptionalTypeInfo<?>) other).value());
	}

	@Override
	public Optional<T> cast(Object instance) {
		Preconditions.checkArgument(isInstance(instance), "type mismatch: %s is not a %s", instance, this);
		return (Optional<T>) instance;
	}

	public static <T> TypeInfo<Optional<T>> of(TypeInfo<T> valueType) {
		return ImmutableOptionalTypeInfo.of(valueType);
	}
}
