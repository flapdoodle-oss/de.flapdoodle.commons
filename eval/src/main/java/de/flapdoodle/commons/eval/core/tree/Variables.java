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
package de.flapdoodle.commons.eval.core.tree;

import de.flapdoodle.commons.checks.Preconditions;
import org.immutables.value.Value;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Value.Immutable
public abstract class Variables {
	// multiple entries with different hashes for same name
	public abstract List<Variable> list();

	@Value.Derived
	public Map<String, Integer> nameHashMap() {
		Map<String, Integer> map=new LinkedHashMap<>();
		list().forEach(pair -> map.putIfAbsent(pair.name(), pair.hash()));
		return map;
	}

	@Value.Derived
	public Set<String> names() {
		return nameHashMap().keySet();
	}

	@Value.Auxiliary
	public int firstHashOf(String name) {
		return Preconditions.checkNotNull(nameHashMap().get(name),"could not find hash of %s", name);
	}

	@Value.Auxiliary
	@Deprecated
	public int hashOf(String name) {
		return firstHashOf(name);
	}

	@Value.Auxiliary
	public List<Integer> positionsOf(String name) {
		Preconditions.checkNotNull(nameHashMap().get(name),"could not find hash of %s", name);
		return list().stream()
			.filter(it -> it.name().equals(name))
			.map(Variable::position)
			.sorted()
			.collect(Collectors.toList());
	}

	public static ImmutableVariables.Builder builder() {
		return ImmutableVariables.builder();
	}
}
