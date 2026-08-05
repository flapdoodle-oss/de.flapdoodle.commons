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

import de.flapdoodle.commons.eval.core.ImmutableExpressionFactory;
import de.flapdoodle.commons.eval.core.evaluables.OperatorMap;
import de.flapdoodle.commons.eval.core.evaluables.TypedEvaluableByName;
import de.flapdoodle.commons.eval.core.exceptions.ParseException;
import de.flapdoodle.commons.eval.example.TestConfigurationProvider;
import org.assertj.core.api.Assertions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** */
public abstract class BaseParserTest {

	ImmutableExpressionFactory factory =
		TestConfigurationProvider.StandardFactoryWithAdditionalTestOperators;

	OperatorMap operatorMap =
			TestConfigurationProvider.OperatorMapWithTestOperators;

	TypedEvaluableByName evaluatables =
			TestConfigurationProvider.EvaluatablesWithTestFunctions;

	void assertAllTokensParsedCorrectly(String input, Token... expectedTokens) throws ParseException {
		List<Token> tokensParsed = factory.tokens(input);

		Assertions.assertThat(tokensParsed).containsExactly(expectedTokens);
	}

	/**
	 * Compares if the generated abstract syntax tree is correct. To visualize the generated JSON in a
	 * tree format, i.e. to display the AST, you can use the following online service:<br>
	 * <a href="https://vanya.jp.net/vtree/">Online JSON to Tree Diagram Converter</a>
	 */
	void assertASTTreeIsEqualTo(String expression, String treeJSON) throws ParseException {
		ASTNode root = factory.abstractSyntaxTree(expression);
		assertThat(root.toJSON()).isEqualTo(treeJSON);
	}
}
