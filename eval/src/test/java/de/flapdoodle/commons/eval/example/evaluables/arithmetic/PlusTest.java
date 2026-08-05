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
package de.flapdoodle.commons.eval.example.evaluables.arithmetic;

import de.flapdoodle.commons.eval.core.EvaluationContext;
import de.flapdoodle.commons.eval.core.VariableResolver;
import de.flapdoodle.commons.eval.core.evaluables.Evaluated;
import de.flapdoodle.commons.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.commons.eval.core.evaluables.TypedEvaluableByArguments;
import de.flapdoodle.commons.eval.core.exceptions.EvaluableException;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.parser.Token;
import de.flapdoodle.commons.eval.core.parser.TokenType;
import de.flapdoodle.commons.eval.example.Value;
import de.flapdoodle.commons.types.Either;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.MathContext;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static de.flapdoodle.commons.eval.example.AssertEither.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

class PlusTest {
	private Plus testee = new Plus();

	private EvaluationContext evaluationContext = EvaluationContext.builder()
		.zoneId(ZoneId.systemDefault())
		.mathContext(MathContext.UNLIMITED)
		.build();

	private Token token = Token.of(0,"<foo>", TokenType.VARIABLE_OR_CONSTANT);

	private static Stream<Arguments> validSamples() {
		ZonedDateTime now= ZonedDateTime.now();

		return Stream.of(
			Arguments.of(Value.of(1.2), Value.of(3.8), Value.of(5.0)),
			Arguments.of(Value.of(now.toInstant()), Value.of(Duration.ofDays(1)), Value.of(now.plus(Duration.ofDays(1)).toInstant())),
			Arguments.of(Value.of(Duration.ofDays(2)), Value.of(Duration.ofDays(2)), Value.of(Duration.ofDays(4))),
			Arguments.of(Value.of(now.toInstant()), Value.of(1000), Value.of(now.plusSeconds(1).toInstant())),
			Arguments.of(Value.of(1.2), Value.of(false), Value.of("1.2false"))
		);
	}

	@ParameterizedTest
	@MethodSource("validSamples")
	void add(Value<?> first, Value<?> second, Value<?> expected) throws EvaluationException {
		List<? extends Evaluated<Value<? extends Object>>> values = Evaluated.asList(first, second);

		Optional<? extends TypedEvaluableByArguments> byNumberOfArguments = testee.filterByNumberOfArguments(values.size());
		assertThat(byNumberOfArguments).isPresent();

		Either<TypedEvaluable<?>, EvaluableException> byArguments = byNumberOfArguments.get().find(values);
		assertThat(byArguments).isLeft();

		Evaluated<?> result = byArguments.left()
			.evaluate(VariableResolver.empty(), evaluationContext, token, values);

		assertThat(result.wrapped()).isEqualTo(expected);
	}

	@Test
	void prefixPlus() throws EvaluationException {
		List<? extends Evaluated<Value.NumberValue>> values = Evaluated.asList(Value.of(-123.0));

		Optional<? extends TypedEvaluableByArguments> byNumberOfArguments = testee.filterByNumberOfArguments(values.size());
		assertThat(byNumberOfArguments).isPresent();

		Either<TypedEvaluable<?>, EvaluableException> byArguments = byNumberOfArguments.get().find(values);
		assertThat(byArguments).isLeft();

		Evaluated<?> result = byArguments.left()
			.evaluate(VariableResolver.empty(), evaluationContext, token, values);

		assertThat(result.wrapped()).isEqualTo(Value.of(-123.0));
	}

	@Test
	void sum() throws EvaluationException {
		List<? extends Evaluated<Value.NumberValue>> values = Evaluated.asList(Value.of(1.0), Value.of(2.0), Value.of(3.0));

		Optional<? extends TypedEvaluableByArguments> byNumberOfArguments = testee.filterByNumberOfArguments(values.size());
		assertThat(byNumberOfArguments).isPresent();

		Either<TypedEvaluable<?>, EvaluableException> byArguments = byNumberOfArguments.get().find(values);
		assertThat(byArguments).isLeft();

		Evaluated<?> result = byArguments.left()
			.evaluate(VariableResolver.empty(), evaluationContext, token, values);

		assertThat(result.wrapped()).isEqualTo(Value.of(6.0));
	}
}