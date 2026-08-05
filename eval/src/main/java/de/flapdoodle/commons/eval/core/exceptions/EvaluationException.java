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
package de.flapdoodle.commons.eval.core.exceptions;

import de.flapdoodle.commons.eval.core.parser.Token;

import java.util.List;

/** Exception while evaluating the parsed expression. */
public class EvaluationException extends BaseException {

	public EvaluationException(Token token, String message) {
		super(
			token.start(),
			token.start() + token.value().length(),
			token.value(),
			message);
	}

	public EvaluationException(Token token, EvaluableException cause) {
		super(
			token.start(),
			token.start() + token.value().length(),
			token.value(),
			cause);
	}

	public EvaluationException of(Token token, List<? extends EvaluableException> causes) {
		EvaluableException cause = causes.get(0);
		for (int i = 1; i < causes.size(); i++) {
			EvaluableException c = causes.get(i);
			cause.addSuppressed(c);
		}
		return new EvaluationException(token, cause);
	}

	public static EvaluationException ofUnsupportedDataTypeInOperation(Token token) {
		return new EvaluationException(token, "Unsupported data types in operation");
	}

	public static class AsRuntimeException extends RuntimeException {

		private final EvaluationException wrapped;

		public AsRuntimeException(EvaluationException wrapped) {
			this.wrapped = wrapped;
		}

		public EvaluationException wrapped() {
			return wrapped;
		}
	}
}
