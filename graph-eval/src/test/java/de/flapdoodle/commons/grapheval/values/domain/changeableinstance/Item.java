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
package de.flapdoodle.commons.grapheval.values.domain.changeableinstance;

import de.flapdoodle.commons.grapheval.calculate.Calculate;
import de.flapdoodle.commons.grapheval.rules.Rules;
import de.flapdoodle.commons.grapheval.types.Id;
import de.flapdoodle.commons.grapheval.values.domain.ChangeableInstance;
import de.flapdoodle.commons.grapheval.values.domain.ChangeableValue;
import de.flapdoodle.commons.grapheval.values.domain.HasRules;
import de.flapdoodle.commons.grapheval.values.domain.ReadableValue;
import de.flapdoodle.commons.grapheval.values.properties.*;
import de.flapdoodle.commons.reflection.TypeInfo;
import de.flapdoodle.commons.types.Maybe;
import org.immutables.value.Value;

import javax.annotation.Nullable;

import java.util.function.Function;

import static de.flapdoodle.commons.grapheval.values.properties.Properties.copyOnChange;
import static de.flapdoodle.commons.grapheval.values.properties.Properties.changeable;
import static de.flapdoodle.commons.grapheval.values.properties.Properties.readOnly;

@Value.Immutable
public interface Item extends ChangeableInstance<Item>, IsChangeableInstance<Item, ImmutableItem>, HasRules {
	IsChangeableProperty<Item, Double> sumProperty = changeable(Item.class, "sum", Item::sum, ImmutableItem::withSum);
	IsReadOnlyProperty<Item, Double> priceProperty = readOnly(Item.class, "price", Item::price);
	IsReadOnlyProperty<Item, Integer> quantityProperty = readOnly(Item.class, "quantity", Item::quantity);
	IsChangeableProperty<Item, Boolean> isCheapestProperty = changeable(Item.class, "isCheapest", Item::isCheapest, ImmutableItem::withIsCheapest);

	@Value.Default
	@Override
	default Id<Item> id() {
		return Id.idFor(TypeInfo.of(Item.class));
	}

	@Nullable String name();

	@Nullable Integer quantity();

	@Nullable Double price();

	@Nullable Double sum();

	@Nullable Boolean isCheapest();

	@Override
	default Item change(Function<ImmutableItem, Item> change) {
		return change.apply(ImmutableItem.copyOf(this));
	}

	@Override
	default <T> Item change(ChangeableValue<?, T> id, T value) {
		if (id.id().equals(id())) {
			return ((ChangeableValue<Item, T>) id).change(this, value);
		}
		return this;
	}

	@Override
	default <T> Maybe<T> findValue(ReadableValue<?, T> id) {
		if (id.id().equals(id())) {
			return Maybe.some(((ReadableValue<Item, T>) id).get(this));
		}
		return Maybe.none();
	}

	@Override
	@Value.Auxiliary
	default Rules addRulesTo(Rules current) {
		return current
			.add(Calculate
				.value(Item.sumProperty.withId(id()))
				.using(Item.priceProperty.withId(id()), Item.quantityProperty.withId(id()))
				.ifAllSetBy((price, quantity) -> price * quantity,"price*quantity"));
	}

	static ImmutableItem.Builder builder() {
		return ImmutableItem.builder();
	}
}
