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
package de.flapdoodle.commons.grapheval.values.properties;

import com.google.common.base.Preconditions;
import de.flapdoodle.commons.grapheval.types.HasHumanReadableLabel;
import de.flapdoodle.commons.grapheval.types.Id;
import de.flapdoodle.commons.grapheval.values.domain.ReadOnlyValue;
import de.flapdoodle.commons.reflection.TypeInfo;
import org.immutables.value.Value;

import java.util.function.Function;

@Value.Immutable
public abstract class ReadOnlyProperty<O, T> implements IsReadOnlyProperty<O, T> {
	@Value.Parameter
	protected abstract TypeInfo<O> type();

	@Value.Parameter
	protected abstract String name();

	@Value.Parameter
	protected abstract Function<O, T> getter();

	@Override
	public String toString() {
		return getClass().getSimpleName()+"{"+type().simpleName()+"."+name()+"}";
	}

	@Override
	public String asHumanReadable() {
		return type().simpleName()+"."+name()+"()";
	}
	
	@Override
	@Value.Auxiliary
	public T get(O instance) {
		Preconditions.checkArgument(type().isInstance(instance),"instance type mismatch: %s != %s", type(), instance);
		return getter().apply(instance);
	}

	@Override
	public ReadOnlyValue<O, T> withId(Id<O> id) {
		return ReadOnlyValue.of(id, this);
	}

	public static <O, T> ImmutableReadOnlyProperty<O,T> of(Class<O> type, String name, Function<O, T> getter) {
		return ImmutableReadOnlyProperty.of(TypeInfo.of(type), name, getter);
	}

	public static <O, T> ImmutableReadOnlyProperty<O,T> of(TypeInfo<O> type, String name, Function<O, T> getter) {
		return ImmutableReadOnlyProperty.of(type, name, getter);
	}
}
