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

public class EvaluableException extends Exception {

	private final boolean isValidationError;

	private  EvaluableException(String message, boolean isValidationError) {
		super(message);
		this.isValidationError = isValidationError;
	}

	private  EvaluableException(String message) {
		this(message, false);
	}

	public boolean isValidationError() {
		return isValidationError;
	}

	public static EvaluableException ofUnsupportedDataTypeInOperation() {
		return new EvaluableException("Unsupported data types in operation");
	}

	public static EvaluableException of(String message, Object ... args) {
		return new EvaluableException(String.format(message, (args)));
	}

	public static EvaluableException validationError(String message) {
		return new EvaluableException(message, true);
	}
}
