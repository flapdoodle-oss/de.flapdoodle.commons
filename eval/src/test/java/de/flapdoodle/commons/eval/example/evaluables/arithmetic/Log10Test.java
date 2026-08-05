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

import java.math.MathContext;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static de.flapdoodle.commons.eval.example.AssertEither.assertThat;

class Log10Test {
	private Log10 testee = new Log10();

	private EvaluationContext evaluationContext = EvaluationContext.builder()
		.zoneId(ZoneId.systemDefault())
		.mathContext(MathContext.UNLIMITED)
		.build();

	private Token token = Token.of(0, "<foo>", TokenType.VARIABLE_OR_CONSTANT);

	@Test
	void sample() throws EvaluationException {
		List<? extends Evaluated<?>> values = Evaluated.wrap(Arrays.asList(Value.of(3.0)));

		Optional<? extends TypedEvaluableByArguments> byNumberOfArguments = testee.filterByNumberOfArguments(values.size());
		assertThat(byNumberOfArguments).isPresent();

		Either<TypedEvaluable<?>, EvaluableException> byArguments = byNumberOfArguments.get().find(values);
		assertThat(byArguments).isLeft();

		Evaluated<?> result = byArguments.left()
			.evaluate(VariableResolver.empty(), evaluationContext, token, values);

		assertThat(result.wrapped()).isEqualTo(Value.of(0.47712125471966244));
	}

	@Test
	void failOnNegative() {
		Optional<? extends TypedEvaluableByArguments> byNumberOfArguments = testee.filterByNumberOfArguments(1);
		assertThat(byNumberOfArguments).isPresent();

		assertThat(byNumberOfArguments.get().find(Evaluated.wrap(Arrays.asList(Value.of(-1.0)))))
			.isRight()
			.rightSatisfies(right -> assertThat(right).isInstanceOf(EvaluableException.class)
				.hasMessageContaining("value is not > 0: -1.0"));

		Either<TypedEvaluable<?>, EvaluableException> byArguments = byNumberOfArguments.get().find(Evaluated.wrap(Arrays.asList(Value.of(1.0))));
		assertThat(byArguments).isLeft();

		assertThatThrownBy(() -> byArguments.left()
			.evaluate(VariableResolver.empty(), evaluationContext, token, Evaluated.wrap(Arrays.asList(Value.of(-1.0)))))
			.isInstanceOf(EvaluationException.class)
			.hasMessage("value is not > 0: -1.0");
	}

	@Test
	void failOnZero() {
		Optional<? extends TypedEvaluableByArguments> byNumberOfArguments = testee.filterByNumberOfArguments(1);
		assertThat(byNumberOfArguments).isPresent();

		assertThat(byNumberOfArguments.get().find(Evaluated.wrap(Arrays.asList(Value.of(0.0)))))
			.isRight()
			.rightSatisfies(right -> assertThat(right).isInstanceOf(EvaluableException.class)
				.hasMessageContaining("value is not > 0: 0.0"));

		Either<TypedEvaluable<?>, EvaluableException> byArguments = byNumberOfArguments.get().find(Evaluated.wrap(Arrays.asList(Value.of(1.0))));
		assertThat(byArguments).isLeft();

		assertThatThrownBy(() -> byArguments.left()
			.evaluate(VariableResolver.empty(), evaluationContext, token, Evaluated.wrap(Arrays.asList(Value.of(0.0)))))
			.isInstanceOf(EvaluationException.class)
			.hasMessage("value is not > 0: 0.0");
	}
}