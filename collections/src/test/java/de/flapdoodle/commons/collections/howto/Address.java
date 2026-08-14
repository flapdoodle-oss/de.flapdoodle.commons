package de.flapdoodle.commons.collections.howto;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface Address {
	String name();
	int number();
	List<String> familyNames();

	static ImmutableAddress.Builder builder() {
		return ImmutableAddress.builder();
	}
}
