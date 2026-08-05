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
package de.flapdoodle.commons.eval.core;

import de.flapdoodle.commons.eval.core.evaluables.Evaluated;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.exceptions.ParseException;
import de.flapdoodle.commons.eval.core.tree.Node;
import de.flapdoodle.commons.eval.core.tree.Variables;
import de.flapdoodle.commons.reflection.TypeInfo;
import org.immutables.value.Value;

import java.math.MathContext;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Value.Immutable
public abstract class Expression {
	protected abstract MathContext mathContext();
	protected abstract ZoneId zoneId();
	public abstract String source();
	public abstract Node root();

	@Value.Auxiliary
	public Evaluated<?> evaluate(VariableResolver variableResolver) throws EvaluationException, ParseException {
		return root().evaluate(variableResolver, EvaluationContext.builder()
			.mathContext(mathContext())
			.zoneId(zoneId())
			.build());
	}

	@Value.Auxiliary
	public TypeInfo<?> evaluateType(VariableTypeResolver variableResolver) throws EvaluationException {
		return root().evaluateType(variableResolver);
	}

	@Value.Derived
	public Variables variables() {
		return Node.hashedUsedVariables(source(), root());
	}

	@Value.Lazy
	public Map<String, Integer> usedVariablesWithHash() {
		return variables().nameHashMap();
	}

	@Value.Derived
	public Set<String> usedVariables() {
		return variables().names();
	}

	@Value.Auxiliary
	public Set<String> undefinedVariables(VariableResolver variableResolver) {
		return usedVariables().stream()
			.filter(name -> !variableResolver.has(name))
			.collect(Collectors.toSet());
	}

	@Value.Auxiliary
	public List<Node> allNodes() {
		return Node.allNodes(root());
	}

	public static ImmutableExpression.Builder builder() {
		return ImmutableExpression.builder();
	}

}
