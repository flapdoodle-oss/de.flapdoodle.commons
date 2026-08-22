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
package de.flapdoodle.commons.grapheval.values.domain;

import de.flapdoodle.commons.grapheval.types.HasHumanReadableLabel;
import de.flapdoodle.commons.grapheval.types.Id;
import de.flapdoodle.commons.grapheval.values.properties.CopyOnChangeProperty;
import de.flapdoodle.commons.grapheval.values.properties.IsChangeable;
import de.flapdoodle.commons.grapheval.values.properties.IsChangeableProperty;
import de.flapdoodle.commons.grapheval.values.properties.IsReadable;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

@Immutable
public abstract class CopyOnChangeValue<O, T> implements ChangeableValue<O, T>, HasHumanReadableLabel {
	@Parameter
	public abstract Id<O> id();
	@Parameter
	protected abstract IsChangeableProperty<O, T> property();

	@Override
	public String asHumanReadable() {
		return property().asHumanReadable()+" {"+id().asHumanReadable()+"}";
	}
	
	@Override
	public T get(O instance) {
		return property().get(instance);
	}

	@Override
	public O change(O instance, T value) {
		return property().change(instance, value);
	}

	public static <O, T> ImmutableCopyOnChangeValue<O, T> of(Id<O> id, IsChangeableProperty<O, T> property) {
		return ImmutableCopyOnChangeValue.of(id, property);
	}
}
