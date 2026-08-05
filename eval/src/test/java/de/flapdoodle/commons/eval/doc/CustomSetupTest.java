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
import de.flapdoodle.commons.eval.core.VariableResolver;
import de.flapdoodle.commons.eval.core.evaluables.*;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.exceptions.ParseException;
import de.flapdoodle.commons.eval.core.tree.EvalFailedWithException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomSetupTest {

	@Test
	public void example() throws ParseException, EvaluationException {
		ExpressionFactory expressionFactory = ExpressionFactory.builder()
			.constants(VariableResolver.empty())
			.evaluatables(TypedEvaluableMap.builder()
				.putMap("add", new Plus())
				.putMap("minus", new Minus())
				.build())
			.operatorMap(OperatorMap.builder()
				.putPrefix("+", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_UNARY, false, "add"))
				.putPrefix("-", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_UNARY, false, "minus"))

				.putInfix("+", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_ADDITIVE, "add"))
				.putInfix("-", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_ADDITIVE, "minus"))
				.build())
			.arrayAccess(new ArrayAccess())
			.associateAccess(new ArrayAccess())
			.propertyAccess(new PropertyAccess())
			.numberAsValue((s, m) -> Integer.parseInt(s))
			.stringAsValue(s -> s)
			.exceptionMapper(EvalFailedWithException.mapper())
			.build();

		Expression expression6 = expressionFactory.parse("add(2,3)");
		VariableResolver variableResolver6 = VariableResolver.empty();
		assertThat(expression6.evaluate(variableResolver6).wrapped())
			.isEqualTo(5);
		Expression expression5 = expressionFactory.parse("2+3");
		VariableResolver variableResolver5 = VariableResolver.empty();
		assertThat(expression5.evaluate(variableResolver5).wrapped())
			.isEqualTo(5);
		Expression expression4 = expressionFactory.parse("2-3");
		VariableResolver variableResolver4 = VariableResolver.empty();
		assertThat(expression4.evaluate(variableResolver4).wrapped())
			.isEqualTo(-1);
		Expression expression3 = expressionFactory.parse("-2");
		VariableResolver variableResolver3 = VariableResolver.empty();
		assertThat(expression3.evaluate(variableResolver3).wrapped())
			.isEqualTo(-2);

		Expression expression2 = expressionFactory.parse("add(a,b)");
		VariableResolver variableResolver2 = VariableResolver.empty()
			.with("a", Evaluated.ofNull(Integer.class))
			.and("b", Evaluated.ofNull(Integer.class));
		assertThat(expression2.evaluate(variableResolver2).wrapped())
			.isNull();

		Expression expression1 = expressionFactory.parse("add(a,b)");
		VariableResolver variableResolver1 = VariableResolver.empty()
			.with("a", Evaluated.ofNullable(Integer.class, 1))
			.and("b", Evaluated.ofNull(Integer.class));
		assertThat(expression1.evaluate(variableResolver1).wrapped())
			.isEqualTo(1);

		Expression expression = expressionFactory.parse("add(a,b)");
		VariableResolver variableResolver = VariableResolver.empty()
			.with("a", Evaluated.ofNull(Integer.class))
			.and("b", Evaluated.ofNullable(Integer.class, 2));
		assertThat(expression.evaluate(variableResolver).wrapped())
			.isEqualTo(2);
	}
}
