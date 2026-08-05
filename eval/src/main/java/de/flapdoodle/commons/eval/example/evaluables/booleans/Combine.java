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
package de.flapdoodle.commons.eval.example.evaluables.booleans;

import de.flapdoodle.commons.eval.core.EvaluationContext;
import de.flapdoodle.commons.eval.core.VariableResolver;
import de.flapdoodle.commons.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.commons.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.parser.Token;
import de.flapdoodle.commons.eval.example.Value;

public class Combine extends TypedEvaluables.Wrapper {
    public static class And implements TypedEvaluable.Arg2<Value.BooleanValue, Value.BooleanValue, Value.BooleanValue> {

        @Override
        public Value.BooleanValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.BooleanValue first, Value.BooleanValue second) throws EvaluationException {
            return Value.of(first.wrapped() && second.wrapped());
        }
    }

    public static class Or implements TypedEvaluable.Arg2<Value.BooleanValue, Value.BooleanValue, Value.BooleanValue> {

        @Override
        public Value.BooleanValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.BooleanValue first, Value.BooleanValue second) throws EvaluationException {
            return Value.of(first.wrapped() || second.wrapped());
        }
    }

    private Combine(TypedEvaluables delegate) {
        super(delegate);
    }

    public static Combine and() {
        return new Combine(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.BooleanValue.class, Value.BooleanValue.class, new And()))
                .build());
    }

    public static Combine or() {
        return new Combine(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.BooleanValue.class, Value.BooleanValue.class, new Or()))
                .build());
    }
}
