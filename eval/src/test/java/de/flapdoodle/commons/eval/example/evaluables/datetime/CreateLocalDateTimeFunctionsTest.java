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

import de.flapdoodle.commons.eval.core.ExpressionFactory;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.exceptions.ParseException;
import de.flapdoodle.commons.eval.example.BaseEvaluationTest;
import de.flapdoodle.commons.eval.example.TestConfigurationProvider;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.ZoneId;

class CreateLocalDateTimeFunctionsTest extends BaseEvaluationTest {

	private final ExpressionFactory DateTimeTestConfiguration =
		TestConfigurationProvider.StandardFactoryWithAdditionalTestOperators
			.withZoneId(ZoneId.of("UTC+2"));

	@ParameterizedTest
	@CsvSource(
		delimiter = '|',
		value = {
			"parseLocalDateTime(\"2022-10-30T11:50:20Z\") | 2022-10-30T11:50:20Z",
			"parseLocalDateTime(\"2022-10-30T11:50:20\") | 2022-10-30T09:50:20Z",
			"parseLocalDateTime(\"2022-10-30T11:50:20.000000030\") | 2022-10-30T09:50:20.000000030Z",
			"parseLocalDateTime(\"2022-10-30\") | 2022-10-30T00:00:00Z",
			"parseLocalDateTime(\"30/10/2022 11:50:20\", \"dd/MM/yyyy HH:mm:ss\") | 2022-10-30T09:50:20Z",
			"parseLocalDateTime(\"30/10/2022\",\"dd/MM/yyyy\") | 2022-10-30T00:00:00Z",
		})
	void testDateTimeParse(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, expectedResult, DateTimeTestConfiguration);
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = '|',
		value = {
			"parseLocalDateTime(\"NOT A DATE\") | Unable to parse date/time: NOT A DATE",
		})
	void testDateTimeParseFailure(String expression, String message) {
		assertExpressionThrowsException(expression, message, DateTimeTestConfiguration);
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = '|',
		value = {
			"parseZonedDateTime(\"2022-10-30T11:50:20Z\") | 2022-10-30T11:50:20Z",
			"parseZonedDateTime(\"2011-12-03T10:15:30+01:00[Europe/Paris]\") | 2011-12-03T09:15:30Z",
			"parseZonedDateTime(\"2011-12-03T10:15:30+01:00\") | 2011-12-03T09:15:30Z",
		})
	void testZonedDateTimeParse(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, expectedResult, DateTimeTestConfiguration);
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = '|',
		value = {
			"parseZonedDateTime(\"03/12/2011 10:15:30 Europe/Paris\", \"dd/MM/yyyy HH:mm:ss v\") |"
				+ " 2011-12-03T09:15:30Z",
			"parseZonedDateTime(\"03/08/2019T16:20:17:717+05:30\",\"dd/MM/uuuu'T'HH:mm:ss:SSSXXXXX\") |"
				+ " 2019-08-03T10:50:17.717Z",
		})
	@DisabledOnJre({JRE.JAVA_8})
	void testZonedDateTimeParseJdk11(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, expectedResult, DateTimeTestConfiguration);
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = '|',
		value = {
			"parseZonedDateTime(\"NOT A DATE\") | Unable to parse zoned date/time: NOT A DATE",
		})
	void testZonedDateTimeParseFailure(String expression, String message) {
		assertExpressionThrowsException(expression, message, DateTimeTestConfiguration);
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = '|',
		value = {
			"localDateTime(2022,10,30) | 2022-10-29T22:00:00Z",
			"localDateTime(2022,10,30,11) | 2022-10-30T09:00:00Z",
			"localDateTime(2022,10,30,11,50,20) | 2022-10-30T09:50:20Z",
			"localDateTime(2022,10,30,11,50,20,30) | 2022-10-30T09:50:20.000000030Z"
		})
	void testDateTime(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, expectedResult, DateTimeTestConfiguration);
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = '|',
		value = {
			"formatLocalDateTime(parseLocalDateTime(\"2022-10-30T11:50:20\")) | 2022-10-30T11:50:20",
			"formatLocalDateTime(parseLocalDateTime(\"2022-10-30T11:50:20.000000030\"), \"dd/MM/yyyy\") | 30/10/2022",
			"formatLocalDateTime(parseLocalDateTime(\"2022-10-30T11:50:20.000000030\"), \"dd/MM/yyyy HH:mm:ss\") |"
				+ " 30/10/2022 11:50:20"
		})
	void testDateTimeFormat(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, expectedResult, DateTimeTestConfiguration);
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = '|',
		value = {
			"localDateTime2EpochMilli(localDateTimeOfEpochMilli(1667130620000)) | 1667130620000",
			"localDateTime2EpochMilli(localDateTimeOfEpochMilli(0)) | 0"
		})
	void testDateTimeToEpoch(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = '|',
		value = {
			"durationOfMillis(1667130620000) | PT463091H50M20S",
			"durationOfMillis(0) | PT0S"
		})
	void testDurationFromMillis(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asDuration(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = '|',
		value = { "durationOfDays(53216) | PT1277184H", "durationOfDays(1) | PT24H" })
	void testDurationFromDays(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asDuration(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = '|',
		value = {
			"parseDuration(\"PT1277184H\") | PT1277184H",
			"parseDuration(\"P1D\") | PT24H"
		})
	void testDurationParse(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asDuration(expectedResult));
	}
}
