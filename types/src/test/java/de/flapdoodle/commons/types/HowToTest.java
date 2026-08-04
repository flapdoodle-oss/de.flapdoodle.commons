package de.flapdoodle.commons.types;

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
}
