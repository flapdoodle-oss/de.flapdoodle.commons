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
package de.flapdoodle.commons.eval.doc;

import de.flapdoodle.commons.eval.core.EvaluationContext;
import de.flapdoodle.commons.eval.core.Nullable;
import de.flapdoodle.commons.eval.core.VariableResolver;
import de.flapdoodle.commons.eval.core.evaluables.Parameter;
import de.flapdoodle.commons.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.commons.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.parser.Token;

import java.util.List;

public class Plus extends TypedEvaluables.Wrapper {

	public static class Int implements TypedEvaluable.Arg2<Integer, Integer, Integer> {

		@Override
		public Integer evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Integer first,
			Integer second) throws EvaluationException {
			return first + second;
		}
	}

	public static class IntNullable implements TypedEvaluable.Arg2<Integer, Integer, Integer> {

		@Override
		public Integer evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, @Nullable Integer first,
			@Nullable Integer second) throws EvaluationException {
			if (first != null && second != null) return first + second;
			return first != null ? first : second;
		}
	}

	public static class ToString<A, B> implements TypedEvaluable.Arg2<A, B, String> {

		@Override
		public String evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, A first,
										  B second) throws EvaluationException {
			return "" + first + second;
		}
	}

	public static class Sum implements TypedEvaluable.VarArg1<Integer, Integer> {

		@Override
		public Integer evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, List<Integer> arguments)
			throws EvaluationException {
			int sum = 0;
			for (Integer parameter : arguments) {
				sum = sum + parameter;
			}
			return sum;
		}
	}

	public Plus() {
		super(TypedEvaluables.builder()
			.addList(TypedEvaluable.of(Integer.class, Integer.class, Integer.class, new Int()))
			.addList(TypedEvaluable.of(Integer.class, Parameter.nullableWith(Integer.class), Parameter.nullableWith(Integer.class), new IntNullable()))
			.addList(TypedEvaluable.ofVarArg(Integer.class, Integer.class, new Sum()))
			.addList(TypedEvaluable.of(String.class, Object.class, Object.class, new ToString<>()))
			.build());
	}
}
