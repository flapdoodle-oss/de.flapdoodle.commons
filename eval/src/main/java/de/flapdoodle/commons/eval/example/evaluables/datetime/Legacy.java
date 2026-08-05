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

import java.time.Instant;

public class Legacy extends TypedEvaluables.Wrapper {

    public static class EpochFromNumber implements TypedEvaluable.Arg1<Value.NumberValue, Value.DateTimeValue> {
        @Override
        public Value.DateTimeValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue argument) throws EvaluationException {
            return Value.of(Instant.ofEpochMilli(argument.wrapped().longValue()));
        }
    }

    public static class DateTime2Epoch implements TypedEvaluable.Arg1<Value.DateTimeValue, Value.NumberValue> {
        @Override
        public Value.NumberValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.DateTimeValue argument) throws EvaluationException {
            return Value.of(argument.wrapped().toEpochMilli());
        }
    }


    private Legacy(TypedEvaluables delegate) {
        super(delegate);
    }

    public static Legacy epochFromNumber() {
        return new Legacy(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.DateTimeValue.class, Value.NumberValue.class, new EpochFromNumber()))
                .build());
    }

    public static Legacy dateTime2Epoch() {
        return new Legacy(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.NumberValue.class, Value.DateTimeValue.class, new DateTime2Epoch()))
                .build());
    }

}
