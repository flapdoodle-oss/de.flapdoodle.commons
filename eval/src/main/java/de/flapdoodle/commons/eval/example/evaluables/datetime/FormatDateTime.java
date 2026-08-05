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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormatDateTime extends TypedEvaluables.Wrapper {

    public static class ToString implements TypedEvaluable.Arg1<Value.DateTimeValue, Value.StringValue> {

        @Override
        public Value.StringValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.DateTimeValue dateTime) throws EvaluationException {
            LocalDateTime dateTimeValue = dateTime.wrapped().atZone(evaluationContext.zoneId()).toLocalDateTime();
            return Value.of(dateTimeValue.toString());
        }
    }

    public static class FormatString implements TypedEvaluable.Arg2<Value.DateTimeValue, Value.StringValue, Value.StringValue> {

        @Override
        public Value.StringValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.DateTimeValue dateTime, Value.StringValue format) throws EvaluationException {
            LocalDateTime dateTimeValue = dateTime.wrapped().atZone(evaluationContext.zoneId()).toLocalDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format.wrapped());
            return Value.of(dateTimeValue.format(formatter));
        }
    }

    public FormatDateTime() {
        super(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.StringValue.class, Value.DateTimeValue.class, new ToString()))
                .addList(TypedEvaluable.of(Value.StringValue.class, Value.DateTimeValue.class, Value.StringValue.class, new FormatString()))
                .build());
    }
}
