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

import de.flapdoodle.commons.eval.core.evaluables.OperatorMap;
import de.flapdoodle.commons.eval.core.evaluables.OperatorMapping;
import de.flapdoodle.commons.eval.core.evaluables.TypedEvaluableByArguments;
import de.flapdoodle.commons.eval.core.evaluables.TypedEvaluableByName;
import de.flapdoodle.commons.eval.core.exceptions.ParseException;

import java.util.*;

/**
 * @see <a href="https://en.wikipedia.org/wiki/Shunting_yard_algorithm">Shunting yard algorithm</a>
 * @see <a href="https://en.wikipedia.org/wiki/Abstract_syntax_tree">Abstract syntax tree</a>
 */
public class ShuntingYardConverter {
	private final String originalExpression;
	private final List<Token> expressionTokens;

	private final OperatorMap operatorMap;
	private final TypedEvaluableByName evaluatables;

	private final Deque<Token> operatorStack = new ArrayDeque<>();
	private final Deque<ASTNode> operandStack = new ArrayDeque<>();

	public ShuntingYardConverter(
			String originalExpression,
			List<Token> expressionTokens,
		OperatorMap operatorMap,
		TypedEvaluableByName evaluatables) {
		this.originalExpression = originalExpression;
		this.expressionTokens = expressionTokens;

		this.operatorMap = operatorMap;
		this.evaluatables = evaluatables;
	}

	public ASTNode toAbstractSyntaxTree() throws ParseException {

		Token previousToken = null;
		for (Token currentToken : expressionTokens) {
			switch (currentToken.type()) {
				case VARIABLE_OR_CONSTANT:
				case NUMBER_LITERAL:
				case STRING_LITERAL:
					operandStack.push(ASTNode.of(currentToken));
					break;
				case FUNCTION:
					operatorStack.push(currentToken);
					break;
				case COMMA:
					processOperatorsFromStackUntilTokenType(TokenType.BRACE_OPEN);
					break;
				case INFIX_OPERATOR:
				case PREFIX_OPERATOR:
				case POSTFIX_OPERATOR:
					processOperator(currentToken);
					break;
				case BRACE_OPEN:
					processBraceOpen(previousToken, currentToken);
					break;
				case BRACE_CLOSE:
					processBraceClose();
					break;
				case ARRAY_OPEN:
					processArrayOpen(currentToken);
					break;
				case ARRAY_CLOSE:
					processArrayClose();
					break;
				case ASSOCIATE_OPEN:
					processAssociateOpen(currentToken);
					break;
				case ASSOCIATE_CLOSE:
					processAssociateClose();
					break;
				case STRUCTURE_SEPARATOR:
					processStructureSeparator(currentToken);
					break;
				default:
					throw new ParseException(
						currentToken, "Unexpected token of type '" + currentToken.type() + "'");
			}
			previousToken = currentToken;
		}

		while (!operatorStack.isEmpty()) {
			Token token = operatorStack.pop();
			createOperatorNode(token);
		}

		if (operandStack.isEmpty()) {
			throw new ParseException(this.originalExpression, "Empty expression");
		}

		if (operandStack.size() > 1) {
			throw new ParseException(this.originalExpression, "Too many operands");
		}

		return operandStack.pop();
	}

	private void processStructureSeparator(Token currentToken) throws ParseException {
		Token nextToken = operatorStack.isEmpty() ? null : operatorStack.peek();
		while (nextToken != null && nextToken.type() == TokenType.STRUCTURE_SEPARATOR) {
			Token token = operatorStack.pop();
			createOperatorNode(token);
			nextToken = operatorStack.peek();
		}
		operatorStack.push(currentToken);
	}

	private void processBraceOpen(Token previousToken, Token currentToken) {
		if (previousToken != null && previousToken.type() == TokenType.FUNCTION) {
			// start of parameter list, marker for variable number of arguments
			Token paramStart =
				Token.of(
					currentToken.start(),
					currentToken.value(),
					TokenType.FUNCTION_PARAM_START);
			operandStack.push(ASTNode.of(paramStart));
		}
		operatorStack.push(currentToken);
	}

