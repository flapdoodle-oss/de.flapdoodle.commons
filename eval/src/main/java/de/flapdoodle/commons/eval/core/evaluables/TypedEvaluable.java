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

import de.flapdoodle.commons.eval.core.EvaluationContext;
import de.flapdoodle.commons.eval.core.VariableResolver;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.parser.Token;

import java.util.List;
import java.util.stream.Collectors;

public interface TypedEvaluable<T> {
    Signature<T> signature();
    Evaluated<T> evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, List<? extends Evaluated<?>> arguments) throws EvaluationException;

    interface Arg0<T> {
        T evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token) throws EvaluationException;
    }

    interface Arg1<S, T> {
        T evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, S argument) throws EvaluationException;
    }

    interface VarArg1<S, T> {
        T evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, List<S> arguments) throws EvaluationException;
    }

    interface Arg2<A, B, T> {
        T evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, A first, B second) throws EvaluationException;
    }

    interface VarArg2<A, B, T> {
        T evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, A first, List<B> last) throws EvaluationException;
    }

    interface Arg3<A, B, C, T> {
        T evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, A first, B second, C third) throws EvaluationException;
    }

    interface Arg4<A, B, C, D, T> {
        T evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, A first, B second, C third, D fourth) throws EvaluationException;
    }

    interface Arg5<A, B, C, D, E, T> {
        T evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, A first, B second, C third, D fourth, E fifth) throws EvaluationException;
    }

    interface Arg6<A, B, C, D, E, F, T> {
        T evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, A first, B second, C third, D fourth, E fifth, F sixth) throws EvaluationException;
    }

    interface Arg7<A, B, C, D, E, F, G, T> {
        T evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, A first, B second, C third, D fourth, E fifth, F sixth, G seventh) throws EvaluationException;
    }

    static <T> TypedEvaluable<T> of(Class<T> returnType, Arg0<T> function) {
        Evaluable<T> evaluable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token);
        return new TypedEvaluableAdapter<>(Signature.of(returnType), evaluable.named(function.toString()));
    }

    static <T, A> TypedEvaluable<T> of(Class<T> returnType, Parameter<A> a, Arg1<A, T> function) {
        Evaluable<T> evaluable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token,
                a.type().castIfNotNull(arguments.get(0)));
        return new TypedEvaluableAdapter<>(Signature.of(returnType, a), evaluable.named(function.toString()));
    }

    static <T, A> TypedEvaluable<T> of(Class<T> returnType, Class<A> a, Arg1<A, T> function) {
        return of(returnType, Parameter.of(a), function);
    }

    static <T, A> TypedEvaluable<T> ofVarArg(Class<T> returnType, Class<A> a, VarArg1<A, T> function) {
        Evaluable<T> evaluable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token,
                arguments.stream()
                        .map(a::cast).collect(Collectors.toList()));
        return new TypedEvaluableAdapter<>(Signature.ofVarArg(returnType, Parameter.of(a)), evaluable.named(function.toString()));
    }

    static <T, A, B> TypedEvaluable<T> of(Class<T> returnType, Parameter<A> a, Parameter<B> b,
                                                                                               Arg2<A, B, T> function) {
        Evaluable<T> evaluable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token,
                a.type().castIfNotNull(arguments.get(0)),
                b.type().castIfNotNull(arguments.get(1)));
        return new TypedEvaluableAdapter<>(Signature.of(returnType, a, b), evaluable.named(function.toString()));
    }

    static <T, A, B> TypedEvaluable<T> ofVarArg(Class<T> returnType, Class<A> a, Class<B> b, VarArg2<A, B, T> function) {
        Evaluable<T> evaluable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token,
          a.cast(arguments.get(0)),
          arguments.subList(1, arguments.size()).stream()
            .map(b::cast).collect(Collectors.toList()));
        return new TypedEvaluableAdapter<>(Signature.ofVarArg(returnType, Parameter.of(a), Parameter.of(b)), evaluable.named(function.toString()));
    }

    static <T, A, B> TypedEvaluable<T> of(Class<T> returnType, Class<A> a, Class<B> b,
                                                                                               Arg2<A, B, T> function) {
        return of(returnType, Parameter.of(a), Parameter.of(b), function);
    }

    static <T, A, B, C> TypedEvaluable<T> of(Class<T> returnType, Class<A> a, Class<B> b, Class<C> c,
                                                                                                                   Arg3<A, B, C, T> function) {
        return of(returnType, Parameter.of(a), Parameter.of(b), Parameter.of(c), function);
    }

    static <T, A, B, C> TypedEvaluable<T> of(Class<T> returnType, Parameter<A> a, Parameter<B> b, Parameter<C> c,
                                                                                                                   Arg3<A, B, C, T> function) {
        Evaluable<T> evaluable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token,
                a.type().castIfNotNull(arguments.get(0)),
                b.type().castIfNotNull(arguments.get(1)),
                c.type().castIfNotNull(arguments.get(2)));
        return new TypedEvaluableAdapter<>(Signature.of(returnType, a, b, c), evaluable.named(function.toString()));
    }

    static <T, A, B, C, D> TypedEvaluable<T> of(Class<T> returnType, Parameter<A> a, Parameter<B> b, Parameter<C> c, Parameter<D> d,
                                                                                                                                       Arg4<A, B, C, D, T> function) {
        Evaluable<T> evaluable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token,
                a.type().castIfNotNull(arguments.get(0)),
                b.type().castIfNotNull(arguments.get(1)),
                c.type().castIfNotNull(arguments.get(2)),
                d.type().castIfNotNull(arguments.get(3)));
        return new TypedEvaluableAdapter<>(Signature.of(returnType, a, b, c, d), evaluable.named(function.toString()));
    }

    static <T, A, B, C, D, E> TypedEvaluable<T> of(Class<T> returnType, Parameter<A> a, Parameter<B> b, Parameter<C> c, Parameter<D> d, Parameter<E> e,
                                                                                                                                       Arg5<A, B, C, D, E, T> function) {
        Evaluable<T> evaluable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token,
                a.type().castIfNotNull(arguments.get(0)),
                b.type().castIfNotNull(arguments.get(1)),
                c.type().castIfNotNull(arguments.get(2)),
                d.type().castIfNotNull(arguments.get(3)),
                e.type().castIfNotNull(arguments.get(4)));
        return new TypedEvaluableAdapter<>(Signature.of(returnType, a, b, c, d, e), evaluable.named(function.toString()));
    }

    static <T, A, B, C, D, E, F> TypedEvaluable<T> of(Class<T> returnType, Parameter<A> a, Parameter<B> b, Parameter<C> c, Parameter<D> d, Parameter<E> e, Parameter<F> f,
                                                                                                                                                           Arg6<A, B, C, D, E, F, T> function) {
        Evaluable<T> evaluable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token,
                a.type().castIfNotNull(arguments.get(0)),
                b.type().castIfNotNull(arguments.get(1)),
                c.type().castIfNotNull(arguments.get(2)),
                d.type().castIfNotNull(arguments.get(3)),
                e.type().castIfNotNull(arguments.get(4)),
                f.type().castIfNotNull(arguments.get(5)));
        return new TypedEvaluableAdapter<>(Signature.of(returnType, a, b, c, d, e, f), evaluable.named(function.toString()));
    }

    static <T, A, B, C, D, E, F, G> TypedEvaluable<T> of(Class<T> returnType, Parameter<A> a, Parameter<B> b, Parameter<C> c, Parameter<D> d, Parameter<E> e, Parameter<F> f, Parameter<G> g,
                                                                                                                                                                               Arg7<A, B, C, D, E, F, G, T> function) {
        Evaluable<T> evaluable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token,
                a.type().castIfNotNull(arguments.get(0)),
                b.type().castIfNotNull(arguments.get(1)),
                c.type().castIfNotNull(arguments.get(2)),
                d.type().castIfNotNull(arguments.get(3)),
                e.type().castIfNotNull(arguments.get(4)),
                f.type().castIfNotNull(arguments.get(5)),
                g.type().castIfNotNull(arguments.get(6)));
        return new TypedEvaluableAdapter<>(Signature.of(returnType, a, b, c, d, e, f, g), evaluable.named(function.toString()));
    }
}
