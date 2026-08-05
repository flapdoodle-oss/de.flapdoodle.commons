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
package de.flapdoodle.commons.eval.core.parser;

import org.immutables.value.Value;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Expressions are parsed into an abstract syntax tree (AST). The tree has one root node and each
 * node has zero or more children (parameters), depending on the operation. A leaf node is a
 * numerical or string constant that has no more children (parameters). Other nodes define
 * operators, functions and special operations like array index and structure separation.
 *
 * <p>The tree is evaluated from bottom (leafs) to top, in a recursive way, until the root node is
 * evaluated, which then holds the result of the complete expression.
 *
 * <p>To be able to visualize the tree, a <code>toJSON</code> method is provided. The produced JSON
 * string can be used to visualize the tree. OE.g. with this online tool:
 *
 * <p><a href="https://vanya.jp.net/vtree/">Online JSON to Tree Diagram Converter</a>
 */
@Value.Immutable
public abstract class ASTNode {

	public abstract List<ASTNode> getParameters();

	public abstract Token getToken();

	public static ASTNode of(Token token, ASTNode... parameters) {
		return ImmutableASTNode.builder()
			.token(token)
			.addParameters(parameters)
			.build();
	}

	/**
	 * Produces a JSON string representation of this node ad all its children.
	 *
	 * @return A JSON string of the tree structure starting at this node.
	 */
	public String toJSON() {
		if (getParameters().isEmpty()) {
			return String.format(
				"{" + "\"type\":\"%s\",\"value\":\"%s\"}", getToken().type(), getToken().value());
		} else {
			String childrenJson =
				getParameters().stream().map(ASTNode::toJSON).collect(Collectors.joining(","));
			return String.format(
				"{" + "\"type\":\"%s\",\"value\":\"%s\",\"children\":[%s]}",
				getToken().type(), getToken().value(), childrenJson);
		}
	}
}
