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
package de.flapdoodle.commons.eval.doc;

import de.flapdoodle.commons.eval.core.Expression;
import de.flapdoodle.commons.eval.core.ExpressionFactory;
import de.flapdoodle.commons.eval.core.MapBasedVariableResolver;
import de.flapdoodle.commons.eval.core.VariableResolver;
import de.flapdoodle.commons.eval.core.evaluables.*;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.exceptions.ParseException;
import de.flapdoodle.commons.eval.core.tree.EvalFailedWithException;
import de.flapdoodle.commons.eval.example.Defaults;
import de.flapdoodle.commons.eval.example.Value;
import de.flapdoodle.commons.testdoc.Recorder;
import de.flapdoodle.commons.testdoc.Recording;
import de.flapdoodle.commons.testdoc.TabSize;
import org.assertj.core.data.MapEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class HowToTest {
	@RegisterExtension
	public static Recording recording = Recorder.with("HowTo.md", TabSize.spaces(2));

	@Test
	public void example() throws ParseException, EvaluationException {
		recording.begin();
		ExpressionFactory expressionFactory = Defaults.expressionFactory();
		Expression expression = expressionFactory.parse("a*2");
		VariableResolver variableResolver = VariableResolver.empty()
			.with("a", Evaluated.value(Value.of(2)));
		Object result = expression.evaluate(variableResolver).wrapped();

		assertThat(result).isEqualTo(Value.of(4.0));
		recording.end();
	}

	@Test
	public void usedVariables() throws ParseException, EvaluationException {
		recording.begin();
		ExpressionFactory expressionFactory = Defaults.expressionFactory();
		Expression expression = expressionFactory.parse("a*2");
		assertThat(expression.usedVariables())
			.containsExactly("a");
		recording.end();
	}

	@Test
	public void usedVariablesWithHash() throws ParseException, EvaluationException {
		recording.begin("setup");
		ExpressionFactory expressionFactory = Defaults.expressionFactory();
		recording.end();

		recording.begin("first-expression");
		assertThat(expressionFactory.parse("a*2+b").usedVariablesWithHash())
			.containsExactly(
				MapEntry.entry("a",1546),
				MapEntry.entry("b",47022362)
			);
		recording.end();

		recording.begin("second-expression");
		assertThat(expressionFactory.parse("X*2+z").usedVariablesWithHash())
			.containsExactly(
				MapEntry.entry("X",1546),
				MapEntry.entry("z",47022362)
			);
		recording.end();
	}

	@Test
	public void minimalCustomSetup() throws ParseException, EvaluationException {
		recording.begin();
		ImmutableTypedEvaluables add = TypedEvaluables.builder()
			.addList(TypedEvaluable.of(BigDecimal.class, BigDecimal.class, BigDecimal.class,
				(valueResolver, evaluationContext, token, first, second) -> first.add(second)))
			.build();

		ExpressionFactory expressionFactory = ExpressionFactory.builder()
			.constants(VariableResolver.empty().with("pi", Evaluated.value(BigDecimal.valueOf(3.1415))))
			.evaluatables(TypedEvaluableMap.builder()
				.putMap("add", add)
				.build())
			.operatorMap(OperatorMap.builder()
				.putInfix("+", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_ADDITIVE, "add"))
				.build())
			.arrayAccess(TypedEvaluables.builder()
				.addList(TypedEvaluable.of(String.class, String.class, BigDecimal.class,
					(valueResolver, evaluationContext, token, first, second) -> "" + first.charAt(second.intValue())))
				.build())
			.associateAccess(TypedEvaluables.builder()
				.addList(TypedEvaluable.of(String.class, String.class, BigDecimal.class,
					(valueResolver, evaluationContext, token, first, second) -> "" + first.charAt(second.intValue())))
				.build())
			.propertyAccess(TypedEvaluables.builder()
				.addList(TypedEvaluable.of(String.class, Map.class, String.class,
					(valueResolver, evaluationContext, token, first, second) -> "" + first.get(second)))
				.build())
			.numberAsValue((s, m) -> new BigDecimal(s))
			.stringAsValue(s -> s)
			.exceptionMapper(EvalFailedWithException.mapper())
			.build();

		assertThat(expressionFactory.parse("pi")
			.evaluate(VariableResolver.empty()).wrapped())
			.isEqualTo(BigDecimal.valueOf(3.1415));

		assertThat(expressionFactory.parse("add(2,3)")
			.evaluate(VariableResolver.empty()).wrapped())
			.isEqualTo(BigDecimal.valueOf(5L));

		assertThat(expressionFactory.parse("2+3")
			.evaluate(VariableResolver.empty()).wrapped())
			.isEqualTo(BigDecimal.valueOf(5L));

		assertThat(expressionFactory.parse("\"fun\"[1]")
			.evaluate(VariableResolver.empty()).wrapped())
			.isEqualTo("u");

		assertThat(expressionFactory.parse("\"fun\"{1}")
			.evaluate(VariableResolver.empty()).wrapped())
			.isEqualTo("u");

		MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		Map<Object, Object> value = new LinkedHashMap<>();
		value.put("key", "stuff");

		assertThat(expressionFactory.parse("map.key")
			.evaluate(mapBasedValueResolver.with("map", Evaluated.value(value))).wrapped())
			.isEqualTo("stuff");

		recording.end();
	}
}
