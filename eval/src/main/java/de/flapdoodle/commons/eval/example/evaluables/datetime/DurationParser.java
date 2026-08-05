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
package de.flapdoodle.commons.eval.example.evaluables.datetime;

import de.flapdoodle.commons.eval.core.EvaluationContext;
import de.flapdoodle.commons.eval.core.VariableResolver;
import de.flapdoodle.commons.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.commons.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.parser.Token;
import de.flapdoodle.commons.eval.example.Value;

import java.time.Duration;

public class DurationParser extends TypedEvaluables.Wrapper {

    public static class Strings implements TypedEvaluable.Arg1<Value.StringValue, Value.DurationValue> {
        @Override
        public Value.DurationValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.StringValue argument) throws EvaluationException {
            return Value.of(Duration.parse(argument.wrapped()));
        }
    }

    public static class OfDays implements TypedEvaluable.Arg1<Value.NumberValue, Value.DurationValue> {
        @Override
        public Value.DurationValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue argument) throws EvaluationException {
            return Value.of(Duration.ofDays(argument.wrapped().longValue()));
        }
    }

    public static class OfMillis implements TypedEvaluable.Arg1<Value.NumberValue, Value.DurationValue> {
        @Override
        public Value.DurationValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue argument) throws EvaluationException {
            return Value.of(Duration.ofMillis(argument.wrapped().longValue()));
        }
    }

    private DurationParser(TypedEvaluables evaluatables) {
        super(evaluatables);
    }

    public DurationParser() {
        this(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.DurationValue.class, Value.StringValue.class, new Strings()))
                .build());
    }

    @Deprecated
    public static DurationParser parseDuration() {
        return new DurationParser(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.DurationValue.class, Value.StringValue.class, new Strings()))
                .build());
    }
    @Deprecated
    public static DurationParser ofDays() {
        return new DurationParser(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.DurationValue.class, Value.NumberValue.class, new OfDays()))
                .build());
    }
    @Deprecated
    public static DurationParser ofMillis() {
        return new DurationParser(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.DurationValue.class, Value.NumberValue.class, new OfMillis()))
                .build());
    }
}
