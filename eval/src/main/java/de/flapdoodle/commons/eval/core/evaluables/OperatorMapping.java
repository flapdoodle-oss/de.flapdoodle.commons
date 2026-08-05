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

import org.immutables.value.Value;

@Value.Immutable
public interface OperatorMapping {
	/**
	 * @return The operator's precedence.
	 */
	int precedence();

	/**
	 * If operators with same precedence are evaluated from left to right.
	 *
	 * @return The associativity.
	 */
	boolean isLeftAssociative();

	/**
	 * name of evaluatable delegate
	 */
	String evaluatable();

	static ImmutableOperatorMapping.Builder builder() {
		return ImmutableOperatorMapping.builder();
	}

	static OperatorMapping of(int precedence, boolean isLeftAssociative, String evaluatable) {
		return builder()
				.precedence(precedence)
				.isLeftAssociative(isLeftAssociative)
				.evaluatable(evaluatable)
				.build();
	}

	static OperatorMapping of(Precedence precedence, boolean isLeftAssociative, String evaluatable) {
		return of(precedence.value(), isLeftAssociative, evaluatable);
	}

	static OperatorMapping of(Precedence precedence, String evaluatable) {
		return of(precedence, true, evaluatable);
	}
}
