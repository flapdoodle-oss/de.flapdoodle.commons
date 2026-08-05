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

public class Factorial extends TypedEvaluables.Wrapper {

    public static class Number implements TypedEvaluable.Arg1<Value.NumberValue, Value.NumberValue> {

        @Override
        public Value.NumberValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue argument) throws EvaluationException {
            int number = argument.wrapped().intValue();
            BigDecimal factorial = BigDecimal.ONE;
            for (int i = 1; i <= number; i++) {
                factorial =
                        factorial.multiply(new BigDecimal(i, evaluationContext.mathContext()), evaluationContext.mathContext());
            }
            return Value.of(factorial);
        }
    }

    public Factorial() {
        super(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.NumberValue.class, Value.NumberValue.class, new Number()))
                .build());
    }
}
