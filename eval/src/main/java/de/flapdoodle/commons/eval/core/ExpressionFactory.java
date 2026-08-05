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

import de.flapdoodle.commons.eval.core.evaluables.*;
import de.flapdoodle.commons.eval.core.exceptions.EvaluationException;
import de.flapdoodle.commons.eval.core.exceptions.ParseException;
import de.flapdoodle.commons.eval.core.parser.ASTNode;
import de.flapdoodle.commons.eval.core.parser.ShuntingYardConverter;
import de.flapdoodle.commons.eval.core.parser.Token;
import de.flapdoodle.commons.eval.core.parser.Tokenizer;
import de.flapdoodle.commons.eval.core.tree.*;

import java.math.MathContext;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@org.immutables.value.Value.Immutable
public abstract class ExpressionFactory {
	@org.immutables.value.Value.Default
	public  MathContext mathContext() {
		return MathContext.DECIMAL128;
	}

	@org.immutables.value.Value.Default
	protected ZoneId zoneId() {
		return ZoneId.systemDefault();
	}

	public  abstract VariableResolver constants();
	public  abstract TypedEvaluableByName evaluatables();
	protected abstract TypedEvaluableByNumberOfArguments arrayAccess();
	protected abstract TypedEvaluableByNumberOfArguments associateAccess();
	protected abstract TypedEvaluableByNumberOfArguments propertyAccess();
	protected abstract NumberAsValue numberAsValue();
	protected abstract StringAsValue stringAsValue();
	protected abstract EvaluableExceptionMapper exceptionMapper();

	public abstract OperatorMap operatorMap();

	@org.immutables.value.Value.Auxiliary
	public final ImmutableExpressionFactory withConstant(String name, Evaluated<?> value) {
		return ImmutableExpressionFactory.copyOf(this)
				.withConstants(MapBasedVariableResolver.empty()
						.with(name, value)
						.andThen(constants()));
	}

	@org.immutables.value.Value.Auxiliary
	public Expression parse(String expression) throws ParseException, EvaluationException {
		Node node = map(abstractSyntaxTree(expression));
		return Expression.builder()
			.mathContext(mathContext())
			.zoneId(zoneId())
			.root(node)
			.source(expression)
			.build();
	}

	// VisibleForTests
	@org.immutables.value.Value.Auxiliary
	public ASTNode abstractSyntaxTree(String expression) throws ParseException {
		return new ShuntingYardConverter(expression, tokens(expression), operatorMap(), evaluatables())
			.toAbstractSyntaxTree();
	}

	// VisibleForTests
	@org.immutables.value.Value.Auxiliary
	public List<Token> tokens(String expression) throws ParseException {
		return new Tokenizer(expression, operatorMap()).parse();
	}

	@org.immutables.value.Value.Auxiliary
	protected Node map(ASTNode startNode) throws EvaluationException {
		Node result;
		Token token = startNode.getToken();
		switch (token.type()) {
			case NUMBER_LITERAL:
				result = ValueNode.of(token, Evaluated.value(numberAsValue().parse(token.value(), mathContext())));
				break;
			case STRING_LITERAL:
				result = ValueNode.of(token, Evaluated.value(stringAsValue().parse(token.value())));
				break;
			case VARIABLE_OR_CONSTANT:
				result = getVariableOrConstant(token);
				break;
			case PREFIX_OPERATOR:
				result = prefixOperator(startNode, token);
				break;
			case POSTFIX_OPERATOR:
				result = postfixOperator(startNode, token);
				break;
			case INFIX_OPERATOR:
				result = infixOperator(startNode, token);
				break;
			case ARRAY_INDEX:
				result = evaluateArrayIndex(startNode);
				break;
			case ASSOCIATE_INDEX:
				result = evaluateAssociateIndex(startNode);
				break;
			case STRUCTURE_SEPARATOR:
				result = evaluateStructureSeparator(startNode);
				break;
			case FUNCTION:
				result = evaluateFunction(startNode, token);
				break;
			default:
				throw new EvaluationException(token, "Unexpected evaluation token: " + token);
		}
		return result;
	}

	private Node postfixOperator(ASTNode startNode, Token token) throws EvaluationException {
		List<Node> parameters = Arrays.asList(map(startNode.getParameters().get(0)));
		Optional<OperatorMapping> operatorMapping = operatorMap().postfixOperator(token.value());

		if (!operatorMapping.isPresent()) throw new EvaluationException(token, "could not find postfix operator");

		return evaluatableNode(token, operatorMapping.get(), parameters);
	}

