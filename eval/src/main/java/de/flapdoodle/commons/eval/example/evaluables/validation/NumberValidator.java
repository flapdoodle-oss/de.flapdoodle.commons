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
package de.flapdoodle.commons.eval.example.evaluables.validation;

import de.flapdoodle.commons.eval.core.exceptions.EvaluableException;
import de.flapdoodle.commons.eval.core.validation.ParameterValidator;
import de.flapdoodle.commons.eval.example.Value;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Predicate;

public class NumberValidator implements ParameterValidator<Value.NumberValue> {

    private final Predicate<BigDecimal> test;
    private final String errorMessageTemplate;

    private NumberValidator(Predicate<BigDecimal> test, String errorMessageTemplate) {
        this.test = test;
        this.errorMessageTemplate = errorMessageTemplate;
    }

    @Override
    public Optional<EvaluableException> validate(Value.NumberValue parameterValue) {
        if (!test.test(parameterValue.wrapped())) {
            return Optional.of(EvaluableException.validationError(errorMessageTemplate.replace("%s", parameterValue.wrapped().toString())));
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return test.toString();
    }

    public static NumberValidator isNot(BigDecimal value, String errorMessageTemplate) {
        return new NumberValidator(withDescription(v -> v.compareTo(value) != 0,"value != "+value), errorMessageTemplate);
    }

    public static NumberValidator isNot(BigDecimal value) {
        return isNot(value, "value of "+value+" is invalid");
    }

    public static NumberValidator greaterOrEqualThan(BigDecimal value, String errorMessageTemplate) {
        return new NumberValidator(withDescription(v -> v.compareTo(value) >= 0,"value >= "+value), errorMessageTemplate);
    }

    public static NumberValidator greaterOrEqualThan(BigDecimal value) {
        return greaterOrEqualThan(value, "value is not >= "+value+": %s");
    }

    public static NumberValidator greaterThan(BigDecimal value, String errorMessageTemplate) {
        return new NumberValidator(withDescription(v -> v.compareTo(value) > 0, "value > " + value), errorMessageTemplate);
    }

    public static NumberValidator greaterThan(BigDecimal value) {
        return greaterThan(value, "value is not > " + value + ": %s");
    }

    public static NumberValidator smallerOrEqualThan(BigDecimal value, String errorMessageTemplate) {
        return new NumberValidator(withDescription(v -> v.compareTo(value) <= 0,"value <= "+value), errorMessageTemplate);
    }

    public static NumberValidator smallerOrEqualThan(BigDecimal value) {
        return smallerOrEqualThan(value, "value is not <= "+value+": %s");
    }

    public static NumberValidator smallerThan(BigDecimal value, String errorMessageTemplate) {
        return new NumberValidator(withDescription(v -> v.compareTo(value) < 0,"value < "+value), errorMessageTemplate);
    }

    public static NumberValidator smallerThan(BigDecimal value) {
        return smallerThan(value, "value is not < "+value+": %s");
    }

    public static NumberValidator between(long min, long max) {
        return new NumberValidator(withDescription(v -> {
            return v.longValue() >= min && v.longValue() <= max;
        }, min+" <= value <= "+max), "value is not between "+min+" and "+max+": %s");
    }

    private static <T> Predicate<T> withDescription(Predicate<T> wrapped, String toString) {
        return new Predicate<T>() {
            @Override
            public boolean test(T v) {
                return wrapped.test(v);
            }

            @Override
            public String toString() {
                return toString;
            }
        };
    }
}
