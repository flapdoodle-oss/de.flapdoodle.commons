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

import de.flapdoodle.commons.eval.core.exceptions.EvaluableException;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.parser.ASTNode;
import de.flapdoodle.commons.eval.core.parser.Token;
import de.flapdoodle.commons.eval.core.validation.ParameterValidator;
import de.flapdoodle.commons.reflection.TypeInfo;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

/** Definition of a function parameter. */
@Value.Immutable
public interface Parameter<T> {

	@Value.Parameter
	TypeInfo<T> type();

	@Value.Default
	default boolean isNullable() { return false; }
	/**
	 * Set to true, the parameter will not be evaluated in advance, but the corresponding {@link
	 * ASTNode} will be passed as a parameter value.
	 */
	@Value.Default
	default boolean isLazy() {
		return false;
	}

	List<ParameterValidator<T>> validators();

	static <T> ImmutableParameter<T> of(Class<T> type) {
		return ImmutableParameter.of(TypeInfo.of(type));
	}

	static <T> ImmutableParameter<T> of(TypeInfo<T> type) {
		return ImmutableParameter.of(type);
	}

	static <T> ImmutableParameter<T> lazyWith(Class<T> type) {
		return lazyWith(TypeInfo.of(type));
	}

	static <T> ImmutableParameter<T> lazyWith(TypeInfo<T> type) {
		return ImmutableParameter.of(type).withIsLazy(true);
	}

	static <T> ImmutableParameter<T> nullableWith(Class<T> type) {
		return Parameter.nullableWith(TypeInfo.of(type));
	}

	static <T> ImmutableParameter<T> nullableWith(TypeInfo<T> type) {
		return ImmutableParameter.of(type).withIsNullable(true);
	}

	@Value.Auxiliary
	@Deprecated
	default void validate(Token token, Evaluated<?> parameterValue) throws EvaluationException {
		Optional<EvaluableException> error = validationError(parameterValue);
		if (error.isPresent()) {
			throw new EvaluationException(token, error.get());
		}
	}

	@Value.Auxiliary
	default Optional<EvaluableException> validationError(Evaluated<?> parameterValue) {
		if (isNullable() && parameterValue.isNull()) {
			return Optional.empty();
		}
		
		if (type().isInstance(parameterValue.wrapped())) {
			T value = type().cast(parameterValue.wrapped());
			for (ParameterValidator<T> validator : validators()) {
				Optional<EvaluableException> error = validator.validate(value);
				if (error.isPresent()) {
					return error;
				}
			}
		} else {
			return Optional.of(EvaluableException.ofUnsupportedDataTypeInOperation());
		}
		return Optional.empty();
	}
}
