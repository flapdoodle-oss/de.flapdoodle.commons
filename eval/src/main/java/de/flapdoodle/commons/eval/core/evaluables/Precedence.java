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
package de.flapdoodle.commons.eval.core.evaluables;

public enum Precedence {
	/** Or operator precedence: || */
	OPERATOR_PRECEDENCE_OR(2),

	/** And operator precedence: && */
	OPERATOR_PRECEDENCE_AND(4),

	/** Equality operators precedence: =, ==, !=, <> */
	OPERATOR_PRECEDENCE_EQUALITY(7),

	/** Comparative operators precedence: <, >, <=, >= */
	OPERATOR_PRECEDENCE_COMPARISON(10),

	/** Additive operators precedence: + and - */
	OPERATOR_PRECEDENCE_ADDITIVE(20),

	/** Multiplicative operators precedence: *, /, % */
	OPERATOR_PRECEDENCE_MULTIPLICATIVE(30),

	/** Power operator precedence: ^ */
	OPERATOR_PRECEDENCE_POWER(40),

	/** Unary operators precedence: + and - as prefix */
	OPERATOR_PRECEDENCE_UNARY(60);
	private final int value;

	Precedence(int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}
}
