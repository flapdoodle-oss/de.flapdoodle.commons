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

public class IndexedAccess extends TypedEvaluables.Wrapper {

	public static class ValueArrayAccess implements TypedEvaluable.Arg2<Value.ArrayValue, Value.NumberValue, Value<?>> {

		@Override
		public Value<?> evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.ArrayValue first, Value.NumberValue second)
			throws EvaluationException {
			return first.wrapped().get(second.wrapped().intValue());
		}
	}

	public static class StringAccess implements TypedEvaluable.Arg2<Value.StringValue, Value.NumberValue, Value<?>> {
		@Override
		public Value<?> evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.StringValue first, Value.NumberValue second)
			throws EvaluationException {
			return Value.of(""+first.wrapped().charAt(second.wrapped().intValue()));
		}
	}

	public IndexedAccess() {
		super(TypedEvaluables.builder()
			.addList(TypedEvaluable.of((Class) Value.class, Parameter.of(Value.ArrayValue.class), Parameter.of(Value.NumberValue.class), new ValueArrayAccess()))
			.addList(TypedEvaluable.of((Class) Value.class, Parameter.of(Value.StringValue.class), Parameter.of(Value.NumberValue.class), new StringAccess()))
			.addList(TypedEvaluable.of((Class) Value.class, Parameter.of(Value.MapValue.class), Parameter.of(Value.StringValue.class), new PropertyAccess.MapAccess()))
			.build());
	}
}
