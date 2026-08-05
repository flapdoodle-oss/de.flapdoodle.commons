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
import de.flapdoodle.commons.reflection.TypeInfo;
import org.immutables.builder.Builder;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Value.Immutable
public abstract class Signature<T> {
	public abstract List<Parameter<?>> parameters();

	// vararg is at least ONE value, zero values are not allowed
	@Value.Default
	public boolean isVarArg() {
		return false;
	}

	@Builder.Parameter
	public abstract TypeInfo<T> returnType();

	@Value.Derived
	public int minNumberOfArguments() {
		return parameters().size();
	}

	@Value.Derived
	public int maxNumberOfArguments() {
		return isVarArg()
			? Integer.MAX_VALUE
			: parameters().size();
	}

	@Value.Auxiliary
	public String asHumanReadable() {
		return "" +returnType()+"(" + (isVarArg() ? "vararg " : "") + parameters().stream().map(it -> it.type().toString()+((it.isNullable()) ? "?" : "")).collect(Collectors.joining(", "))+")";
	}

	@Value.Auxiliary
	public Parameter<?> get(int index) {
		if (isVarArg() && index >= parameters().size()) {
			index = parameters().size() - 1;
		}
		return parameters().get(index);
	}

	@Value.Auxiliary
	public Optional<EvaluableException> validateArguments(List<? extends Evaluated<?>> arguments) {
		if (minNumberOfArguments() > arguments.size()) return Optional.of(EvaluableException.of("not enough(<%s) arguments: %s", minNumberOfArguments(), arguments.size()));
		if (arguments.size() > maxNumberOfArguments()) return Optional.of(EvaluableException.of("to many(>%s) arguments: ", maxNumberOfArguments(), arguments.size()));

		for (int i = 0; i < minNumberOfArguments(); i++) {
			Evaluated<?> value = arguments.get(i);
			Parameter<?> parameter = get(i);
			TypeInfo<?> type = parameter.type();

			if (parameter.isNullable() && value.isNull()) {
				if (!type.isAssignable(value.type())) return Optional.of(EvaluableException.of("wrong nullable type: %s != %s", type, value.type()));
			} else {
				if (!type.isInstance(value.wrapped())) return Optional.of(EvaluableException.of("wrong type: %s != %s (%s)", type, value.type(), value.wrapped()));
			}
			Optional<EvaluableException> error = parameter.validationError(value);
			if (error.isPresent()) return error;
		}
		if (isVarArg()) {
			Parameter<?> parameter = get(minNumberOfArguments() - 1);
			TypeInfo<?> type = parameter.type();
			for (int i = minNumberOfArguments(); i < arguments.size(); i++) {
				Evaluated<?> value = arguments.get(i);
				if (parameter.isNullable() && value.isNull()) {
					if (!type.isAssignable(value.type())) return Optional.of(EvaluableException.of("wrong nullable type: %s != %s", type, value.type()));
				} else {
					if (!type.isInstance(value.wrapped())) return Optional.of(EvaluableException.of("wrong type: %s != %s (%s)", type, value.getClass(), value));
				}
				Optional<EvaluableException> error = parameter.validationError(value);
				if (error.isPresent()) return error;
			}
		}
		return Optional.empty();
	}

	@Value.Auxiliary
	public Optional<EvaluableException> validateArgumentTypes(List<? extends TypeInfo<?>> arguments) {
		if (minNumberOfArguments() > arguments.size()) return Optional.of(EvaluableException.of("not enough(<%s) arguments: %s", minNumberOfArguments(), arguments.size()));
		if (arguments.size() > maxNumberOfArguments()) return Optional.of(EvaluableException.of("to many(>%s) arguments: ", maxNumberOfArguments(), arguments.size()));

		for (int i = 0; i < minNumberOfArguments(); i++) {
			TypeInfo<?> valueType = arguments.get(i);
			Parameter<?> parameter = get(i);
			TypeInfo<?> type = parameter.type();

			if (!type.isAssignable(valueType)) return Optional.of(EvaluableException.of("wrong type: %s != %s", type, valueType));
		}
		if (isVarArg()) {
			Parameter<?> parameter = get(minNumberOfArguments() - 1);
			TypeInfo<?> type = parameter.type();
			for (int i = minNumberOfArguments(); i < arguments.size(); i++) {
				TypeInfo<?> valueType = arguments.get(i);
				if (!type.isAssignable(valueType)) return Optional.of(EvaluableException.of("wrong type: %s != %s", type, valueType));
			}
		}
		return Optional.empty();
	}

	public static <T> Signature<T> of(Class<T> returnType, List<? extends Parameter<?>> parameters) {
		return of(TypeInfo.of(returnType), parameters);
	}

	public static <T> Signature<T> of(TypeInfo<T> returnType, List<? extends Parameter<?>> parameters) {
		return ImmutableSignature.builder(returnType)
			.addAllParameters(parameters)
			.build();
	}

	public static <T> Signature<T> of(Class<T> returnType, Parameter<?>... parameters) {
		return of(TypeInfo.of(returnType), parameters);
	}

	public static <T> Signature<T> of(TypeInfo<T> returnType, Parameter<?>... parameters) {
		return ImmutableSignature.builder(returnType)
			.addParameters(parameters)
			.build();
	}

	public static <T> Signature<T> ofVarArg(Class<T> returnType, Parameter<?>... parameters) {
		return ofVarArg(TypeInfo.of(returnType), parameters);
	}

	public static <T> Signature<T> ofVarArg(TypeInfo<T> returnType, Parameter<?>... parameters) {
		return ImmutableSignature.builder(returnType)
			.addParameters(parameters)
			.isVarArg(true)
			.build();
	}
}
