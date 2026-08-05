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
package de.flapdoodle.commons.eval.example.evaluables.basic;

import de.flapdoodle.commons.eval.core.EvaluationContext;
import de.flapdoodle.commons.eval.core.VariableResolver;
import de.flapdoodle.commons.eval.core.evaluables.Parameter;
import de.flapdoodle.commons.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.commons.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.parser.Token;
import de.flapdoodle.commons.eval.example.Value;

public class PropertyAccess extends TypedEvaluables.Wrapper {

	public static class MapAccess implements TypedEvaluable.Arg2<Value.MapValue, Value.StringValue, Value<?>> {

		@Override
		public Value<?> evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.MapValue first, Value.StringValue second)
			throws EvaluationException {
			if (!first.wrapped().containsKey(second.wrapped())) {
				throw new EvaluationException(
					token, String.format("Field '%s' not found in structure", second.wrapped()));
			}

			return first.wrapped().get(second.wrapped());
		}
	}

	public PropertyAccess() {
		super(TypedEvaluables.builder()
			.addList(TypedEvaluable.of((Class) Value.class, Parameter.of(Value.MapValue.class), Parameter.of(Value.StringValue.class), new MapAccess()))
			.build());
	}
}
