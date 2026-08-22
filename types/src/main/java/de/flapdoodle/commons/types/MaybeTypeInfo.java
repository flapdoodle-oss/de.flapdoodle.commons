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
public abstract class MaybeTypeInfo<T> implements TypeInfo<Maybe<T>> {
	@Value.Parameter
	public abstract TypeInfo<T> type();

	@Override
	public String simpleName() {
		return Maybe.class.getSimpleName()+"<"+type().simpleName()+">";
	}

	@Override
	public boolean isInstance(Object instance) {
		if (instance instanceof Maybe) {
			Maybe<?> casted = (Maybe<?>) instance;
			return !casted.hasSome() || type().isInstance(casted.get());
		}
		return false;
	}

	@Override
	public boolean isAssignable(TypeInfo<?> other) {
		return other instanceof  MaybeTypeInfo && type().isAssignable(((MaybeTypeInfo<?>) other).type());
	}

	@Override
	public Maybe<T> cast(Object instance) {
		Preconditions.checkArgument(isInstance(instance), "type mismatch: %s is not a %s", instance, this);
		return (Maybe<T>) instance;
	}

	public static <T> TypeInfo<Maybe<T>> of(TypeInfo<T> type) {
		return ImmutableMaybeTypeInfo.of(type);
	}

	public static <T> TypeInfo<Maybe<T>> of(Class<T> type) {
		return of(TypeInfo.of(type));
	}

}
