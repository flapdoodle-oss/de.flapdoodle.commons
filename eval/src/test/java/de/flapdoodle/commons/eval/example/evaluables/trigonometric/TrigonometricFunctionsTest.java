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
package de.flapdoodle.commons.eval.example.evaluables.trigonometric;

import de.flapdoodle.commons.eval.core.Expression;
import de.flapdoodle.commons.eval.core.MapBasedVariableResolver;
import de.flapdoodle.commons.eval.core.VariableResolver;
import de.flapdoodle.commons.eval.core.evaluables.Evaluated;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.exceptions.ParseException;
import de.flapdoodle.commons.eval.example.BaseEvaluationTest;
import de.flapdoodle.commons.eval.example.Defaults;
import de.flapdoodle.commons.eval.example.Value;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TrigonometricFunctionsTest extends BaseEvaluationTest {

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"acos(0) : 90",
			"acos(1) : 0",
			"acos(-1) : 180",
		})
	void testAcos(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"acosH(1) : 0",
			"acosH(2) : 1.3169578969248166",
			"acosH(3) : 1.762747174039086",
		})
	void testAcosH(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@ParameterizedTest
	@ValueSource(doubles = { -1, -0.5, 0, 0.5, 0.9 })
	void testAcosHThrowsException(double d) {
		assertThatThrownBy(() -> {
            MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
			Value<?> value = Value.of(d);
			MapBasedVariableResolver mapBasedVariableResolver = mapBasedValueResolver.with("x", Evaluated.value(value));
			Expression expression = Defaults.expressionFactory().parse("acosH(x)");
			expression.evaluate(mapBasedVariableResolver).wrapped();
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessage("value is not >= 1: "+d);
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"acosR(0) : 1.5707963267948966",
			"acosR(1) : 0",
			"acosR(-1) : 3.141592653589793",
		})
	void testAcosR(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"acot(1) : 45",
			"acot(-1) : 135",
		})
	void testAcot(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = { "acotH(-1.5) : -0.8047189562170501", "acotH(1.5) : 0.8047189562170501" })
	void testAcotH(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asNumber(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"acotR(1) : 0.7853981633974483",
			"acotR(-1) : 2.356194490192345",
		})
	void testAcotR(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asNumber(expectedResult));
	}

	@Test
	void testAcotRThrowsException() {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse("acotR(0)");
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessage("value of 0 is invalid");
	}

	@Test
	void testAcotThrowsException() {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse("acot(0)");
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessage("value of 0 is invalid");
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"asin(0) : 0",
			"asin(1) : 90",
			"asin(-1) : -90",
		})
	void testAsin(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@Test
	void testAsinThrowsExceptionPositive() {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse("asin(1.5)");
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessage("Illegal x > 1 for asin(x): x = 1.5");
	}

	@Test
	void testAsinThrowsExceptionNegative() {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse("asin(-1.5)");
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessage("Illegal x < -1 for asin(x): x = -1.5");
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"asinH(0) : 0",
			"asinH(1) : 0.8813735870195429",
			"asinH(-1) : -0.8813735870195428",
		})
	void testAsinH(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"asinR(0) : 0",
			"asinR(1) : 1.5707963267948966",
			"asinR(-1) : -1.5707963267948966",
		})
	void testAsinR(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@Test
	void testAsinRThrowsExceptionPositive() {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse("asinR(1.5)");
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessage("Illegal x > 1 for asinR(x): x = 1.5");
	}

	@Test
	void testAsinRThrowsExceptionNegative() {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse("asinR(-1.5)");
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessage("Illegal x < -1 for asinR(x): x = -1.5");
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"atan(0) : 0",
			"atan(1) : 45",
			"atan(-1) : -45",
		})
	void testAtan(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"atan2(0,0) : 0",
			"atan2(0,1) : 0",
			"atan2(0,-1) : 180",
			"atan2(1,0) : 90",
			"atan2(1,1) : 45",
			"atan2(1,-1) : 135",
			"atan2(-1,0) : -90",
			"atan2(-1,1) : -45",
			"atan2(-1,-1) : -135",
		})
	void testAtan2(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"atan2R(0,0) : 0",
			"atan2R(0,1) : 0",
			"atan2R(0,-1) : 3.141592653589793",
			"atan2R(1,0) : 1.5707963267948966",
			"atan2R(1,1) : 0.7853981633974483",
			"atan2R(1,-1) : 2.356194490192345",
			"atan2R(-1,0) : -1.5707963267948966",
			"atan2R(-1,1) : -0.7853981633974483",
			"atan2R(-1,-1) : -2.356194490192345",
		})
	void testAtan2R(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"atanH(0) : 0",
			"atanH(0.9) : 1.4722194895832204",
			"atanH(-0.9) : -1.4722194895832204",
		})
	void testAtanH(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@ParameterizedTest
	@ValueSource(doubles = { -1.1, -1.0, 1.0, 1.1 })
	void testAtanHThrowsException(double d) {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse("atanH(x)");
            MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
			Value<?> value = Value.of(d);
			MapBasedVariableResolver mapBasedVariableResolver = mapBasedValueResolver.with("x", Evaluated.value(value));
			expression.evaluate(mapBasedVariableResolver).wrapped();
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessageContaining("Illegal")
			.hasMessageContaining("atanH(x): x = "+d);
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"atanR(0) : 0",
			"atanR(1) : 0.7853981633974483",
			"atanR(-1) : -0.7853981633974483",
		})
	void testAtanR(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"csc(1) : 57.298688498550185",
			"csc(19) : 3.0715534867572423",
			"csc(-19) : -3.0715534867572423"
		})
	void testCSC(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asNumber(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"cscH(1) : 0.8509181282393216",
			"cscH(19) : 0.000000011205592875074534",
			"cscH(-19) : -0.000000011205592875074534"
		})
	void testCSCH(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"cscR(1) : 1.1883951057781212",
			"cscR(19) : 6.672128486037505",
			"cscR(-19) : -6.672128486037505"
		})
	void testCSCR(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asNumber(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"cot(1) : 57.28996163075943",
			"cot(19) : 2.9042108776758226",
			"cot(-19) : -2.9042108776758226"
		})
	void testCoTan(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asNumber(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"cotR(1) : 0.6420926159343306",
			"cotR(19) : 6.596764247280111",
			"cotR(-19) : -6.596764247280111"
		})
	void testCoTanR(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asNumber(expectedResult));
	}

	@Test
	void testCoTanRThrowsException() {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse("cotR(0)");
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessage("value of 0 is invalid");
	}

	@Test
	void testCoTanThrowsException() {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse("cot(0)");
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessage("value of 0 is invalid");
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"cos(0) : 1",
			"cos(1) : 0.9998476951563913",
			"cos(19) : 0.9455185755993168",
			"cos(-19) : 0.9455185755993168"
		})
	void testCos(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"cosH(0) : 1",
			"cosH(1) : 1.543080634815244",
			"cosH(-1) : 1.543080634815244",
		})
	void testCosH(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"cosR(0) : 1",
			"cosR(1) : 0.5403023058681398",
			"cosR(19) : 0.9887046181866692",
			"cosR(-19) : 0.9887046181866692"
		})
	void testCosR(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"cotH(1) : 1.3130352854993315",
			"cotH(5) : 1.0000908039820193",
			"cotH(-5) : -1.0000908039820193"
		})
	void testCotH(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asNumber(expectedResult));
	}

	@Test
	void testCotHThrowsException() {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse("cotH(0)");
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessage("value of 0 is invalid");
	}

	@Test
	void testCscHThrowsException() {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse("cscH(0)");
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessage("value of 0 is invalid");
	}

	@Test
	void testCscRThrowsException() {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse("cscR(0)");
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessage("value of 0 is invalid");
	}

	@Test
	void testCscThrowsException() {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse("csc(0)");
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessage("value of 0 is invalid");
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"deg(0) : 0",
			"deg(1) : 57.29577951308232",
			"deg(90) : 5156.620156177409",
			"deg(-90) : -5156.620156177409"
		})
	void testDeg(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"rad(0) : 0",
			"rad(1) : 0.017453292519943295",
			"rad(45) : 0.7853981633974483",
			"rad(50) : 0.8726646259971648",
			"rad(90) : 1.5707963267948966",
			"rad(-90) : -1.5707963267948966"
		})
	void testRad(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"sec(1) : 1.0001523280439077",
			"sec(19) : 1.0576206811866706",
			"sec(-19) : 1.0576206811866706"
		})
	void testSec(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asNumber(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"secH(1) : 0.6480542736638853",
			"secH(19) : 0.000000011205592875074534",
			"secH(-19) : 0.000000011205592875074534"
		})
	void testSecH(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@Test
	void testSecHThrowsException() {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse("secH(0)");
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessage("value of 0 is invalid");
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"secR(1) : 1.8508157176809255",
			"secR(19) : 1.01142442505634",
			"secR(-19) : 1.01142442505634"
		})
	void testSecR(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asNumber(expectedResult));
	}

	@Test
	void testSecRThrowsException() {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse("secR(0)");
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessage("value of 0 is invalid");
	}

	@Test
	void testSecThrowsException() {
		assertThatThrownBy(() -> {
			Expression expression = Defaults.expressionFactory().parse("sec(0)");
			expression.evaluate(VariableResolver.empty()).wrapped();
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessage("value of 0 is invalid");
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = { "sin(0) : 0", "sin(1) : 0.01745240643728351", "sin(90) : 1", "sin(-90) : -1" })
	void testSin(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"sinH(0) : 0",
			"sinH(1) : 1.1752011936438014",
			"sinH(-1) : -1.1752011936438014",
		})
	void testSinH(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"sinR(0) : 0",
			"sinR(1) : 0.8414709848078965",
			"sinR(90) : 0.8939966636005579",
			"sinR(-90) : -0.8939966636005579"
		})
	void testSinR(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"tan(0) : 0",
			"tan(1) : 0.017455064928217585",
			"tan(19) : 0.34432761328966527",
			"tan(-19) : -0.34432761328966527"
		})
	void testTan(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"tanH(0) : 0",
			"tanH(1) : 0.7615941559557649",
			"tanH(-1) : -0.7615941559557649",
		})
	void testTanH(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"tanR(0) : 0",
			"tanR(1) : 1.5574077246549023",
			"tanR(19) : 0.15158947061240008",
			"tanR(-19) : -0.15158947061240008"
		})
	void testTanR(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}
}
