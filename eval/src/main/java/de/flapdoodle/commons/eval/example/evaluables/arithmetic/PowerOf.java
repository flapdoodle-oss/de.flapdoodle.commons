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
import de.flapdoodle.commons.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.commons.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.parser.Token;
import de.flapdoodle.commons.eval.example.Value;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class PowerOf extends TypedEvaluables.Wrapper {

	public static class Number implements TypedEvaluable.Arg2<Value.NumberValue, Value.NumberValue, Value.NumberValue> {

		@Override
		public Value.NumberValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first,
										  Value.NumberValue second) throws EvaluationException {

			/*-
			 * Thanks to Gene Marin:
			 * http://stackoverflow.com/questions/3579779/how-to-do-a-fractional-power-on-bigdecimal-in-java
			 */

			MathContext mathContext = evaluationContext.mathContext();
			BigDecimal v1 = first.wrapped();
			BigDecimal v2 = second.wrapped();

			int signOf2 = v2.signum();
			double dn1 = v1.doubleValue();
			v2 = v2.multiply(new BigDecimal(signOf2)); // n2 is now positive
			BigDecimal remainderOf2 = v2.remainder(BigDecimal.ONE);
			BigDecimal n2IntPart = v2.subtract(remainderOf2);
			BigDecimal intPow = v1.pow(n2IntPart.intValueExact(), mathContext);
			BigDecimal doublePow = BigDecimal.valueOf(Math.pow(dn1, remainderOf2.doubleValue()));

			BigDecimal result = intPow.multiply(doublePow, mathContext);
			if (signOf2 == -1) {
				result = BigDecimal.ONE.divide(result, mathContext.getPrecision(), RoundingMode.HALF_UP);
			}
			return Value.of(result);
		}
	}

	public PowerOf() {
		super(TypedEvaluables.builder()
			.addList(TypedEvaluable.of(Value.NumberValue.class, Value.NumberValue.class, Value.NumberValue.class, new Number()))
			.build());
	}
}
