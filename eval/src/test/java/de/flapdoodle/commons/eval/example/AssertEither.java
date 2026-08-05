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
package de.flapdoodle.commons.eval.example;

import de.flapdoodle.commons.types.Either;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.InstanceOfAssertFactories;

import java.util.function.Consumer;

public class AssertEither<L, R> extends AbstractObjectAssert<AssertEither<L, R>, Either<L, R>> {

	public AssertEither(Either<L, R> either) {
		super(either, AssertEither.class);
	}

	public AssertEither<L, R> isLeft() {
		extracting(Either::isLeft, InstanceOfAssertFactories.BOOLEAN)
			.describedAs("isLeft")
			.isTrue();
		return myself;
	}

	public AssertEither<L, R> isRight() {
		extracting(Either::isLeft, InstanceOfAssertFactories.BOOLEAN)
			.describedAs("isLeft")
			.isFalse();
		return myself;
	}

	public AssertEither<L, R> containsLeft(L left) {
		extracting(either -> either.left())
			.describedAs("left")
			.isEqualTo(left);
		return myself;
	}

	public AssertEither<L, R> containsRight(R right) {
		extracting(either -> either.right())
			.describedAs("right")
			.isEqualTo(right);
		return myself;
	}

	public void rightSatisfies(Consumer<? super R> check) {
		extracting(either -> either.right())
			.describedAs("right")
			.satisfies(check);
	}

	public static <L, R> AssertEither<L, R> assertThat(Either<L, R> actual) {
		return new AssertEither<>(actual);
	}
}