	private void processBraceClose() throws ParseException {
		processOperatorsFromStackUntilTokenType(TokenType.BRACE_OPEN);
		operatorStack.pop(); // throw away the marker
		if (!operatorStack.isEmpty() && operatorStack.peek().type() == TokenType.FUNCTION) {
			Token functionToken = operatorStack.pop();
			ArrayList<ASTNode> parameters = new ArrayList<>();
			while (true) {
				// add all parameters in reverse order from stack to the parameter array
				ASTNode node = operandStack.pop();
				if (node.getToken().type() == TokenType.FUNCTION_PARAM_START) {
					break;
				}
				parameters.add(0, node);
			}
			validateFunctionParameters(functionToken, parameters);
			operandStack.push(ASTNode.of(functionToken, parameters.toArray(new ASTNode[0])));
		}
	}

	private void validateFunctionParameters(Token functionToken, ArrayList<ASTNode> parameters)
		throws ParseException {
		Optional<? extends TypedEvaluableByArguments> evaluatable = evaluatables.find(functionToken.value(), parameters.size());
		if (!evaluatable.isPresent()) {
			throw new ParseException(functionToken, "could not find evaluatable '"+functionToken.value()+"' with "+parameters.size()+" arguments");
		}
	}

	/**
	 * Array index is treated like a function with two parameters. First parameter is the array (name
	 * or evaluation result). Second parameter is the array index.
	 *
	 * @param currentToken The current ARRAY_OPEN ("[") token.
	 */
	private void processArrayOpen(Token currentToken) throws ParseException {
		Token nextToken = operatorStack.isEmpty() ? null : operatorStack.peek();
		while (nextToken != null && (nextToken.type() == TokenType.STRUCTURE_SEPARATOR)) {
			Token token = operatorStack.pop();
			createOperatorNode(token);
			nextToken = operatorStack.isEmpty() ? null : operatorStack.peek();
		}
		// create ARRAY_INDEX operator (just like a function name) and push it to the operator stack
		Token arrayIndex =
			Token.of(currentToken.start(), currentToken.value(), TokenType.ARRAY_INDEX);
		operatorStack.push(arrayIndex);

		// push the ARRAY_OPEN to the operators, too (to later match the ARRAY_CLOSE)
		operatorStack.push(currentToken);
	}

	/**
	 * Associate index is treated like a function with two parameters. First parameter is the associate (name
	 * or evaluation result). Second parameter is the associate index.
	 *
	 * @param currentToken The current ASSOCIATE_OPEN ("[") token.
	 */
	private void processAssociateOpen(Token currentToken) throws ParseException {
		Token nextToken = operatorStack.isEmpty() ? null : operatorStack.peek();
		while (nextToken != null && (nextToken.type() == TokenType.STRUCTURE_SEPARATOR)) {
			Token token = operatorStack.pop();
			createOperatorNode(token);
			nextToken = operatorStack.isEmpty() ? null : operatorStack.peek();
		}
		// create ASSOCIATE_INDEX operator (just like a function name) and push it to the operator stack
		Token associateIndex =
			Token.of(currentToken.start(), currentToken.value(), TokenType.ASSOCIATE_INDEX);
		operatorStack.push(associateIndex);

		// push the ASSOCIATE_OPEN to the operators, too (to later match the ASSOCIATE_CLOSE)
		operatorStack.push(currentToken);
	}

	/**
	 * Follows the logic for a function, but with two fixed parameters.
	 *
	 * @throws ParseException If there were problems while processing the stacks.
	 */
	private void processArrayClose() throws ParseException {
		processOperatorsFromStackUntilTokenType(TokenType.ARRAY_OPEN);
		operatorStack.pop(); // throw away the marker
		Token arrayToken = operatorStack.pop();
		ArrayList<ASTNode> operands = new ArrayList<>();

		// second parameter of the "ARRAY_INDEX" function is the index (first on stack)
		ASTNode index = operandStack.pop();
		operands.add(0, index);

		// first parameter of the "ARRAY_INDEX" function is the array (name or evaluation result)
		// (second on stack)
		ASTNode array = operandStack.pop();
		operands.add(0, array);

		operandStack.push(ASTNode.of(arrayToken, operands.toArray(new ASTNode[0])));
	}

