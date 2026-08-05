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
package de.flapdoodle.commons.eval.example;

import de.flapdoodle.commons.eval.core.ExpressionFactory;
import de.flapdoodle.commons.eval.core.ImmutableExpressionFactory;
import de.flapdoodle.commons.eval.core.VariableResolver;
import de.flapdoodle.commons.eval.core.evaluables.*;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.tree.EvaluableExceptionMapper;
import de.flapdoodle.commons.eval.example.evaluables.arithmetic.*;
import de.flapdoodle.commons.eval.example.evaluables.basic.*;
import de.flapdoodle.commons.eval.example.evaluables.booleans.Combine;
import de.flapdoodle.commons.eval.example.evaluables.booleans.Not;
import de.flapdoodle.commons.eval.example.evaluables.datetime.*;
import de.flapdoodle.commons.eval.example.evaluables.string.Contains;
import de.flapdoodle.commons.eval.example.evaluables.string.ToLowerCase;
import de.flapdoodle.commons.eval.example.evaluables.string.ToUpperCase;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public abstract class Defaults {
	private Defaults() {
		// no instance
	}

	static Map<String, Evaluated<Value<?>>> standardConstants() {
		Map<String, Evaluated<Value<?>>> constants = new LinkedHashMap<>();

		constants.put("true", Evaluated.value(Value.of(true)));
		constants.put("false", Evaluated.value(Value.of(false)));
		constants.put(
			"PI",
			Evaluated.value(Value.of(
				new BigDecimal(
					"3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679"))));
		constants.put(
			"E",
			Evaluated.value(Value.of(
				new BigDecimal(
					"2.71828182845904523536028747135266249775724709369995957496696762772407663"))));
		constants.put("null", Evaluated.value(Value.ofNull()));
		return Collections.unmodifiableMap(constants);
	}

	private static final VariableResolver CONSTANTS = VariableResolver.empty().withValues(standardConstants());

	private static final TypedEvaluableByName EVALUATABLES = defaultEvaluatablesMap();
	private static final TypedEvaluableByNumberOfArguments ARRAY_ACCESS = new IndexedAccess();
	private static final TypedEvaluableByNumberOfArguments ASSOCIATE_ACCESS = new IndexedAccess();
	private static final TypedEvaluableByNumberOfArguments PROPERTY_ACCESS = new PropertyAccess();
	private static final OperatorMap OPERATOR_MAP = defaultOperatorMap();

	public static VariableResolver constants() {
		return CONSTANTS;
	}

	public static TypedEvaluableByName evaluatables() {
		return EVALUATABLES;
	}

	public static OperatorMap operatorMap() {
		return OPERATOR_MAP;
	}

	private static OperatorMap defaultOperatorMap() {
		return OperatorMap.builder()
			.putPrefix("+", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_UNARY, false, "sum"))
			.putPrefix("-", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_UNARY, false, "minus"))
			.putPrefix("!", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_UNARY, false, "not"))

			.putInfix("+", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_ADDITIVE, "sum"))
			.putInfix("-", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_ADDITIVE, "minus"))
			.putInfix("*", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_MULTIPLICATIVE, "multiply"))
			.putInfix("/", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_MULTIPLICATIVE, "divide"))
			.putInfix("^", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_POWER, false, "power"))
			.putInfix("%", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_MULTIPLICATIVE, "modulo"))
			// booleans
			.putInfix("=", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_EQUALITY, "equal"))
			.putInfix("==", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_EQUALITY, "equal"))
			.putInfix("===", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_EQUALITY, "same"))
			.putInfix("!=", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_EQUALITY, "notequal"))
			.putInfix("<>", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_EQUALITY, "notequal"))
			.putInfix(">", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_COMPARISON, "greater"))
			.putInfix(">=", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_COMPARISON, "greaterOrEqual"))
			.putInfix("<", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_COMPARISON, "less"))
			.putInfix("<=", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_COMPARISON, "lessOrEqual"))
			.putInfix("&&", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_AND, "and"))
			.putInfix("||", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_OR, "or"))
			.putPrefix("!", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_UNARY, "not"))
			.build();
	}

	public static final TypedEvaluableByNumberOfArguments arrayAccess() {
		return ARRAY_ACCESS;
	}

	public static final TypedEvaluableByNumberOfArguments associateAccess() {
		return ASSOCIATE_ACCESS;
	}

	public static final TypedEvaluableByNumberOfArguments propertyAccess() {
		return PROPERTY_ACCESS;
	}

	private static TypedEvaluableByName defaultEvaluatablesMap() {
		return TypedEvaluableMap.builder()
			.putMap("abs", new Abs())
			.putMap("ceiling", Round.ceiling()) // deprecated
			.putMap("factorial", new Factorial())
			.putMap("floor", Round.floor()) // deprecated

			.putMap("if", new Conditional())

			.putMap("log", new Log())
			.putMap("log10", new Log10())
			.putMap("max", new Max())
			.putMap("min", new Min())
			.putMap("not", new Not())
			.putMap("random", new Random())
			.putMap("round", new Round())
			.putMap("sum", new Plus())
			.putMap("sqrt", new Sqrt())
			// trigonometric
			.putMap("acos", Trigonometric.acos())
			.putMap("acosH", Trigonometric.acosH())
			.putMap("acosR", Trigonometric.acosR())
			.putMap("acot", Trigonometric.acot())
			.putMap("acotH", Trigonometric.acotH())
			.putMap("acotR", Trigonometric.acotR())
			.putMap("asin", Trigonometric.asin())
			.putMap("asinH", Trigonometric.asinH())
			.putMap("asinR", Trigonometric.asinR())
			.putMap("atan", Trigonometric.atan())
			.putMap("atan2", Trigonometric.atan2())
			.putMap("atan2R", Trigonometric.atan2R())
			.putMap("atanH", Trigonometric.atanH())
			.putMap("atanR", Trigonometric.atanR())
			.putMap("cos", Trigonometric.cos())
			.putMap("cosH", Trigonometric.cosH())
			.putMap("cosR", Trigonometric.cosR())
			.putMap("cot", Trigonometric.cot())
			.putMap("cotH", Trigonometric.cotH())
			.putMap("cotR", Trigonometric.cotR())
			.putMap("csc", Trigonometric.csc())
			.putMap("cscH", Trigonometric.cscH())
			.putMap("cscR", Trigonometric.cscR())
			.putMap("deg", Trigonometric.deg())
			.putMap("rad", Trigonometric.rad())
			.putMap("sin", Trigonometric.sin())
			.putMap("sinH", Trigonometric.sinH())
			.putMap("sinR", Trigonometric.sinR())
			.putMap("sec", Trigonometric.sec())
			.putMap("secH", Trigonometric.secH())
			.putMap("secR", Trigonometric.secR())
			.putMap("tan", Trigonometric.tan())
			.putMap("tanH", Trigonometric.tanH())
			.putMap("tanR", Trigonometric.tanR())
			// string functions
			.putMap("contains", new Contains())
			.putMap("toLower", new ToLowerCase())
			.putMap("toUpper", new ToUpperCase())
			// date time functions
			.putMap("localDateTime", new CreateLocalDateTime())
			.putMap("parseLocalDateTime", new DateTimeParser())
			.putMap("parseZonedDateTime", new ZonedDateTimeParser())
			.putMap("formatLocalDateTime", new FormatDateTime())
			.putMap("localDateTime2EpochMilli", Legacy.dateTime2Epoch())
			.putMap("localDateTimeOfEpochMilli", Legacy.epochFromNumber())
			.putMap("durationOfMillis", DurationParser.ofMillis())
			.putMap("durationOfDays", DurationParser.ofDays())
			.putMap("parseDuration", DurationParser.parseDuration())

			// operators only
			.putMap("minus", new Minus())
			.putMap("multiply", new Multiply())
			.putMap("divide", new Divide())
			.putMap("power", new PowerOf())
			.putMap("modulo", new Modulo())
			.putMap("equal", new Equals())
			.putMap("same", new Same())
			.putMap("notequal", Equals.not())
			.putMap("greater", Comparables.greater())
			.putMap("greaterOrEqual", Comparables.greaterOrEqual())
			.putMap("less", Comparables.less())
			.putMap("lessOrEqual", Comparables.lessOrEqual())
			.putMap("and", Combine.and())
			.putMap("or", Combine.or())
			.build();
	}

	public static Value.NumberValue numberFromString(String value, MathContext mathContext) {
		if (value.startsWith("0x") || value.startsWith("0X")) {
			BigInteger hexToInteger = new BigInteger(value.substring(2), 16);
			return Value.of(new BigDecimal(hexToInteger, mathContext));
		} else {
			return Value.of(new BigDecimal(value, mathContext));
		}
	}

	public static Value.StringValue valueFromString(String s) {
		return Value.of(s);
	}

	public static EvaluableExceptionMapper exceptionMapper() {
		return new EvaluableExceptionMapper() {
			@Override
			public Object map(EvaluationException ex) {
				return Value.failedWith(ex);
			}
			@Override
			public Optional<EvaluationException> match(Evaluated<?> value) {
				return value.wrapped() instanceof Value.FailedWithException
					? Optional.of(((Value.FailedWithException<?>) value.wrapped()).exception())
					: Optional.empty();
			}
		};
	}
	
	public static ImmutableExpressionFactory expressionFactory() {
		return ExpressionFactory.builder()
			.constants(constants())
			.evaluatables(evaluatables())
			.arrayAccess(arrayAccess())
			.associateAccess(associateAccess())
			.propertyAccess(propertyAccess())
			.numberAsValue(Defaults::numberFromString)
			.stringAsValue(Defaults::valueFromString)
			.operatorMap(operatorMap())
			.exceptionMapper(exceptionMapper())
			.build();
	}
}
