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
package de.flapdoodle.commons.reflection;

import de.flapdoodle.commons.testdoc.Recorder;
import de.flapdoodle.commons.testdoc.Recording;
import de.flapdoodle.commons.testdoc.TabSize;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class HowToTest {
    @RegisterExtension
    public static Recording recording = Recorder.with("HowToUseReflection.md", TabSize.spaces(2))
            .renderTo("Reflection.md");

    @Test
    void firstExample() {
        recording.begin();
        List<Object> instance = new ArrayList<>();
        TypeInfo<List<String>> typeInfo = TypeInfo.listOf(TypeInfo.of(String.class));

        assertThat(typeInfo.isInstance(instance)).isTrue();

        instance.add("Hello");
        assertThat(typeInfo.isInstance(instance)).isTrue();

        instance.add(2);
        assertThat(typeInfo.isInstance(instance)).isFalse();
        recording.end();
    }

    @Test
    void complexTypeChecksAndCasts() {
        recording.begin();
        List<Pair<String, Map<String, Integer>>> instance = new ArrayList<>();

        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        map.put("Hello", 1);
        instance.add(Pair.of("foo", map));

        TypeInfo<List<Pair<String, Map<String, Integer>>>> typeInfo = TypeInfo.listOf(
                Pair.typeInfo(
                        TypeInfo.of(String.class), TypeInfo.mapOf(
                                TypeInfo.of(String.class), TypeInfo.of(Integer.class)
                        )));

        Object erased = instance;

        assertThat(typeInfo.ifInstance(erased))
                .containsSame(instance);
        recording.end();
    }
}
