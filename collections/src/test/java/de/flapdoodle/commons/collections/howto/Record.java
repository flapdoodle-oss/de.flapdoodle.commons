package de.flapdoodle.commons.collections.howto;

import org.immutables.value.Value;

@Value.Immutable
public interface Record {
	String lastname();
	String city();
	String street();
	int number();

	static ImmutableRecord.Builder builder() {
		return ImmutableRecord.builder();
	}
}
