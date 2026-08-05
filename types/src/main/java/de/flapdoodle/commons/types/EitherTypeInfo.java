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
public abstract class EitherTypeInfo<L, R> implements TypeInfo<Either<L, R>> {

	@Value.Parameter
	public abstract TypeInfo<L> left();

	@Value.Parameter
	public abstract TypeInfo<R> right();

	@Override
	public boolean isInstance(Object instance) {
		return instance instanceof Either && ((Either<?, ?>) instance)
			.map(l -> left().isInstance(l), r -> right().isInstance(r));
	}

	@Override
	public boolean isAssignable(TypeInfo<?> other) {
		return other instanceof EitherTypeInfo
			&& left().isAssignable(((EitherTypeInfo<?, ?>) other).left())
			&& right().isAssignable(((EitherTypeInfo<?, ?>) other).right());
	}

	@Override
	public Either<L, R> cast(Object instance) {
		Preconditions.checkArgument(isInstance(instance), "type mismatch: %s is not a %s", instance, this);
		return (Either<L, R>) instance;
	}

	public static <L, R> TypeInfo<Either<L, R>> of(TypeInfo<L> first, TypeInfo<R> second) {
		return ImmutableEitherTypeInfo.of(first, second);
	}

	public static <L, R> TypeInfo<Either<L, R>> of(Class<L> first, Class<R> second) {
		return of(TypeInfo.of(first), TypeInfo.of(second));
	}
}
