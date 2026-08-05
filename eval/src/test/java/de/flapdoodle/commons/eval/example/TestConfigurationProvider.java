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

import de.flapdoodle.commons.eval.core.ImmutableExpressionFactory;
import de.flapdoodle.commons.eval.core.evaluables.*;

import java.math.BigDecimal;

public class TestConfigurationProvider {

	public static final ImmutableExpressionFactory StandardFactoryWithAdditionalTestOperators;

	public static final OperatorMap OperatorMapWithTestOperators = OperatorMap.builder()
			.putPrefix("++", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_UNARY, false, "plusOne"))
			.putPostfix("++", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_UNARY, true, "plusOne"))
			.putPostfix("?", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_UNARY, false, "question"))
			.build()
			.andThen(Defaults.operatorMap());

	public static final TypedEvaluableByName EvaluatablesWithTestFunctions = TypedEvaluableMap.builder()
			.putMap("TEST", TypedEvaluables.builder()
					.addList(TypedEvaluable.ofVarArg(Value.StringValue.class, Value.StringValue.class, (valueResolver, evaluationContext, token, arguments) -> Value.of("OK")))
					.build())
			.putMap("plusOne", TypedEvaluables.builder()
					.addList(TypedEvaluable.of(Value.NumberValue.class, Value.NumberValue.class, (valueResolver, evaluationContext, token, argument) -> Value.of(argument.wrapped().add(BigDecimal.ONE))))
					.build())
			.putMap("question", TypedEvaluables.builder()
					.addList(TypedEvaluable.of(Value.StringValue.class, Value.NullValue.class, (valueResolver, evaluationContext, token, argument) -> Value.of("?")))
					.build())
			.build()
			.andThen(Defaults.evaluatables());

	static {
		StandardFactoryWithAdditionalTestOperators = Defaults.expressionFactory()
			.withEvaluatables(EvaluatablesWithTestFunctions)
			.withOperatorMap(OperatorMapWithTestOperators);
	}
}