	private EvaluatableNode evaluatableNode(Token token, OperatorMapping operatorMapping, List<Node> parameters) {
		Optional<? extends TypedEvaluableByArguments> evaluatable = evaluatables().find(operatorMapping.evaluatable(), parameters.size());
		if (evaluatable.isPresent()) {
			return EvaluatableNode.of(token, evaluatable.get(), parameters, exceptionMapper());
		} else {
			throw new RuntimeException("could not find evaluatable for "+ operatorMapping);
		}
	}

	private Node infixOperator(ASTNode startNode, Token token) throws EvaluationException {
		Node first = map(startNode.getParameters().get(0));
		Node second = map(startNode.getParameters().get(1));
		List<Node> parameters = Arrays.asList(first, second);
		Optional<OperatorMapping> operatorMapping = operatorMap().infixOperator(token.value());

		if (!operatorMapping.isPresent()) throw new EvaluationException(token, "could not find infix operator");

		return evaluatableNode(token, operatorMapping.get(), parameters);
	}

	private Node prefixOperator(ASTNode startNode, Token token) throws EvaluationException {
		List<Node> parameters = Arrays.asList(map(startNode.getParameters().get(0)));
		Optional<OperatorMapping> operatorMapping = operatorMap().prefixOperator(token.value());

		if (!operatorMapping.isPresent()) throw new EvaluationException(token, "could not find prefix operator");

		return evaluatableNode(token, operatorMapping.get(), parameters);
	}

	private Node getVariableOrConstant(Token token) {
		Evaluated<?> result = constants().get(token.value());
		if (result!=null) {
			return ValueNode.of(token, result);
		}
		return LookupNode.of(token);
	}

	private Node evaluateFunction(ASTNode startNode, Token token) throws EvaluationException {
		List<Node> parameterResults = new ArrayList<>();
		for (int i = 0; i < startNode.getParameters().size(); i++) {
			parameterResults.add(map(startNode.getParameters().get(i)));
		}

		Optional<? extends TypedEvaluableByArguments> evaluatable = evaluatables().find(token.value(),
			startNode.getParameters().size());

		if (!evaluatable.isPresent()) throw new EvaluationException(token, "could not find evaluatable");

		return EvaluatableNode.of(token, evaluatable.get(), parameterResults, exceptionMapper());
	}

	private Node evaluateArrayIndex(ASTNode startNode) throws EvaluationException {
		Node objectNode = map(startNode.getParameters().get(0));
		Node indexNode = map(startNode.getParameters().get(1));
		Optional<? extends TypedEvaluableByArguments> arrayAccess = arrayAccess().filterByNumberOfArguments(2);

		if (!arrayAccess.isPresent()) throw new EvaluationException(startNode.getToken(), "could not find array access");

		return EvaluatableNode.of(startNode.getToken(), arrayAccess.get(), Arrays.asList(objectNode, indexNode), exceptionMapper());
	}

	private Node evaluateAssociateIndex(ASTNode startNode) throws EvaluationException {
		Node objectNode = map(startNode.getParameters().get(0));
		Node indexNode = map(startNode.getParameters().get(1));
		Optional<? extends TypedEvaluableByArguments> associateAccess = associateAccess().filterByNumberOfArguments(2);

		if (!associateAccess.isPresent()) throw new EvaluationException(startNode.getToken(), "could not find associate access");

		return EvaluatableNode.of(startNode.getToken(), associateAccess.get(), Arrays.asList(objectNode, indexNode), exceptionMapper());
	}
	
	private Node evaluateStructureSeparator(ASTNode startNode) throws EvaluationException {
		Node structure = map(startNode.getParameters().get(0));
		Token nameToken = startNode.getParameters().get(1).getToken();
		Node name = ValueNode.of(nameToken, Evaluated.value(stringAsValue().parse(nameToken.value())));

		Optional<? extends TypedEvaluableByArguments> propertyAccess = propertyAccess().filterByNumberOfArguments(2);

		if (!propertyAccess.isPresent()) throw new EvaluationException(startNode.getToken(), "could not find property access");

		return EvaluatableNode.of(startNode.getToken(), propertyAccess.get(), Arrays.asList(structure, name), exceptionMapper());
	}

	public static ImmutableExpressionFactory.Builder builder() {
		return ImmutableExpressionFactory.builder();
	}
}
