package de.flapdoodle.commons.collections;

import de.flapdoodle.commons.collections.howto.*;
import de.flapdoodle.commons.collections.howto.ImmutableAddress;
import de.flapdoodle.commons.reflection.TypeInfo;
import de.flapdoodle.commons.testdoc.Recorder;
import de.flapdoodle.commons.testdoc.Recording;
import de.flapdoodle.commons.testdoc.TabSize;
import de.flapdoodle.commons.types.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class HowToTest {
	@RegisterExtension
	public static Recording recording = Recorder.with("HowToUseCollections.md", TabSize.spaces(2))
		.renderTo("Collections.md");

	@Test
	void groupBy() {
		recording.begin("records");
		List<Record> records = Arrays.asList(
			Record.builder().city("Hamburg").street("Hauptstraße").number(2).lastname("Schmidt").build(),
			Record.builder().city("Hamburg").street("Hauptstraße").number(2).lastname("Meier").build(),
			Record.builder().city("Hamburg").street("Nebenstraße").number(3).lastname("Jonson").build(),
			Record.builder().city("Köln").street("Hauptstraße").number(2).lastname("Schüler").build(),
			Record.builder().city("Köln").street("Am Kanal").number(7).lastname("Schneider").build()
		);
		recording.end();

		recording.begin("group-by-address");
		GroupBy<Record, ImmutableAddress, Pair<String, Integer>> groupByAddress = GroupBy.withListOf(Record.class)
			.map(record -> Address.builder().name(record.street()).number(record.number()).build())
			.identifiedBy(address -> Pair.of(address.name(), address.number()))
			.merge(ImmutableAddress::withFamilyNames, Record::lastname);
		recording.end();

		recording.begin("records-grouped-by-address");
		List<? extends Address> groupedByAddress = groupByAddress.apply(records);

		assertThat(groupedByAddress)
			.hasSize(3)
			.satisfiesExactly(
				first -> {
					assertThat(first.name()).isEqualTo("Hauptstraße");
					assertThat(first.number()).isEqualTo(2);
					assertThat(first.familyNames()).containsExactly("Schmidt", "Meier", "Schüler");
				},
				second -> {
					assertThat(second.name()).isEqualTo("Nebenstraße");
					assertThat(second.number()).isEqualTo(3);
					assertThat(second.familyNames()).containsExactly("Jonson");
				},
				third -> {
					assertThat(third.name()).isEqualTo("Am Kanal");
					assertThat(third.number()).isEqualTo(7);
					assertThat(third.familyNames()).containsExactly("Schneider");
				});
		recording.end();

		recording.begin("group-by-city");
		GroupBy<Record, ImmutableCity, String> groupByCity = GroupBy.withListOf(Record.class)
			.map(record -> City.builder().name(record.city()).build())
			.identifiedBy(City::name)
			.merge((city, matchingRecords) -> city.withAddressList(groupByAddress.apply(matchingRecords)));
		recording.end();

		recording.begin("records-grouped-by-city");
		List<? extends City> groupedByCity = groupByCity.apply(records);

		assertThat(groupedByCity)
			.hasSize(2)
			.satisfiesExactly(
				city_a -> {
					assertThat(city_a.name()).isEqualTo("Hamburg");
					assertThat(city_a.addressList())
						.hasSize(2)
						.satisfiesExactly(
							first -> {
								assertThat(first.name()).isEqualTo("Hauptstraße");
								assertThat(first.number()).isEqualTo(2);
								assertThat(first.familyNames()).containsExactly("Schmidt", "Meier");
							},
							second -> {
								assertThat(second.name()).isEqualTo("Nebenstraße");
								assertThat(second.number()).isEqualTo(3);
								assertThat(second.familyNames()).containsExactly("Jonson");
							});
				},
				city_b -> {
					assertThat(city_b.name()).isEqualTo("Köln");
					assertThat(city_b.addressList())
						.hasSize(2)
						.satisfiesExactly(
							first -> {
								assertThat(first.name()).isEqualTo("Hauptstraße");
								assertThat(first.number()).isEqualTo(2);
								assertThat(first.familyNames()).containsExactly("Schüler");
							},
							second -> {
								assertThat(second.name()).isEqualTo("Am Kanal");
								assertThat(second.number()).isEqualTo(7);
								assertThat(second.familyNames()).containsExactly("Schneider");
							});
				});
		recording.end();
	}

	@Test
	void immutableTypedMap() {
		recording.begin();
		ImmutableTypedMap<String> immutableMap = TypedMap.<String>immutable()
			.add(TypeInfo.of(String.class), "foo", "bar")
			.add(Pair.typeInfo(String.class, Double.class), "bar", Pair.of("x", 2.0));

		ImmutableTypedMap<String> copy = immutableMap.add(TypeInfo.of(String.class), "bar", "different type");

		assertThat(copy.get(TypeInfo.of(String.class), "foo"))
			.isEqualTo("bar");
		assertThat(copy.get(Pair.typeInfo(String.class, Double.class), "bar"))
			.isEqualTo(Pair.of("x", 2.0));
		assertThat(copy.get(TypeInfo.of(String.class), "bar"))
			.isEqualTo("different type");
		recording.end();
	}

	@Test
	void mutableTypedMap() {
		recording.begin();
		MutableTypedMap<String> typedMap = TypedMap.mutable();

		typedMap.put(TypeInfo.of(String.class), "foo", "bar");
		typedMap.put(Pair.typeInfo(String.class, Double.class), "bar", Pair.of("x", 2.0));

		assertThat(typedMap.get(TypeInfo.of(String.class), "foo"))
			.isEqualTo("bar");
		assertThat(typedMap.get(Pair.typeInfo(String.class, Double.class), "bar"))
			.isEqualTo(Pair.of("x", 2.0));
		assertThat(typedMap.get(TypeInfo.of(String.class), "bar"))
			.isNull();
		recording.end();
	}

	@Test
	void typeMapMutableToImmutable() {
		recording.begin();
		MutableTypedMap<String> typedMap = TypedMap.mutable();

		typedMap.put(TypeInfo.of(String.class), "foo", "bar");
		typedMap.put(Pair.typeInfo(String.class, Double.class), "bar", Pair.of("x", 2.0));

		ImmutableTypedMap<String> copy = typedMap.asImmutable();

		assertThat(copy.get(TypeInfo.of(String.class), "foo"))
			.isEqualTo("bar");
		assertThat(copy.get(Pair.typeInfo(String.class, Double.class), "bar"))
			.isEqualTo(Pair.of("x", 2.0));

		MutableTypedMap<String> mutableAgain = copy.asMutable();
		recording.end();
	}
}
