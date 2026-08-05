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

import de.flapdoodle.commons.checks.Preconditions;
import de.flapdoodle.commons.reflection.TypeInfo;
import org.immutables.value.Value;

@Value.Immutable
public abstract class PairTypeInfo<FIRST, SECOND> implements TypeInfo<Pair<FIRST, SECOND>> {
	@Value.Parameter
	public abstract TypeInfo<FIRST> first();

	@Value.Parameter
	public abstract TypeInfo<SECOND> second();

	@Override
	public boolean isInstance(Object instance) {
		return instance instanceof Pair
			&& first().isInstance(((Pair<?, ?>) instance).first())
			&& second().isInstance(((Pair<?, ?>) instance).second());
	}

	@Override
	public boolean isAssignable(TypeInfo<?> other) {
		return other instanceof PairTypeInfo
			&& first().isAssignable(((PairTypeInfo<?, ?>) other).first())
			&& second().isAssignable(((PairTypeInfo<?, ?>) other).second());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Pair<FIRST, SECOND> cast(Object instance) {
		Preconditions.checkArgument(isInstance(instance), "type mismatch: %s is not a %s", instance, this);
		return (Pair<FIRST, SECOND>) instance;
	}

	public static <FIRST, SECOND> TypeInfo<Pair<FIRST, SECOND>> of(TypeInfo<FIRST> first, TypeInfo<SECOND> second) {
		return ImmutablePairTypeInfo.of(first, second);
	}

	public static <FIRST, SECOND> TypeInfo<Pair<FIRST, SECOND>> of(Class<FIRST> first, Class<SECOND> second) {
		return of(TypeInfo.of(first), TypeInfo.of(second));
	}

}
