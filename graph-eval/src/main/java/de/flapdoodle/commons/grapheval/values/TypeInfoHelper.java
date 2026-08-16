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
package de.flapdoodle.commons.grapheval.values;

import de.flapdoodle.commons.grapheval.types.HasHumanReadableLabel;
import de.flapdoodle.commons.reflection.ClassTypeInfo;
import de.flapdoodle.commons.reflection.ListTypeInfo;
import de.flapdoodle.commons.reflection.TypeInfo;
import de.flapdoodle.commons.types.Pair;
import de.flapdoodle.commons.types.PairTypeInfo;

public abstract class TypeInfoHelper {

	public static String asHumanReadable(TypeInfo<?> typeInfo) {
		if (typeInfo instanceof ClassTypeInfo) {
			return ((ClassTypeInfo<?>) typeInfo).type().getSimpleName();
		}
		if (typeInfo instanceof PairTypeInfo) {
			PairTypeInfo<?, ?> pair = (PairTypeInfo<?, ?>) typeInfo;
			return "Pair("+asHumanReadable(pair.first())+", "+asHumanReadable(pair.second())+")";
		}
		if (typeInfo instanceof ListTypeInfo) {
			ListTypeInfo<?> list = (ListTypeInfo<?>) typeInfo;
			return "List("+asHumanReadable(list.elements())+")";
		}
		if (typeInfo instanceof HasHumanReadableLabel) {
			return ((HasHumanReadableLabel) typeInfo).asHumanReadable();
		}
		return typeInfo.toString();
	}
}
