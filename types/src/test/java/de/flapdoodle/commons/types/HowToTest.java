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
package de.flapdoodle.commons.types;

import de.flapdoodle.commons.reflection.TypeInfo;
import de.flapdoodle.commons.testdoc.Recorder;
import de.flapdoodle.commons.testdoc.Recording;
import de.flapdoodle.commons.testdoc.TabSize;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class HowToTest {

	@RegisterExtension
	public static Recording recording = Recorder.with("HowToUseTypes.md", TabSize.spaces(2))
		.renderTo("Types.md");

	@Test
	void eitherUsage() {
		recording.begin();
		Either<String, Integer> either = Either.left("left");

		String result = either
			.mapLeft(it -> "<" + it + ">")
			.mapRight(it -> it + 2)
			.map(l -> "left: " + l, r -> "right: " + r);

		assertThat(result).isEqualTo("left: <left>");
		recording.end();
	}

	@Test
	void eitherTypeInfo() {
		recording.begin();
		Object result = Either.<String, Integer>left("left")
			.mapLeft(it -> "<" + it + ">")
			.mapRight(it -> it + 2);

		TypeInfo<Either<String, Integer>> typeInfo = Either.typeInfo(String.class, Integer.class);

		assertThat(typeInfo.isInstance(result)).isTrue();
		Either<String, Integer> casted = typeInfo.cast(result);
		assertThat(casted).isEqualTo(Either.left("<left>"));
		recording.end();
	}

	@Test
	void pairUsage() {
		recording.begin();
		Pair<String, String> result = Pair.of("A", 2)
			.mapFirst(it -> "<" + it + ">")
			.mapSecond(it -> it + 2)
			.map(l -> "first: " + l, r -> "second: " + r);

		assertThat(result).isEqualTo(Pair.of("first: <A>","second: 4"));
		recording.end();
	}

	@Test
	void pairTypeInfo() {
		recording.begin();
		Object result = Pair.of("A", 2)
			.mapFirst(it -> "<" + it + ">")
			.mapSecond(it -> it + 2);

		TypeInfo<Pair<String, Integer>> typeInfo = Pair.typeInfo(String.class, Integer.class);
		assertThat(typeInfo.isInstance(result)).isTrue();
		Pair<String, Integer> casted = typeInfo.cast(result);
		assertThat(casted).isEqualTo(Pair.of("<A>", 4));
		recording.end();
	}

	@Test
	void tryUsage() {
		recording.begin();
		Function<String, URL> testee = Try.<String, URL, MalformedURLException>function(URL::new)
			.mapException(ex -> new Exception("failed", ex))
			.mapToUncheckedException(ex -> new RuntimeException("to runtime", ex));

		assertThatThrownBy(() -> testee.apply("broken"))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("to runtime")
			.hasCauseExactlyInstanceOf(Exception.class)
			.cause()
			.isInstanceOf(Exception.class)
			.hasMessageContaining("failed")
			.hasCauseExactlyInstanceOf(MalformedURLException.class)
			.cause()
			.isInstanceOf(MalformedURLException.class)
			.hasMessageContaining("no protocol: broken");
		recording.end();
	}

	@Test
	void maybeUsage() {
		recording.begin();
		Maybe<String> maybe = Maybe.some("value")
			.map(it -> "<" + it + ">")
			.map(it -> null);

		assertThat(maybe.hasSome()).isTrue();
		assertThat(maybe.get()).isNull();
		recording.end();
	}

	@Test
	void maybeTypeInfo() {
		recording.begin();
		Object result = Maybe.some("value")
			.map(it -> "<" + it + ">");

		TypeInfo<Maybe<String>> typeInfo = Maybe.typeInfo(String.class);

		assertThat(typeInfo.isInstance(result)).isTrue();
		Maybe<String> casted = typeInfo.cast(result);
		assertThat(casted).isEqualTo(Maybe.some("<value>"));
		recording.end();
	}

	@Test
	void lensUsage() {
		recording.begin();
		ImmutableSample sample = Sample.builder()
			.name("Name")
			.child(Sample.Child.builder()
				.name("Child")
				.age(12)
				.build())
			.build();

		Lens<Sample, String> name = Lens.ofProperty(Sample::name)
			.changeBy(ImmutableSample::copyOf, ImmutableSample::withName);
		Lens<Sample, Sample.Child> child = Lens.ofProperty(Sample::child)
			.changeBy(ImmutableSample::copyOf, ImmutableSample::withChild);

		Lens<Sample.Child, Integer> age = Lens.ofProperty(Sample.Child::age)
			.changeBy(ImmutableChild::copyOf, ImmutableChild::withAge);

		assertThat(sample.name()).isEqualTo("Name");
		assertThat(sample.child().name()).isEqualTo("Child");
		assertThat(sample.child().age()).isEqualTo(12);

		Sample withNewName = name.change(sample, "new Name");
		Sample withNewAge = child.and(age).change(withNewName, 18);

		assertThat(withNewAge.name()).isEqualTo("new Name");
		assertThat(withNewAge.child().name()).isEqualTo("Child");
		assertThat(withNewAge.child().age()).isEqualTo(18);
		recording.end();
	}
}
