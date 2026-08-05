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
package de.flapdoodle.commons.eval.core.exceptions;

import de.flapdoodle.commons.eval.core.parser.Token;

/**
 * Exception while parsing the expression.
 */
public class ParseException extends BaseException {

	public ParseException(int startPosition, int endPosition, String tokenString, String message) {
		super(startPosition, endPosition, tokenString, message);
	}

	public ParseException(String expression, String message) {
		super(1, expression.length(), expression, message);
	}

	public ParseException(Token token, String message) {
		super(
			token.start(),
			token.start() + token.value().length() - 1,
			token.value(),
			message);
	}

	public String toString() {
		return "ParseException(super=" + super.toString() + ")";
	}
}