	/**
	 * Follows the logic for a function, but with two fixed parameters.
	 *
	 * @throws ParseException If there were problems while processing the stacks.
	 */
	private void processAssociateClose() throws ParseException {
		processOperatorsFromStackUntilTokenType(TokenType.ASSOCIATE_OPEN);
		operatorStack.pop(); // throw away the marker
		Token arrayToken = operatorStack.pop();
		ArrayList<ASTNode> operands = new ArrayList<>();

		// second parameter of the "ASSOCIATE_INDEX" function is the index (first on stack)
		ASTNode index = operandStack.pop();
		operands.add(0, index);

		// first parameter of the "ASSOCIATE_INDEX" function is the array (name or evaluation result)
		// (second on stack)
		ASTNode array = operandStack.pop();
		operands.add(0, array);

		operandStack.push(ASTNode.of(arrayToken, operands.toArray(new ASTNode[0])));
	}

	private void processOperatorsFromStackUntilTokenType(TokenType untilTokenType)
		throws ParseException {
		while (!operatorStack.isEmpty() && operatorStack.peek().type() != untilTokenType) {
			Token token = operatorStack.pop();
			createOperatorNode(token);
		}
	}

	private void createOperatorNode(Token token) throws ParseException {
		if (operandStack.isEmpty()) {
			throw new ParseException(token, "Missing operand for operator");
		}

		ASTNode operand1 = operandStack.pop();

		if (token.type() == TokenType.PREFIX_OPERATOR
			|| token.type() == TokenType.POSTFIX_OPERATOR) {
			operandStack.push(ASTNode.of(token, operand1));
		} else {
			if (operandStack.isEmpty()) {
				throw new ParseException(token, "Missing second operand for operator");
			}
			ASTNode operand2 = operandStack.pop();
			operandStack.push(ASTNode.of(token, operand2, operand1));
		}
	}

	private void processOperator(Token currentToken) throws ParseException {
		Token nextToken = operatorStack.isEmpty() ? null : operatorStack.peek();
		while (isOperator(nextToken)
			&& isNextOperatorOfHigherPrecedence(
			currentToken, nextToken)) {
			Token token = operatorStack.pop();
			createOperatorNode(token);
			nextToken = operatorStack.isEmpty() ? null : operatorStack.peek();
		}
		operatorStack.push(currentToken);
	}

	private boolean isNextOperatorOfHigherPrecedence(
			Token currentOperator, Token nextOperator
	) throws ParseException {
		return isNextOperatorOfHigherPrecedence(operatorMapping(currentOperator), operatorMapping(nextOperator));
	}


	private OperatorMapping operatorMapping(Token token) throws ParseException {
		switch (token.type()) {
			case PREFIX_OPERATOR:
				return operatorMap.prefixOperator(token.value()).orElse(null);
			case POSTFIX_OPERATOR:
				return operatorMap.postfixOperator(token.value()).orElse(null);
			case INFIX_OPERATOR:
				return operatorMap.infixOperator(token.value()).orElse(null);
		}
		return null;
	}

	private boolean isNextOperatorOfHigherPrecedence(
			OperatorMapping currentOperator, OperatorMapping nextOperator) {
		// structure operator (null) has always a higher precedence than other operators
		if (nextOperator == null) {
			return true;
		}

		if (currentOperator.isLeftAssociative()) {
			return currentOperator.precedence()
					<= nextOperator.precedence();
		} else {
			return currentOperator.precedence()
					< nextOperator.precedence();
		}
	}

	private boolean isOperator(Token token) {
		if (token == null) {
			return false;
		}
		TokenType tokenType = token.type();
		switch (tokenType) {
			case INFIX_OPERATOR:
			case PREFIX_OPERATOR:
			case POSTFIX_OPERATOR:
			case STRUCTURE_SEPARATOR:
				return true;
			default:
				return false;
		}
	}
}
