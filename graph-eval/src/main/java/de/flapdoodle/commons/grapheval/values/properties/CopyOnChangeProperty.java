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
import de.flapdoodle.commons.grapheval.types.Id;
import de.flapdoodle.commons.grapheval.values.domain.CopyOnChangeValue;
import de.flapdoodle.commons.reflection.TypeInfo;
import de.flapdoodle.commons.types.CopyOnChangeLens;
import de.flapdoodle.commons.types.Lens;
import org.immutables.value.Value;

import java.util.function.BiFunction;
import java.util.function.Function;

@Value.Immutable
public abstract class CopyOnChangeProperty<O, T> implements IsChangeableProperty<O, T> {
	@Value.Parameter
	protected abstract TypeInfo<O> type();

	@Value.Parameter
	protected abstract String name();

	@Value.Parameter
	protected abstract Lens<O, T> lens();

	@Override
	public String toString() {
		return getClass().getSimpleName()+"{"+type().simpleName()+"."+name()+"}";
	}

	@Override public String asHumanReadable() {
		return type().simpleName()+"."+name()+"#rw";
	}
	
	@Override
	@Value.Auxiliary
	public T get(O instance) {
		Preconditions.checkArgument(type().isInstance(instance),"instance type mismatch: %s != %s", type(), instance);
		return lens().read(instance);
	}

	@Override
	@Value.Auxiliary
	public O change(O instance, T value) {
		Preconditions.checkArgument(type().isInstance(instance),"instance type mismatch: %s != %s", type(), instance);
		return lens().change(instance, value);
	}

	@Override
	public CopyOnChangeValue<O, T> withId(Id<O> id) {
		return CopyOnChangeValue.of(id, this);
	}

	public static <O, T> ImmutableCopyOnChangeProperty<O,T> of(TypeInfo<O> type, String name, Lens<O, T> lens) {
		return ImmutableCopyOnChangeProperty.of(type, name, lens);
	}

	public static <O, T> ImmutableCopyOnChangeProperty<O,T> of(TypeInfo<O> type, String name, Function<O, T> getter, BiFunction<O, T, O> copyOnWrite) {
		return of(type, name, CopyOnChangeLens.of(getter, copyOnWrite));
	}

	public static <O, T> ImmutableCopyOnChangeProperty<O,T> of(Class<O> type, String name, Lens<O, T> lens) {
		return of(TypeInfo.of(type), name, lens);
	}

	public static <O, T> ImmutableCopyOnChangeProperty<O,T> of(Class<O> type, String name, Function<O, T> getter, BiFunction<O, T, O> copyOnWrite) {
		return of(type, name, CopyOnChangeLens.of(getter, copyOnWrite));
	}


}
