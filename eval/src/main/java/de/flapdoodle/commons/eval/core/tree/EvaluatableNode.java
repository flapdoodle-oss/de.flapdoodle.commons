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
import de.flapdoodle.commons.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.commons.eval.core.evaluables.TypedEvaluableByArguments;
import de.flapdoodle.commons.eval.core.exceptions.EvaluableException;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.parser.Token;
import de.flapdoodle.commons.reflection.TypeInfo;
import de.flapdoodle.commons.types.Either;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@org.immutables.value.Value.Immutable
public abstract class EvaluatableNode extends Node {
	@org.immutables.value.Value.Parameter
	protected abstract TypedEvaluableByArguments evaluatable();
	@org.immutables.value.Value.Parameter
	protected abstract List<Node> parameters();
	@org.immutables.value.Value.Parameter
	protected abstract EvaluableExceptionMapper exceptionMapper();

	@Override
	public Evaluated<?> evaluate(VariableResolver variableResolver, EvaluationContext context) throws EvaluationException {
		List<Evaluated<?>> parameterResults = new ArrayList<>();
		for (int i = 0; i < parameters().size(); i++) {
			Node parameter = parameters().get(i);
			try {
				parameterResults.add(parameter.evaluate(variableResolver, context));
			} catch (EvaluationException ex) {
				// used in condition where one branch might not return
				parameterResults.add(Evaluated.value(exceptionMapper().map(ex)));
			}
		}
		Either<TypedEvaluable<?>, EvaluableException> evaluatable = evaluatable().find(parameterResults);
		if (evaluatable.isLeft()) {
			try {
				Evaluated<?> evaluated = evaluatable.left().evaluate(variableResolver, context, token(), parameterResults);
				Optional<EvaluationException> matchedException = exceptionMapper().match(evaluated);
				if (matchedException.isPresent()) {
					throw matchedException.get();
				}
				return evaluated;
			} catch (EvaluationException.AsRuntimeException wrapping) {
				throw wrapping.wrapped();
			}
		} else {
			throw new EvaluationException(token(), evaluatable.right());
		}
	}

	@Override
	public TypeInfo<?> evaluateType(VariableTypeResolver variableResolver) throws EvaluationException {
		List<TypeInfo<?>> parameterResults = new ArrayList<>();
		for (int i = 0; i < parameters().size(); i++) {
			Node parameter = parameters().get(i);
			try {
				parameterResults.add(parameter.evaluateType(variableResolver));
			} catch (EvaluationException ex) {
				// used in condition where one branch might not return
				parameterResults.add(Evaluated.value(exceptionMapper().map(ex)).type());
			}
		}
		Either<TypedEvaluable<?>, EvaluableException> evaluatable = evaluatable().findType(parameterResults);
		if (evaluatable.isLeft()) {
			try {
				TypeInfo<?> evaluatedType = evaluatable.left().signature().returnType();
				return evaluatedType;
			} catch (EvaluationException.AsRuntimeException wrapping) {
				throw wrapping.wrapped();
			}
		} else {
			throw new EvaluationException(token(), evaluatable.right());
		}
	}

	public static EvaluatableNode of(
		Token token,
		TypedEvaluableByArguments function,
		List<Node> parameters,
		EvaluableExceptionMapper exceptionMapper) {
		return ImmutableEvaluatableNode.builder()
			.token(token)
			.evaluatable(function)
			.parameters(parameters)
			.exceptionMapper(exceptionMapper)
			.build();
	}
}
