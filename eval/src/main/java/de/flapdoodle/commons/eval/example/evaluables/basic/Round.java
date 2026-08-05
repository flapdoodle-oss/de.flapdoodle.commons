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

import java.math.RoundingMode;

public class Round  extends TypedEvaluables.Wrapper {

    @Deprecated
    public static class Ceiling implements TypedEvaluable.Arg1<Value.NumberValue, Value.NumberValue> {

        @Override
        public Value.NumberValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first) throws EvaluationException {
            return Value.of(first.wrapped().setScale(0, RoundingMode.CEILING));
        }
    }

    @Deprecated
    public static class Floor implements TypedEvaluable.Arg1<Value.NumberValue, Value.NumberValue> {

        @Override
        public Value.NumberValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first) throws EvaluationException {
            return Value.of(first.wrapped().setScale(0, RoundingMode.FLOOR));
        }
    }

    public static class Number implements TypedEvaluable.Arg2<Value.NumberValue, Value.NumberValue, Value.NumberValue> {

        @Override
        public Value.NumberValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first, Value.NumberValue second) throws EvaluationException {
            return Value.of(
                    first
                            .wrapped()
                            .setScale(
                                    second.wrapped().intValue(),
                                    evaluationContext.mathContext().getRoundingMode()));

        }
    }

    public static class NumberMode implements TypedEvaluable.Arg2<Value.NumberValue, Value.StringValue, Value.NumberValue> {

        @Override
        public Value.NumberValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first, Value.StringValue second) throws EvaluationException {
            return Value.of(first.wrapped().setScale(0, roundingMode(token, second)));
        }
    }

    public static class NumberScaleMode implements TypedEvaluable.Arg3<Value.NumberValue, Value.NumberValue, Value.StringValue, Value.NumberValue> {

        @Override
        public Value.NumberValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first, Value.NumberValue second, Value.StringValue third) throws EvaluationException {
            return Value.of(
                    first
                            .wrapped()
                            .setScale(
                                    second.wrapped().intValue(),
                                    roundingMode(token, third)));

        }
    }

    private static RoundingMode roundingMode(Token token, Value.StringValue second) throws EvaluationException {
        RoundingMode mode=null;
        switch (second.wrapped().toLowerCase()) {
            case "ceiling":
                mode=RoundingMode.CEILING;
                break;
            case "down":
                mode=RoundingMode.DOWN;
                break;
            case "up":
                mode=RoundingMode.UP;
                break;
            case "half-down":
                mode=RoundingMode.HALF_DOWN;
                break;
            case "half-up":
                mode=RoundingMode.HALF_UP;
                break;
            case "half-even":
                mode=RoundingMode.HALF_EVEN;
                break;
            case "floor":
                mode=RoundingMode.FLOOR;
                break;
        }

        if (mode==null) {
            throw new EvaluationException(token,"unsupported rounding mode: "+ second.wrapped());
        }
        return mode;
    }

    protected Round(TypedEvaluables config) {
        super(config);
    }

    public Round() {
        this(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.NumberValue.class, Value.NumberValue.class, Value.NumberValue.class, new Number()))
                .addList(TypedEvaluable.of(Value.NumberValue.class, Value.NumberValue.class, Value.StringValue.class, new NumberMode()))
                .addList(TypedEvaluable.of(Value.NumberValue.class, Value.NumberValue.class, Value.NumberValue.class, Value.StringValue.class, new NumberScaleMode()))
                .build());
    }

    @Deprecated
    public static Round ceiling() {
        return new Round(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.NumberValue.class, Value.NumberValue.class, new Ceiling()))
                .build());
    }

    @Deprecated
    public static Round floor() {
        return new Round(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.NumberValue.class, Value.NumberValue.class, new Floor()))
                .build());
    }
}
