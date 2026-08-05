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
import de.flapdoodle.commons.eval.core.validation.ParameterValidator;
import de.flapdoodle.commons.eval.example.Value;
import de.flapdoodle.commons.reflection.TypeInfo;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static de.flapdoodle.commons.eval.example.AssertEither.assertThat;

class TypedEvaluablesTest {
	TypedEvaluable<Value.NumberValue> addOne = TypedEvaluable.of(Value.NumberValue.class, Value.NumberValue.class,
		(valueResolver, evaluationContext, token, argument) ->
			Value.of(argument.wrapped().add(BigDecimal.ONE)));

	TypedEvaluable<Value.NumberValue> sum2 = TypedEvaluable.of(Value.NumberValue.class, Value.NumberValue.class, Value.NumberValue.class,
		(valueResolver, evaluationContext, token, a, b) ->
			Value.of(a.wrapped().add(b.wrapped())));

	TypedEvaluable<Value.NumberValue> negAddOne = TypedEvaluable.of(Value.NumberValue.class,
		Parameter.of(Value.NumberValue.class).withValidators(new ParameterValidator<Value.NumberValue>() {
			@Override
			public Optional<EvaluableException> validate(Value.NumberValue parameterValue) {
				return parameterValue.wrapped().doubleValue() >= 0 ?
					Optional.of(EvaluableException.of("positive number", parameterValue.wrapped())) :
					Optional.empty();
			}
		}),
		(valueResolver, evaluationContext, token, argument) ->
			Value.of(argument.wrapped().add(BigDecimal.ONE)));

	private TypedEvaluables testee = TypedEvaluables.builder()
		.addList(negAddOne)
		.addList(addOne)
		.addList(sum2)
		.build();

	@Test
	void filterByNumberOfArguments() {
		assertThat(testee.filterByNumberOfArguments(1))
			.isPresent()
			.get()
			.asInstanceOf(InstanceOfAssertFactories.type(TypedEvaluables.class))
			.extracting(TypedEvaluables::list, InstanceOfAssertFactories.list(TypedEvaluable.class))
			.containsExactly(negAddOne, addOne);
	}

	@Test
	void filterByArguments() {
		assertThat(testee.find(Evaluated.asList(Value.of(1.23))))
			.isLeft()
			.containsLeft(addOne);

		assertThat(testee.find(Evaluated.asList(Value.of(1.23), Value.of(3.45))))
			.isLeft()
			.containsLeft(sum2);

		assertThat(testee.find(Evaluated.asList(Value.of(-1.23))))
			.isLeft()
			.containsLeft(negAddOne);
	}

	@Test
	void filterByArgumentTypes() {
		assertThat(testee.findType(asTypeList(Value.of(1.23))))
			.isLeft()
			.containsLeft(negAddOne);

		assertThat(testee.findType(asTypeList(Value.of(1.23), Value.of(3.45))))
			.isLeft()
			.containsLeft(sum2);

		assertThat(testee.findType(asTypeList(Value.of(-1.23))))
			.isLeft()
			.containsLeft(negAddOne);
	}

	@Test
	void varArgMustFailOnSecondParameter() {
		TypedEvaluables testee = TypedEvaluables.builder()
			.addList(TypedEvaluable.ofVarArg(Integer.class, Integer.class,
				(variableResolver, evaluationContext, token, arguments) -> arguments.stream().reduce(0, Integer::sum)))
			.addList(TypedEvaluable.ofVarArg(String.class, Boolean.class, String.class,
				(variableResolver, evaluationContext, token, first, last) -> first + ":" + last.stream().collect(Collectors.joining("|"))))
			.build();

		assertThat(testee.find(Evaluated.asList(1, 2)))
			.isLeft();

		assertThat(testee.find(Evaluated.asList(1, BigInteger.ONE)))
			.isRight()
			.rightSatisfies(it -> assertThat(it)
				.hasMessageContaining("no matching signature found"));

		assertThat(testee.find(Evaluated.asList(false, "1", "2")))
			.isLeft();

		assertThat(testee.find(Evaluated.asList(false, BigInteger.ONE)))
			.isRight()
			.rightSatisfies(it -> assertThat(it)
				.hasMessageContaining("no matching signature found"));
	}

	@Test
	void varArgTypeMustFailOnSecondParameter() {
		TypedEvaluables testee = TypedEvaluables.builder()
			.addList(TypedEvaluable.ofVarArg(Integer.class, Integer.class,
				(variableResolver, evaluationContext, token, arguments) -> arguments.stream().reduce(0, Integer::sum)))
			.addList(TypedEvaluable.ofVarArg(String.class, Boolean.class, String.class,
				(variableResolver, evaluationContext, token, first, last) -> first + ":" + last.stream().collect(Collectors.joining("|"))))
			.build();

		assertThat(testee.findType(asTypeList(1, 2)))
			.isLeft();

		assertThat(testee.findType(asTypeList(1, BigInteger.ONE)))
			.isRight()
			.rightSatisfies(it -> assertThat(it)
				.hasMessageContaining("no matching signature found"));

		assertThat(testee.findType(asTypeList(false, "1", "2")))
			.isLeft();

		assertThat(testee.findType(asTypeList(false, BigInteger.ONE)))
			.isRight()
			.rightSatisfies(it -> assertThat(it)
				.hasMessageContaining("no matching signature found"));
	}

	private List<? extends TypeInfo<?>> asTypeList(Object... values) {
		return Arrays.asList(values).stream().map(it -> TypeInfo.of(it.getClass())).collect(Collectors.toList());
	}
}