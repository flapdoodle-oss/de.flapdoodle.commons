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
package de.flapdoodle.commons.eval.core.evaluables;

import de.flapdoodle.commons.eval.core.EvaluationContext;
import de.flapdoodle.commons.eval.core.VariableResolver;
import de.flapdoodle.commons.eval.core.exceptions.EvaluableException;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.parser.Token;

import java.util.List;
import java.util.Optional;

class TypedEvaluableAdapter<T> implements TypedEvaluable<T> {
	private final Signature<T> signature;
	private final Evaluable<T> delegate;

	public TypedEvaluableAdapter(Signature<T> signature, Evaluable<T> delegate) {
		this.signature = signature;
		this.delegate = delegate;
	}

	public Signature<T> signature() {
		return signature;
	}

	@Override
	public Evaluated<T> evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, List<? extends Evaluated<?>> arguments)
		throws EvaluationException {
		checkArguments(token, arguments);
		return Evaluated.ofNullable(signature.returnType(), delegate.evaluate(variableResolver, evaluationContext, token, Evaluated.unwrap(arguments)));
	}

	protected void checkArguments(Token token, List<? extends Evaluated<?>> arguments) throws EvaluationException {
		Optional<EvaluableException> error = signature().validateArguments(arguments);
		if (error.isPresent()) {
			throw new EvaluationException(token, error.get());
		}
	}

	@Override
	public String toString() {
		return "TypedEvaluableAdapter{" +
			"signature=" + signature +
			", delegate=" + delegate +
			'}';
	}
}
