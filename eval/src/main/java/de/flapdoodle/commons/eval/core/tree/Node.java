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
package de.flapdoodle.commons.eval.core.tree;

import de.flapdoodle.commons.eval.core.EvaluationContext;
import de.flapdoodle.commons.eval.core.VariableResolver;
import de.flapdoodle.commons.eval.core.VariableTypeResolver;
import de.flapdoodle.commons.eval.core.evaluables.Evaluated;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.parser.Token;
import de.flapdoodle.commons.reflection.TypeInfo;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Node {
    @org.immutables.value.Value.Parameter
    public abstract Token token();

    @org.immutables.value.Value.Auxiliary
    public abstract Evaluated<?> evaluate(VariableResolver variableResolver, EvaluationContext context) throws EvaluationException;

    @org.immutables.value.Value.Auxiliary
    public abstract TypeInfo<?> evaluateType(VariableTypeResolver variableResolver) throws EvaluationException;

    public static List<Node> allNodes(Node node) {
        ArrayList<Node> ret = new ArrayList<>();
        ret.add(node);
        List<Node> subnodes = Collections.emptyList();

        if (node instanceof EvaluatableNode) {
            subnodes = ((EvaluatableNode) node).parameters();
        }
        subnodes.forEach(subNode -> ret.addAll(allNodes(subNode)));
        return ret;
    }

    // VisibleForTests
    @Deprecated
    public static Set<String> usedVariables(List<Node> nodes) {
        return nodes.stream()
          .filter(it -> it instanceof LookupNode)
          .map(it -> it.token().value())
          .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Deprecated
    public static Set<String> usedVariables(Node node) {
        return usedVariables(allNodes(node));
    }

    // VisibleForTests
    public static Variables hashedUsedVariables(String expression, Node root) {
        List<Variable> entries=new ArrayList<>();
        hashedUsedVariables(entries, 0, expression, root);
        ImmutableVariables.Builder builder = Variables.builder();
        builder.addAllList(entries);
        return builder.build();
    }

    private static int hashedUsedVariables(List<Variable> entries, int lastTokenEnd, String expression, Node root) {
        if (root instanceof LookupNode) {
            Token token = root.token();
            String expressionBetweenTokens = expression.substring(lastTokenEnd, token.start());
            entries.add(Variable.of(token.value(), (entries.size()+":"+expressionBetweenTokens).hashCode(), token.start()));
            lastTokenEnd = token.start()+token.value().length();
        } else if (root instanceof EvaluatableNode) {
					for (Node it : ((EvaluatableNode) root).parameters()) {
              lastTokenEnd = hashedUsedVariables(entries, lastTokenEnd, expression, it);
					}
				} else {
            // ignore
        }
        return lastTokenEnd;
    }
}
