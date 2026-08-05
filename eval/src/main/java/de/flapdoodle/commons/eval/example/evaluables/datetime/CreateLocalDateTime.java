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
import de.flapdoodle.commons.eval.core.evaluables.Parameter;
import de.flapdoodle.commons.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.commons.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.parser.Token;
import de.flapdoodle.commons.eval.core.validation.ParameterValidator;
import de.flapdoodle.commons.eval.example.Value;
import de.flapdoodle.commons.eval.example.evaluables.validation.NumberValidator;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class CreateLocalDateTime extends TypedEvaluables.Wrapper {

    public static class With3Args implements TypedEvaluable.Arg3<Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.DateTimeValue> {
        @Override
        public Value.DateTimeValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first, Value.NumberValue second, Value.NumberValue third) throws EvaluationException {
            return dateTimeValue(evaluationContext, first.wrapped().intValue(), second.wrapped().intValue(), third.wrapped().intValue(), 0, 0, 0, 0);
        }
    }

    public static class With4Args implements TypedEvaluable.Arg4<Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.DateTimeValue> {
        @Override
        public Value.DateTimeValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue year, Value.NumberValue month, Value.NumberValue day, Value.NumberValue hour) throws EvaluationException {
            return dateTimeValue(evaluationContext, year.wrapped().intValue(), month.wrapped().intValue(), day.wrapped().intValue(), hour.wrapped().intValue(), 0, 0, 0);
        }
    }

    public static class With5Args implements TypedEvaluable.Arg5<Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.DateTimeValue> {
        @Override
        public Value.DateTimeValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue year, Value.NumberValue month, Value.NumberValue day, Value.NumberValue hour, Value.NumberValue minute) throws EvaluationException {
            return dateTimeValue(evaluationContext, year.wrapped().intValue(), month.wrapped().intValue(), day.wrapped().intValue(), hour.wrapped().intValue(), minute.wrapped().intValue(), 0, 0);
        }
    }

    public static class With6Args implements TypedEvaluable.Arg6<Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.DateTimeValue> {
        @Override
        public Value.DateTimeValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue year, Value.NumberValue month, Value.NumberValue day, Value.NumberValue hour, Value.NumberValue minute, Value.NumberValue second) throws EvaluationException {
            return dateTimeValue(evaluationContext, year.wrapped().intValue(), month.wrapped().intValue(), day.wrapped().intValue(), hour.wrapped().intValue(), minute.wrapped().intValue(), second.wrapped().intValue(), 0);
        }
    }

    public static class With7Args implements TypedEvaluable.Arg7<Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.DateTimeValue> {
        @Override
        public Value.DateTimeValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue year, Value.NumberValue month, Value.NumberValue day, Value.NumberValue hour, Value.NumberValue minute, Value.NumberValue second, Value.NumberValue nano) throws EvaluationException {
            return dateTimeValue(evaluationContext, year.wrapped().intValue(), month.wrapped().intValue(), day.wrapped().intValue(), hour.wrapped().intValue(), minute.wrapped().intValue(), second.wrapped().intValue(), nano.wrapped().intValue());
        }
    }

    private static Value.DateTimeValue dateTimeValue(EvaluationContext evaluationContext, int year, int month, int day, int hour, int minute, int second, int nanoOfs) {
        ZoneId zoneId = evaluationContext.zoneId();
        return Value.of(
                LocalDateTime.of(year, month, day, hour, minute, second, nanoOfs)
                        .atZone(zoneId)
                        .toInstant());
    }


    public CreateLocalDateTime() {
        super(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.DateTimeValue.class,
                        Parameter.of(Value.NumberValue.class).withValidators(year()), Parameter.of(Value.NumberValue.class).withValidators(month()), Parameter.of(Value.NumberValue.class).withValidators(dayOfMonth()),
                        new With3Args()))
                .addList(TypedEvaluable.of(Value.DateTimeValue.class,
                        Parameter.of(Value.NumberValue.class).withValidators(year()), Parameter.of(Value.NumberValue.class).withValidators(month()), Parameter.of(Value.NumberValue.class).withValidators(dayOfMonth()),
                        Parameter.of(Value.NumberValue.class).withValidators(hourOfDay()),
                        new With4Args()))
                .addList(TypedEvaluable.of(Value.DateTimeValue.class,
                        Parameter.of(Value.NumberValue.class).withValidators(year()), Parameter.of(Value.NumberValue.class).withValidators(month()), Parameter.of(Value.NumberValue.class).withValidators(dayOfMonth()),
                        Parameter.of(Value.NumberValue.class).withValidators(hourOfDay()), Parameter.of(Value.NumberValue.class).withValidators(minutes()),
                        new With5Args()))
                .addList(TypedEvaluable.of(Value.DateTimeValue.class,
                        Parameter.of(Value.NumberValue.class).withValidators(year()), Parameter.of(Value.NumberValue.class).withValidators(month()), Parameter.of(Value.NumberValue.class).withValidators(dayOfMonth()),
                        Parameter.of(Value.NumberValue.class).withValidators(hourOfDay()), Parameter.of(Value.NumberValue.class).withValidators(minutes()), Parameter.of(Value.NumberValue.class).withValidators(seconds()),
                        new With6Args()))
                .addList(TypedEvaluable.of(Value.DateTimeValue.class,
                        Parameter.of(Value.NumberValue.class).withValidators(year()), Parameter.of(Value.NumberValue.class).withValidators(month()), Parameter.of(Value.NumberValue.class).withValidators(dayOfMonth()),
                        Parameter.of(Value.NumberValue.class).withValidators(hourOfDay()), Parameter.of(Value.NumberValue.class).withValidators(minutes()), Parameter.of(Value.NumberValue.class).withValidators(seconds()),
                        Parameter.of(Value.NumberValue.class).withValidators(nano()),
                        new With7Args()))
                .build());
    }

    private static ParameterValidator<Value.NumberValue> year() {
        return NumberValidator.between(0, 9999);
    }

    private static ParameterValidator<Value.NumberValue> month() {
        return NumberValidator.between(1, 12);
    }

    private static ParameterValidator<Value.NumberValue> dayOfMonth() {
        return NumberValidator.between(1, 31);
    }

    private static ParameterValidator<Value.NumberValue> hourOfDay() {
        return NumberValidator.between(0, 23);
    }

    private static ParameterValidator<Value.NumberValue> minutes() {
        return NumberValidator.between(0, 59);
    }

    private static ParameterValidator<Value.NumberValue> seconds() {
        return NumberValidator.between(0, 59);
    }

    private static ParameterValidator<Value.NumberValue> nano() {
        return NumberValidator.between(0, 999999999L);
    }
}

