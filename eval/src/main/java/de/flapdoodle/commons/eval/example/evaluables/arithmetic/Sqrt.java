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
import de.flapdoodle.commons.eval.example.Value;
import de.flapdoodle.commons.eval.example.evaluables.validation.NumberValidator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

public class Sqrt extends TypedEvaluables.Wrapper {

	public static class Number implements TypedEvaluable.Arg1<Value.NumberValue, Value.NumberValue> {

		/**
		 * The value 0.1, with a scale of 1.
		 */
		private static final BigDecimal ONE_TENTH = BigDecimal.valueOf(1L, 1);

		/**
		 * The value 0.5, with a scale of 1.
		 */
		private static final BigDecimal ONE_HALF = BigDecimal.valueOf(5L, 1);

		@Override
		public Value.NumberValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue argument) throws EvaluationException {
			return Value.of(sqrt(argument.wrapped(), evaluationContext.mathContext()));
		}

		private static boolean isPowerOfTen(BigDecimal thi) {
			return BigInteger.ONE.equals(thi.unscaledValue());
		}

		// java9 BigDecimal.sqrt backport
		private static BigDecimal sqrt(BigDecimal thi, MathContext mc) {
			int signum = thi.signum();
			if (signum == 1) {
				/*
				 * The following code draws on the algorithm presented in
				 * "Properly Rounded Variable Precision Square Root," Hull and
				 * Abrham, ACM Transactions on Mathematical Software, Vol 11,
				 * No. 3, September 1985, Pages 229-237.
				 *
				 * The BigDecimal computational model differs from the one
				 * presented in the paper in several ways: first BigDecimal
				 * numbers aren't necessarily normalized, second many more
				 * rounding modes are supported, including UNNECESSARY, and
				 * exact results can be requested.
				 *
				 * The main steps of the algorithm below are as follows,
				 * first argument reduce the value to the numerical range
				 * [1, 10) using the following relations:
				 *
				 * x = y * 10 ^ exp
				 * sqrt(x) = sqrt(y) * 10^(exp / 2) if exp is even
				 * sqrt(x) = sqrt(y/10) * 10 ^((exp+1)/2) is exp is odd
				 *
				 * Then use Newton's iteration on the reduced value to compute
				 * the numerical digits of the desired result.
				 *
				 * Finally, scale back to the desired exponent range and
				 * perform any adjustment to get the preferred scale in the
				 * representation.
				 */

				// The code below favors relative simplicity over checking
				// for special cases that could run faster.

				int preferredScale = thi.scale() / 2;
				BigDecimal zeroWithFinalPreferredScale = BigDecimal.valueOf(0L, preferredScale);

				// First phase of numerical normalization, strip trailing
				// zeros and check for even powers of 10.
				BigDecimal stripped = thi.stripTrailingZeros();
				int strippedScale = stripped.scale();

				// Numerically sqrt(10^2N) = 10^N
				if (isPowerOfTen(stripped) &&
						strippedScale % 2 == 0) {
					BigDecimal result = BigDecimal.valueOf(1L, strippedScale / 2);
					if (result.scale() != preferredScale) {
						// Adjust to requested precision and preferred
						// scale as appropriate.
						result = result.add(zeroWithFinalPreferredScale, mc);
					}
					return result;
				}

				// After stripTrailingZeros, the representation is normalized as
				//
				// unscaledValue * 10^(-scale)
				//
				// where unscaledValue is an integer with the minimum
				// precision for the cohort of the numerical value. To
				// allow binary floating-point hardware to be used to get
				// approximately a 15 digit approximation to the square
				// root, it is helpful to instead normalize this so that
				// the significand portion is to right of the decimal
				// point by roughly (scale() - precision() + 1).

				// Now the precision / scale adjustment
				int scaleAdjust = 0;
				int scale = stripped.scale() - stripped.precision() + 1;
				if (scale % 2 == 0) {
					scaleAdjust = scale;
				} else {
					scaleAdjust = scale - 1;
				}

				BigDecimal working = stripped.scaleByPowerOfTen(scaleAdjust);

				assert  // Verify 0.1 <= working < 10
						ONE_TENTH.compareTo(working) <= 0 && working.compareTo(BigDecimal.TEN) < 0;

				// Use good ole' Math.sqrt to get the initial guess for
				// the Newton iteration, good to at least 15 decimal
				// digits. This approach does incur the cost of a
				//
				// BigDecimal -> double -> BigDecimal
				//
				// conversion cycle, but it avoids the need for several
				// Newton iterations in BigDecimal arithmetic to get the
				// working answer to 15 digits of precision. If many fewer
				// than 15 digits were needed, it might be faster to do
				// the loop entirely in BigDecimal arithmetic.
				//
				// (A double value might have as many as 17 decimal
				// digits of precision; it depends on the relative density
				// of binary and decimal numbers at different regions of
				// the number line.)
				//
				// (It would be possible to check for certain special
				// cases to avoid doing any Newton iterations. For
				// example, if the BigDecimal -> double conversion was
				// known to be exact and the rounding mode had a
				// low-enough precision, the post-Newton rounding logic
				// could be applied directly.)

				BigDecimal guess = new BigDecimal(Math.sqrt(working.doubleValue()));
				int guessPrecision = 15;
				int originalPrecision = mc.getPrecision();
				int targetPrecision;

				// If an exact value is requested, it must only need about
				// half of the input digits to represent since multiplying
				// an N digit number by itself yield a 2N-1 digit or 2N
				// digit result.
				if (originalPrecision == 0) {
					targetPrecision = stripped.precision() / 2 + 1;
				} else {
					/*
					 * To avoid the need for post-Newton fix-up logic, in
					 * the case of half-way rounding modes, double the
					 * target precision so that the "2p + 2" property can
					 * be relied on to accomplish the final rounding.
					 */
					switch (mc.getRoundingMode()) {
						case HALF_UP:
						case HALF_DOWN:
						case HALF_EVEN:
							targetPrecision = 2 * originalPrecision;
							if (targetPrecision < 0) // Overflow
								targetPrecision = Integer.MAX_VALUE - 2;
							break;

						default:
							targetPrecision = originalPrecision;
							break;
					}
				}

				// When setting the precision to use inside the Newton
				// iteration loop, take care to avoid the case where the
				// precision of the input exceeds the requested precision
				// and rounding the input value too soon.
				BigDecimal approx = guess;
				int workingPrecision = working.precision();
				do {
					int tmpPrecision = Math.max(Math.max(guessPrecision, targetPrecision + 2),
							workingPrecision);
					MathContext mcTmp = new MathContext(tmpPrecision, RoundingMode.HALF_EVEN);
					// approx = 0.5 * (approx + fraction / approx)
					approx = ONE_HALF.multiply(approx.add(working.divide(approx, mcTmp), mcTmp));
					guessPrecision *= 2;
				} while (guessPrecision < targetPrecision + 2);

				BigDecimal result;
				RoundingMode targetRm = mc.getRoundingMode();
				if (targetRm == RoundingMode.UNNECESSARY || originalPrecision == 0) {
					RoundingMode tmpRm =
							(targetRm == RoundingMode.UNNECESSARY) ? RoundingMode.DOWN : targetRm;
					MathContext mcTmp = new MathContext(targetPrecision, tmpRm);
					result = approx.scaleByPowerOfTen(-scaleAdjust / 2).round(mcTmp);

					// If result*result != this numerically, the square
					// root isn't exact
					if (thi.subtract(result.multiply(result)).compareTo(BigDecimal.ZERO) != 0) {
						throw new ArithmeticException("Computed square root not exact.");
					}
				} else {
					result = approx.scaleByPowerOfTen(-scaleAdjust / 2).round(mc);

					switch (targetRm) {
						case DOWN:
						case FLOOR:
							// Check if too big
							if (result.multiply(result).compareTo(thi) > 0) {
								BigDecimal ulp = result.ulp();
								// Adjust increment down in case of 1.0 = 10^0
								// since the next smaller number is only 1/10
								// as far way as the next larger at exponent
								// boundaries. Test approx and *not* result to
								// avoid having to detect an arbitrary power
								// of ten.
								if (approx.compareTo(BigDecimal.ONE) == 0) {
									ulp = ulp.multiply(ONE_TENTH);
								}
								result = result.subtract(ulp);
							}
							break;

						case UP:
						case CEILING:
							// Check if too small
							if (result.multiply(result).compareTo(thi) < 0) {
								result = result.add(result.ulp());
							}
							break;

						default:
							// No additional work, rely on "2p + 2" property
							// for correct rounding. Alternatively, could
							// instead run the Newton iteration to around p
							// digits and then do tests and fix-ups on the
							// rounded value. One possible set of tests and
							// fix-ups is given in the Hull and Abrham paper;
							// however, additional half-way cases can occur
							// for BigDecimal given the more varied
							// combinations of input and output precisions
							// supported.
							break;
					}

				}

				// Test numerical properties at full precision before any
				// scale adjustments.
//      assert thi.squareRootResultAssertions(result, mc);

				if (result.scale() != preferredScale) {
					// The preferred scale of an add is
					// max(addend.scale(), augend.scale()). Therefore, if
					// the scale of the result is first minimized using
					// stripTrailingZeros(), adding a zero of the
					// preferred scale rounding to the correct precision
					// will perform the proper scale vs precision
					// tradeoffs.
					result = result.stripTrailingZeros().
							add(zeroWithFinalPreferredScale,
									new MathContext(originalPrecision, RoundingMode.UNNECESSARY));
				}
				return result;
			} else {
				BigDecimal result = null;
				switch (signum) {
					case -1:
						throw new ArithmeticException("Attempted square root " +
								"of negative BigDecimal");
					case 0:
						result = BigDecimal.valueOf(0L, thi.scale() / 2);
//          assert squareRootResultAssertions(result, mc);
						return result;

					default:
						throw new AssertionError("Bad value from signum");
				}
			}
		}
	}

	public Sqrt() {
		super(TypedEvaluables.builder()
			.addList(TypedEvaluable.of(Value.NumberValue.class, Parameter.of(Value.NumberValue.class)
					.withValidators(NumberValidator.greaterOrEqualThan(BigDecimal.ZERO)), new Number()))
			.build());
	}
}
