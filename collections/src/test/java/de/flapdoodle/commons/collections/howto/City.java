package de.flapdoodle.commons.collections.howto;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface City {
	String name();
	List<Address> addressList();

	public static ImmutableCity.Builder builder() {
		return ImmutableCity.builder();
	}
}
