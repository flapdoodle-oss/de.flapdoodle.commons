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
package de.flapdoodle.commons.eval.core.tree;

import de.flapdoodle.commons.eval.core.EvaluationContext;
import de.flapdoodle.commons.eval.core.VariableResolver;
import de.flapdoodle.commons.eval.core.VariableTypeResolver;
import de.flapdoodle.commons.eval.core.evaluables.Evaluated;
import de.flapdoodle.commons.eval.core.parser.Token;
import de.flapdoodle.commons.reflection.TypeInfo;

@org.immutables.value.Value.Immutable
public abstract class ValueNode<T> extends Node {
	@org.immutables.value.Value.Parameter
	protected abstract Evaluated<T> value();

	@Override
	public Evaluated<?> evaluate(VariableResolver variableResolver, EvaluationContext context) {
		return value();
	}

	@Override
	public TypeInfo<?> evaluateType(VariableTypeResolver variableResolver) {
		return value().type();
	}

	public static <T> ValueNode<T> of(Token token, Evaluated<T> value) {
		return ImmutableValueNode.<T>builder()
			.token(token)
			.value(value)
			.build();
	}
}
