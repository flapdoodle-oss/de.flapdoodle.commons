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

import java.util.Objects;

/**
 * Base exception class used in EvalEx.
 */
public class BaseException extends Exception {

	private final int startPosition;
	private final int endPosition;
	private final String tokenString;
	private final String message;

	public BaseException(int startPosition, int endPosition, String tokenString, String message) {
		super(message);
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.tokenString = tokenString;
		this.message = super.getMessage();
	}

	public BaseException(int startPosition, int endPosition, String tokenString, Throwable cause) {
		super(cause);
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.tokenString = tokenString;
		this.message = cause.getMessage();
	}

	public String toString() {
		return "BaseException(startPosition=" + this.startPosition + ", endPosition=" + this.endPosition + ", tokenString=" + this.tokenString + ", message="
			+ this.message + ")";
	}

	public int getStartPosition() {
		return this.startPosition;
	}
	public int getEndPosition() {
		return this.endPosition;
	}
	public String getTokenString() {
		return this.tokenString;
	}
	public String getMessage() {
		return this.message;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BaseException that = (BaseException) o;
		return startPosition == that.startPosition && endPosition == that.endPosition && Objects.equals(tokenString, that.tokenString)
			&& Objects.equals(message, that.message);
	}

	@Override
	public int hashCode() {
		return Objects.hash(startPosition, endPosition, tokenString, message);
	}
}
