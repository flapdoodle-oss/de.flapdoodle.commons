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
package de.flapdoodle.commons.eval.example.evaluables.arithmetic;

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

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Trigonometric extends TypedEvaluables.Wrapper {

    private Trigonometric(TypedEvaluables delegate) {
        super(delegate);
    }

    protected abstract static class AbstractTransformation implements TypedEvaluable.Arg1<Value.NumberValue, Value.NumberValue> {
        private final Function<Double, Double> transformation;

        public AbstractTransformation(Function<Double, Double> transformation) {
            this.transformation = transformation;
        }

        @Override
        public final Value.NumberValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue argument) throws EvaluationException {
            return Value.of(transformation.apply(argument.wrapped().doubleValue()));
        }
    }

    protected abstract static class AbstractMerge implements TypedEvaluable.Arg2<Value.NumberValue, Value.NumberValue, Value.NumberValue> {
        private final BiFunction<Double, Double, Double> merge;

        public AbstractMerge(BiFunction<Double, Double, Double> merge) {
            this.merge = merge;
        }

        @Override
        public final Value.NumberValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first, Value.NumberValue second) throws EvaluationException {
            return Value.of(merge.apply(first.wrapped().doubleValue(), second.wrapped().doubleValue()));
        }
    }

    @SafeVarargs
    private static Trigonometric of(AbstractTransformation transformation, ParameterValidator<Value.NumberValue>... validators) {
        return new Trigonometric(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.NumberValue.class, Parameter.of(Value.NumberValue.class)
                        .withValidators(validators), transformation))
                .build());
    }

    private static Trigonometric of(AbstractMerge transformation) {
        return new Trigonometric(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.NumberValue.class, Value.NumberValue.class, Value.NumberValue.class, transformation))
                .build());
    }



    public static class Acos extends AbstractTransformation {
        public Acos() {
            super(v -> Math.toDegrees(Math.acos(v)));
        }
    }

    public static class AcosR extends AbstractTransformation {
        public AcosR() {
            super(Math::acos);
        }
    }

    public static class AcosH extends AbstractTransformation {
        /* Formula: acosh(x) = ln(x + sqrt(x^2 - 1)) */
        public AcosH() {
            super(v -> Math.log(v + (Math.sqrt(Math.pow(v, 2) - 1))));
        }
    }

    public static Trigonometric acos() {
        return of(new Acos());
    }

    public static Trigonometric acosH() {
        return of(new AcosH(), NumberValidator.greaterOrEqualThan(BigDecimal.ONE));
    }

    public static Trigonometric acosR() {
        return of(new AcosR());
    }



    public static class Acot extends AbstractTransformation {
        /* Formula: acot(x) = (pi / 2) - atan(x) */
        public Acot() {
            super(v -> Math.toDegrees((Math.PI / 2) - Math.atan(v)));
        }
    }

    public static class AcotH extends AbstractTransformation {
        /* Formula: acoth(x) = log((x + 1) / (x - 1)) * 0.5 */
        public AcotH() {
            super(v -> Math.log((v + 1) / (v - 1)) * 0.5);
        }
    }

    public static class AcotR extends AbstractTransformation {
        /* Formula: acot(x) = (pi / 2) - atan(x) */
        public AcotR() {
            super(v -> (Math.PI / 2) - Math.atan(v));
        }
    }

    public static Trigonometric acot() {
        return of(new Acot(), NumberValidator.isNot(BigDecimal.ZERO));
    }

    public static Trigonometric acotH() {
        return of(new AcotH());
    }

    public static Trigonometric acotR() {
        return of(new AcotR(), NumberValidator.isNot(BigDecimal.ZERO));
    }



    public static class Asin extends AbstractTransformation {
        public Asin() {
            super(v -> Math.toDegrees(Math.asin(v)));
        }
    }

    public static class AsinH extends AbstractTransformation {
        /* Formula: asinh(x) = ln(x + sqrt(x^2 + 1)) */
        public AsinH() {
            super(v -> Math.log(v + (Math.sqrt(Math.pow(v, 2) + 1))));
        }
    }

    public static class AsinR extends AbstractTransformation {
        public AsinR() {
            super(Math::asin);
        }
    }

    public static Trigonometric asin() {
        return of(new Asin(),
                NumberValidator.greaterOrEqualThan(BigDecimal.valueOf(-1L),"Illegal x < -1 for asin(x): x = %s"),
                NumberValidator.smallerOrEqualThan(BigDecimal.ONE,"Illegal x > 1 for asin(x): x = %s"));
    }

    public static Trigonometric asinH() {
        return of(new AsinH());
    }

    public static Trigonometric asinR() {
        return of(new AsinR(),
                NumberValidator.greaterOrEqualThan(BigDecimal.valueOf(-1L),"Illegal x < -1 for asinR(x): x = %s"),
                NumberValidator.smallerOrEqualThan(BigDecimal.ONE,"Illegal x > 1 for asinR(x): x = %s"));
    }



    public static class Atan extends AbstractTransformation {
        public Atan() {
            super(v -> Math.toDegrees(Math.atan(v)));
        }
    }

    public static class AtanH extends AbstractTransformation {
        /* Formula: atanh(x) = 0.5*ln((1 + x)/(1 - x)) */
        public AtanH() {
            super(v -> 0.5 * Math.log((1 + v) / (1 - v)));
        }
    }

    public static class AtanR extends AbstractTransformation {
        public AtanR() {
            super(Math::atan);
        }
    }

    public static Trigonometric atan() {
        return of(new Atan());
    }

    public static Trigonometric atanH() {
        return of(new AtanH(),
                NumberValidator.greaterThan(BigDecimal.valueOf(-1L),"Illegal x < -1 for atanH(x): x = %s"),
                NumberValidator.smallerThan(BigDecimal.ONE,"Illegal x > 1 for atanH(x): x = %s"));
    }

    public static Trigonometric atanR() {
        return of(new AtanR());
    }



    public static class Atan2 extends AbstractMerge {
        public Atan2() {
            super((a,b) -> Math.toDegrees(Math.atan2(a, b)));
        }
    }

    public static class Atan2R extends AbstractMerge {
        public Atan2R() {
            super(Math::atan2);
        }
    }

    public static Trigonometric atan2() {
        return of(new Atan2());
    }

    public static Trigonometric atan2R() {
        return of(new Atan2R());
    }



    public static class Cos extends AbstractTransformation {
        public Cos() {
            super(v -> Math.cos(Math.toRadians(v)));
        }
    }

    public static class CosH extends AbstractTransformation {
        public CosH() {
            super(Math::cosh);
        }
    }

    public static class CosR extends AbstractTransformation {
        public CosR() {
            super(Math::cos);
        }
    }

    public static Trigonometric cos() {
        return of(new Cos());
    }

    public static Trigonometric cosH() {
        return of(new CosH());
    }

    public static Trigonometric cosR() {
        return of(new CosR());
    }



    public static class Cot extends AbstractTransformation {
        /* Formula: cot(x) = cos(x) / sin(x) = 1 / tan(x) */
        public Cot() {
            super(v -> 1.0 / Math.tan(Math.toRadians(v)));
        }
    }

    public static class CotH extends AbstractTransformation {
        /* Formula: coth(x) = 1 / tanh(x) */
        public CotH() {
            super(v -> 1 / Math.tanh(v));
        }
    }

    public static class CotR extends AbstractTransformation {
        /* Formula: cot(x) = cos(x) / sin(x) = 1 / tan(x) */
        public CotR() {
            super(v -> 1.0 / Math.tan(v));
        }
    }

    public static Trigonometric cot() {
        return of(new Cot(), NumberValidator.isNot(BigDecimal.ZERO));
    }

    public static Trigonometric cotH() {
        return of(new CotH(), NumberValidator.isNot(BigDecimal.ZERO));
    }

    public static Trigonometric cotR() {
        return of(new CotR(), NumberValidator.isNot(BigDecimal.ZERO));
    }



    public static class Csc extends AbstractTransformation {
        /* Formula: csc(x) = 1 / sin(x) */
        public Csc() {
            super(v -> 1.0 / Math.sin(Math.toRadians(v)));
        }
    }

    public static class CscH extends AbstractTransformation {
        /* Formula: csch(x) = 1 / sinh(x) */
        public CscH() {
            super(v -> 1.0 / Math.sinh(v));
        }
    }

    public static class CscR extends AbstractTransformation {
        /* Formula: csc(x) = 1 / sin(x) */
        public CscR() {
            super(v -> 1.0 / Math.sin(v));
        }
    }

    public static Trigonometric csc() {
        return of(new Csc(), NumberValidator.isNot(BigDecimal.ZERO));
    }

    public static Trigonometric cscH() {
        return of(new CscH(), NumberValidator.isNot(BigDecimal.ZERO));
    }

    public static Trigonometric cscR() {
        return of(new CscR(), NumberValidator.isNot(BigDecimal.ZERO));
    }



    public static class Deg extends AbstractTransformation {
        public Deg() {
            super(Math::toDegrees);
        }
    }

    public static class Rad extends AbstractTransformation {
        public Rad() {
            super(Math::toRadians);
        }
    }

    public static Trigonometric deg() {
        return of(new Deg());
    }

    public static Trigonometric rad() {
        return of(new Rad());
    }



    public static class Sin extends AbstractTransformation {
        public Sin() {
            super(v -> Math.sin(Math.toRadians(v)));
        }
    }

    public static class SinH extends AbstractTransformation {
        public SinH() {
            super(Math::sinh);
        }
    }

    public static class SinR extends AbstractTransformation {
        public SinR() {
            super(Math::sin);
        }
    }

    public static Trigonometric sin() {
        return of(new Sin());
    }

    public static Trigonometric sinH() {
        return of(new SinH());
    }

    public static Trigonometric sinR() {
        return of(new SinR());
    }



    public static class Sec extends AbstractTransformation {
        /* Formula: sec(x) = 1 / cos(x) */
        public Sec() {
            super(v -> 1.0 / Math.cos(Math.toRadians(v)));
        }
    }

    public static class SecH extends AbstractTransformation {
        /* Formula: sech(x) = 1 / cosh(x) */
        public SecH() {
            super(v -> 1.0 / Math.cosh(v));
        }
    }

    public static class SecR extends AbstractTransformation {
        /* Formula: sec(x) = 1 / cos(x) */
        public SecR() {
            super(v -> 1.0 / Math.cos(v));
        }
    }

    public static Trigonometric sec() {
        return of(new Sec(), NumberValidator.isNot(BigDecimal.ZERO));
    }

    public static Trigonometric secH() {
        return of(new SecH(), NumberValidator.isNot(BigDecimal.ZERO));
    }

    public static Trigonometric secR() {
        return of(new SecR(), NumberValidator.isNot(BigDecimal.ZERO));
    }



    public static class Tan extends AbstractTransformation {
        public Tan() {
            super(v -> Math.tan(Math.toRadians(v)));
        }
    }

    public static class TanH extends AbstractTransformation {
        public TanH() {
            super(Math::tanh);
        }
    }

    public static class TanR extends AbstractTransformation {
        public TanR() {
            super(Math::tan);
        }
    }

    public static Trigonometric tan() {
        return of(new Tan());
    }

    public static Trigonometric tanH() {
        return of(new TanH());
    }

    public static Trigonometric tanR() {
        return of(new TanR());
    }
}
