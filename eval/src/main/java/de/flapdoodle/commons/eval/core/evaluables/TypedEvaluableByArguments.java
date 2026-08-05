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
package de.flapdoodle.commons.eval.core.evaluables;

import de.flapdoodle.commons.eval.core.exceptions.EvaluableException;
import de.flapdoodle.commons.reflection.TypeInfo;
import de.flapdoodle.commons.types.Either;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface TypedEvaluableByArguments {
    Either<TypedEvaluable<?>, EvaluableException> find(List<? extends Evaluated<?>> values);
    Either<TypedEvaluable<?>, EvaluableException> findType(List<? extends TypeInfo<?>> valueTypes);

    static Either<TypedEvaluable<?>, EvaluableException> find(List<TypedEvaluable<?>> list, List<? extends Evaluated<?>> values) {
        List<EvaluableException> errors = new ArrayList<>();
        for (TypedEvaluable<?> evaluable : list) {
            Optional<EvaluableException> error = evaluable.signature().validateArguments(values);
            if (error.isPresent()) errors.add(error.get());
            else return Either.left(evaluable);
        }

        EvaluableException exception;
        if (errors.size()==1 && errors.get(0).isValidationError()) {
            exception = errors.get(0);
        } else {
            String valuesAsString = values.stream().map(it -> it.toString() + "(" + it.getClass() + ")").collect(Collectors.joining(", "));
            String signatures = list.stream().map(it -> it.signature().asHumanReadable()).collect(Collectors.joining("\n", "\n", "n"));
            exception = EvaluableException.of("no matching signature found for %s in %s", valuesAsString, signatures);
        }

        return Either.right(exception);
    }

    static Either<TypedEvaluable<?>, EvaluableException> findType(List<TypedEvaluable<?>> list, List<? extends TypeInfo<?>> valueTypes) {
        List<EvaluableException> errors = new ArrayList<>();
        for (TypedEvaluable<?> evaluable : list) {
            Optional<EvaluableException> error = evaluable.signature().validateArgumentTypes(valueTypes);
            if (error.isPresent()) errors.add(error.get());
            else return Either.left(evaluable);
        }

        EvaluableException exception;
        if (errors.size()==1 && errors.get(0).isValidationError()) {
            exception = errors.get(0);
        } else {
            String valuesAsString = valueTypes.stream().map(Object::toString).collect(Collectors.joining(", "));
            String signatures = list.stream().map(it -> it.signature().asHumanReadable()).collect(Collectors.joining("\n", "\n", "n"));
            exception = EvaluableException.of("no matching signature found for %s in %s", valuesAsString, signatures);
        }

        return Either.right(exception);
    }

    static EvaluableException signatureNotFound(List<TypedEvaluable<?>> list, List<?> values, List<EvaluableException> errors) {
        if (errors.size()==1 && errors.get(0).isValidationError()) {
            return errors.get(0);
        }

        String valuesAsString = values.stream().map(it -> it.toString()+"("+it.getClass()+")").collect(Collectors.joining(", "));
        String signatures = list.stream().map(it -> it.signature().asHumanReadable()).collect(Collectors.joining("\n","\n","n"));
        return EvaluableException.of("no matching signature found for %s in %s", valuesAsString, signatures);
    }
}
