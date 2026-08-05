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
package de.flapdoodle.commons.eval.example.evaluables.basic;

import de.flapdoodle.commons.eval.core.EvaluationContext;
import de.flapdoodle.commons.eval.core.VariableResolver;
import de.flapdoodle.commons.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.commons.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.parser.Token;
import de.flapdoodle.commons.eval.example.Value;

import java.util.function.Predicate;

public class Comparables extends TypedEvaluables.Wrapper {

    private Comparables(TypedEvaluables delegate) {
        super(delegate);
    }

    private static class Compare<T extends Comparable<T>> implements TypedEvaluable.Arg2<T, T, Value.BooleanValue> {

        private final Predicate<Integer> check;

        public Compare(Predicate<Integer> check) {
            this.check = check;
        }

        @Override
        public Value.BooleanValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, T first, T second) throws EvaluationException {
            return Value.of(check.test(first.compareTo(second)));
        }
    }

    public static class Greater<T extends Comparable<T>> extends Compare<T> {

        public Greater() {
            super(it -> it > 0);
        }
    }

    public static class GreaterOrEqual<T extends Comparable<T>> extends Compare<T> {

        public GreaterOrEqual() {
            super(it -> it >= 0);
        }
    }

    public static class Less<T extends Comparable<T>> extends Compare<T> {

        public Less() {
            super(it -> it < 0);
        }
    }

    public static class LessOrEqual<T extends Comparable<T>> extends Compare<T> {

        public LessOrEqual() {
            super(it -> it <= 0);
        }
    }

    public static Comparables less() {
        return new Comparables(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.NumberValue.class, Value.NumberValue.class, new Less<>()))
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.StringValue.class, Value.StringValue.class, new Less<>()))
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.DateTimeValue.class, Value.DateTimeValue.class, new Less<>()))
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.DurationValue.class, Value.DurationValue.class, new Less<>()))
                .build());
    }

    public static Comparables lessOrEqual() {
        return new Comparables(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.NumberValue.class, Value.NumberValue.class, new LessOrEqual<>()))
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.StringValue.class, Value.StringValue.class, new LessOrEqual<>()))
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.DateTimeValue.class, Value.DateTimeValue.class, new LessOrEqual<>()))
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.DurationValue.class, Value.DurationValue.class, new LessOrEqual<>()))
                .build());
    }

    public static Comparables greater() {
        return new Comparables(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.NumberValue.class, Value.NumberValue.class, new Greater<>()))
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.StringValue.class, Value.StringValue.class, new Greater<>()))
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.DateTimeValue.class, Value.DateTimeValue.class, new Greater<>()))
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.DurationValue.class, Value.DurationValue.class, new Greater<>()))
                .build());
    }

    public static Comparables greaterOrEqual() {
        return new Comparables(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.NumberValue.class, Value.NumberValue.class, new GreaterOrEqual<>()))
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.StringValue.class, Value.StringValue.class, new GreaterOrEqual<>()))
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.DateTimeValue.class, Value.DateTimeValue.class, new GreaterOrEqual<>()))
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.DurationValue.class, Value.DurationValue.class, new GreaterOrEqual<>()))
                .build());
    }
}
