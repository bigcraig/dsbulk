/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datastax.oss.dsbulk.mapping;

import static com.datastax.oss.dsbulk.mapping.MappingPreference.INDEXED_ONLY;
import static com.datastax.oss.dsbulk.mapping.MappingPreference.MAPPED_ONLY;
import static com.datastax.oss.dsbulk.mapping.MappingPreference.MAPPED_OR_INDEXED;
import static com.datastax.oss.dsbulk.tests.assertions.TestAssertions.assertThat;
import static com.datastax.oss.dsbulk.tests.assertions.TestAssertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class MappingInspectorTest {

  // These words are passed to MappingInspector's constructor by the engine
  private static final CQLWord INTERNAL_TIMESTAMP_VARNAME = CQLWord.fromInternal("[timestamp]");
  private static final CQLWord INTERNAL_TTL_VARNAME = CQLWord.fromInternal("[ttl]");

  @Test
  void should_parse_mapped_mapping() {
    assertThat(
            new MappingInspector(
                    "fieldA=col1,fieldB=col2",
                    true,
                    MAPPED_ONLY,
                    INTERNAL_TIMESTAMP_VARNAME,
                    INTERNAL_TTL_VARNAME)
                .getExplicitVariables())
        .containsEntry(new MappedMappingField("fieldA"), CQLWord.fromCql("col1"))
        .containsEntry(new MappedMappingField("fieldB"), CQLWord.fromCql("col2"));
    assertThat(
            new MappingInspector(
                    "fieldA:col1,fieldB:col2",
                    true,
                    MAPPED_ONLY,
                    INTERNAL_TIMESTAMP_VARNAME,
                    INTERNAL_TTL_VARNAME)
                .getExplicitVariables())
        .containsEntry(new MappedMappingField("fieldA"), CQLWord.fromCql("col1"))
        .containsEntry(new MappedMappingField("fieldB"), CQLWord.fromCql("col2"));
    assertThat(
            new MappingInspector(
                    "  fieldA : col1 , fieldB : col2  ",
                    true,
                    MAPPED_ONLY,
                    INTERNAL_TIMESTAMP_VARNAME,
                    INTERNAL_TTL_VARNAME)
                .getExplicitVariables())
        .containsEntry(new MappedMappingField("fieldA"), CQLWord.fromCql("col1"))
        .containsEntry(new MappedMappingField("fieldB"), CQLWord.fromCql("col2"));
  }

  @Test
  void should_parse_simple_mapping_as_indexed_when_indexed_mapping_only_supported() {
    assertThat(
            new MappingInspector(
                    "col1,col2",
                    true,
                    INDEXED_ONLY,
                    INTERNAL_TIMESTAMP_VARNAME,
                    INTERNAL_TTL_VARNAME)
                .getExplicitVariables())
        .containsEntry(new IndexedMappingField(0), CQLWord.fromCql("col1"))
        .containsEntry(new IndexedMappingField(1), CQLWord.fromCql("col2"));
    assertThat(
            new MappingInspector(
                    "  col1  ,  col2  ",
                    true,
                    INDEXED_ONLY,
                    INTERNAL_TIMESTAMP_VARNAME,
                    INTERNAL_TTL_VARNAME)
                .getExplicitVariables())
        .containsEntry(new IndexedMappingField(0), CQLWord.fromCql("col1"))
        .containsEntry(new IndexedMappingField(1), CQLWord.fromCql("col2"));
  }

  @Test
  void should_parse_simple_mapping_as_mapped_when_mapped_mapping_only_supported() {
    assertThat(
            new MappingInspector(
                    "col1,col2",
                    true,
                    MAPPED_ONLY,
                    INTERNAL_TIMESTAMP_VARNAME,
                    INTERNAL_TTL_VARNAME)
                .getExplicitVariables())
        .containsEntry(new MappedMappingField("col1"), CQLWord.fromCql("col1"))
        .containsEntry(new MappedMappingField("col2"), CQLWord.fromCql("col2"));
    assertThat(
            new MappingInspector(
                    "  col1  ,  col2  ",
                    true,
                    MAPPED_ONLY,
                    INTERNAL_TIMESTAMP_VARNAME,
                    INTERNAL_TTL_VARNAME)
                .getExplicitVariables())
        .containsEntry(new MappedMappingField("col1"), CQLWord.fromCql("col1"))
        .containsEntry(new MappedMappingField("col2"), CQLWord.fromCql("col2"));
  }

  @Test
  void should_parse_simple_mapping_as_mapped_when_both_mapped_and_indexed_mappings_supported() {
    assertThat(
            new MappingInspector(
                    "col1,col2",
                    true,
                    MAPPED_OR_INDEXED,
                    INTERNAL_TIMESTAMP_VARNAME,
                    INTERNAL_TTL_VARNAME)
                .getExplicitVariables())
        .containsEntry(new MappedMappingField("col1"), CQLWord.fromCql("col1"))
        .containsEntry(new MappedMappingField("col2"), CQLWord.fromCql("col2"));
    assertThat(
            new MappingInspector(
                    "  col1  ,  col2  ",
                    true,
                    MAPPED_OR_INDEXED,
                    INTERNAL_TIMESTAMP_VARNAME,
                    INTERNAL_TTL_VARNAME)
                .getExplicitVariables())
        .containsEntry(new MappedMappingField("col1"), CQLWord.fromCql("col1"))
        .containsEntry(new MappedMappingField("col2"), CQLWord.fromCql("col2"));
  }

  @Test
  void should_parse_indexed_mapping_as_indexed_when_indexed_mapping_supported() {
    assertThat(
            new MappingInspector(
                    "0=col1,1=col2",
                    true,
                    INDEXED_ONLY,
                    INTERNAL_TIMESTAMP_VARNAME,
                    INTERNAL_TTL_VARNAME)
                .getExplicitVariables())
        .containsEntry(new IndexedMappingField(0), CQLWord.fromCql("col1"))
        .containsEntry(new IndexedMappingField(1), CQLWord.fromCql("col2"));
    assertThat(
            new MappingInspector(
                    "  0 = col1  , 1 = col2  ",
                    true,
                    INDEXED_ONLY,
                    INTERNAL_TIMESTAMP_VARNAME,
                    INTERNAL_TTL_VARNAME)
                .getExplicitVariables())
        .containsEntry(new IndexedMappingField(0), CQLWord.fromCql("col1"))
        .containsEntry(new IndexedMappingField(1), CQLWord.fromCql("col2"));
    assertThat(
            new MappingInspector(
                    "0=col1,1=col2",
                    true,
                    MAPPED_OR_INDEXED,
                    INTERNAL_TIMESTAMP_VARNAME,
                    INTERNAL_TTL_VARNAME)
                .getExplicitVariables())
        .containsEntry(new IndexedMappingField(0), CQLWord.fromCql("col1"))
        .containsEntry(new IndexedMappingField(1), CQLWord.fromCql("col2"));
    assertThat(
            new MappingInspector(
                    "  0 = col1  , 1 = col2  ",
                    true,
                    MAPPED_OR_INDEXED,
                    INTERNAL_TIMESTAMP_VARNAME,
                    INTERNAL_TTL_VARNAME)
                .getExplicitVariables())
        .containsEntry(new IndexedMappingField(0), CQLWord.fromCql("col1"))
        .containsEntry(new IndexedMappingField(1), CQLWord.fromCql("col2"));
  }

  @Test
  void should_parse_indexed_mapping_as_mapped_when_indexed_mapping_not_supported() {
    assertThat(
            new MappingInspector(
                    "0=col1,1=col2",
                    true,
                    MAPPED_ONLY,
                    INTERNAL_TIMESTAMP_VARNAME,
                    INTERNAL_TTL_VARNAME)
                .getExplicitVariables())
        .containsEntry(new MappedMappingField("0"), CQLWord.fromCql("col1"))
        .containsEntry(new MappedMappingField("1"), CQLWord.fromCql("col2"));
    assertThat(
            new MappingInspector(
                    "  0 = col1  , 1 =  col2  ",
                    true,
                    MAPPED_ONLY,
                    INTERNAL_TIMESTAMP_VARNAME,
                    INTERNAL_TTL_VARNAME)
                .getExplicitVariables())
        .containsEntry(new MappedMappingField("0"), CQLWord.fromCql("col1"))
        .containsEntry(new MappedMappingField("1"), CQLWord.fromCql("col2"));
  }

  @Test
  void should_parse_quoted_mapping() {
    assertThat(
            new MappingInspector(
                    "\"fieldA\"=\" \",\"\"\"fieldB\"\"\"=\"\"\"\"",
                    true,
                    MAPPED_ONLY,
                    INTERNAL_TIMESTAMP_VARNAME,
                    INTERNAL_TTL_VARNAME)
                .getExplicitVariables())
        .containsEntry(new MappedMappingField("fieldA"), CQLWord.fromInternal(" "))
        .containsEntry(new MappedMappingField("\"fieldB\""), CQLWord.fromInternal("\""));
    assertThat(
            new MappingInspector(
                    "\"fieldA\":\" \",\"\"\"fieldB\"\"\":\"\"\"\"",
                    true,
                    MAPPED_ONLY,
                    INTERNAL_TIMESTAMP_VARNAME,
                    INTERNAL_TTL_VARNAME)
                .getExplicitVariables())
        .containsEntry(new MappedMappingField("fieldA"), CQLWord.fromInternal(" "))
        .containsEntry(new MappedMappingField("\"fieldB\""), CQLWord.fromInternal("\""));
    assertThat(
            new MappingInspector(
                    " \"fieldA\" = \" \" , \"\"\"fieldB\"\"\" = \"\"\"\" ",
                    true,
                    MAPPED_ONLY,
                    INTERNAL_TIMESTAMP_VARNAME,
                    INTERNAL_TTL_VARNAME)
                .getExplicitVariables())
        .containsEntry(new MappedMappingField("fieldA"), CQLWord.fromInternal(" "))
        .containsEntry(new MappedMappingField("\"fieldB\""), CQLWord.fromInternal("\""));
  }

  @Test
  void should_parse_inferred_mapping_token() {
    MappingInspector inspector =
        new MappingInspector(
            " * = * , fieldA = col1 ",
            true,
            MAPPED_ONLY,
            INTERNAL_TIMESTAMP_VARNAME,
            INTERNAL_TTL_VARNAME);
    assertThat(inspector.getExplicitVariables())
        .containsEntry(new MappedMappingField("fieldA"), CQLWord.fromCql("col1"));
    assertThat(inspector.isInferring()).isTrue();
    assertThat(inspector.getExcludedVariables()).isEmpty();
  }

  @Test
  void should_parse_inferred_mapping_token_with_simple_exclusion() {
    MappingInspector inspector =
        new MappingInspector(
            "* = -c2", true, MAPPED_ONLY, INTERNAL_TIMESTAMP_VARNAME, INTERNAL_TTL_VARNAME);
    assertThat(inspector.getExplicitVariables()).isEmpty();
    assertThat(inspector.isInferring()).isTrue();
    assertThat(inspector.getExcludedVariables()).containsOnly(CQLWord.fromInternal("c2"));
    inspector =
        new MappingInspector(
            "* = -\"C2\"", true, MAPPED_ONLY, INTERNAL_TIMESTAMP_VARNAME, INTERNAL_TTL_VARNAME);
    assertThat(inspector.getExplicitVariables()).isEmpty();
    assertThat(inspector.isInferring()).isTrue();
    assertThat(inspector.getExcludedVariables()).containsOnly(CQLWord.fromInternal("C2"));
  }

  @Test
  void should_parse_inferred_mapping_token_with_complex_exclusion() {
    MappingInspector inspector =
        new MappingInspector(
            " * = [-c2, -c3]  ",
            true,
            MAPPED_ONLY,
            INTERNAL_TIMESTAMP_VARNAME,
            INTERNAL_TTL_VARNAME);
    assertThat(inspector.getExplicitVariables()).isEmpty();
    assertThat(inspector.isInferring()).isTrue();
    assertThat(inspector.getExcludedVariables())
        .containsOnly(CQLWord.fromInternal("c2"), CQLWord.fromInternal("c3"));
    inspector =
        new MappingInspector(
            " * = [ - \"C2\", - \"C3\" ]  ",
            true,
            MAPPED_ONLY,
            INTERNAL_TIMESTAMP_VARNAME,
            INTERNAL_TTL_VARNAME);
    assertThat(inspector.getExplicitVariables()).isEmpty();
    assertThat(inspector.isInferring()).isTrue();
    assertThat(inspector.getExcludedVariables())
        .containsOnly(CQLWord.fromInternal("C2"), CQLWord.fromInternal("C3"));
  }

  @Test
  void should_reorder_indexed_mapping_with_explicit_indices() {
    MappingInspector inspector =
        new MappingInspector(
            " 1 = A, 0 = B  ",
            true,
            INDEXED_ONLY,
            INTERNAL_TIMESTAMP_VARNAME,
            INTERNAL_TTL_VARNAME);
    assertThat(inspector.getExplicitVariables().keySet())
        .containsExactly(new IndexedMappingField(0), new IndexedMappingField(1));
  }

  @Test
  void should_detect_ttl_and_timestamp_vars() {
    MappingInspector inspector =
        new MappingInspector(
            " a = __ttl, b = __timestamp  ",
            true,
            MAPPED_ONLY,
            INTERNAL_TIMESTAMP_VARNAME,
            INTERNAL_TTL_VARNAME);
    assertThat(inspector.getExplicitVariables().values())
        .containsExactly(INTERNAL_TTL_VARNAME, INTERNAL_TIMESTAMP_VARNAME);
    assertThat(inspector.getWriteTimeVariables()).containsExactly(INTERNAL_TIMESTAMP_VARNAME);
    inspector =
        new MappingInspector(
            " a = \"[ttl]\", b = \"[timestamp]\"  ",
            true,
            MAPPED_ONLY,
            INTERNAL_TIMESTAMP_VARNAME,
            INTERNAL_TTL_VARNAME);
    assertThat(inspector.getExplicitVariables().values())
        .containsExactly(INTERNAL_TTL_VARNAME, INTERNAL_TIMESTAMP_VARNAME);
    assertThat(inspector.getWriteTimeVariables()).containsExactly(INTERNAL_TIMESTAMP_VARNAME);
    inspector =
        new MappingInspector(
            " a = \"MyTTL\", b = \"MyTimestamp\"  ",
            true,
            MAPPED_ONLY,
            CQLWord.fromInternal("MyTimestamp"),
            CQLWord.fromInternal("MyTTL"));
    assertThat(inspector.getExplicitVariables().values())
        .containsExactly(CQLWord.fromInternal("MyTTL"), CQLWord.fromInternal("MyTimestamp"));
    assertThat(inspector.getWriteTimeVariables())
        .containsExactly(CQLWord.fromInternal("MyTimestamp"));
  }

  @Test
  void should_detect_writetime_function_in_variable() {
    MappingInspector inspector =
        new MappingInspector(
            " a = WrItEtImE(\"My Col 2\")  ",
            true,
            MAPPED_ONLY,
            INTERNAL_TIMESTAMP_VARNAME,
            INTERNAL_TTL_VARNAME);
    assertThat(inspector.getWriteTimeVariables())
        .containsExactly(
            new FunctionCall(
                null, CQLWord.fromInternal("writetime"), CQLWord.fromInternal("My Col 2")));
  }

  @Test
  void should_detect_function_in_field() {
    MappingInspector inspector =
        new MappingInspector(
            " now() = col1, max(col1, col2) = col2, plus(col1, 42) = col3  ",
            true,
            MAPPED_ONLY,
            INTERNAL_TIMESTAMP_VARNAME,
            INTERNAL_TTL_VARNAME);
    assertThat(inspector.getExplicitVariables())
        .containsEntry(new FunctionCall(null, CQLWord.fromInternal("now")), CQLWord.fromCql("col1"))
        .containsEntry(
            new FunctionCall(
                null,
                CQLWord.fromInternal("max"),
                CQLWord.fromCql("col1"),
                CQLWord.fromCql("col2")),
            CQLWord.fromCql("col2"))
        .containsEntry(
            new FunctionCall(
                null, CQLWord.fromInternal("plus"), CQLWord.fromCql("col1"), new CQLLiteral("42")),
            CQLWord.fromCql("col3"));
  }

  @Test
  void should_detect_function_in_field_case_sensitive() {
    MappingInspector inspector =
        new MappingInspector(
            "\"MAX\"(col1, col2) = col2  ",
            true,
            MAPPED_ONLY,
            INTERNAL_TIMESTAMP_VARNAME,
            INTERNAL_TTL_VARNAME);
    assertThat(inspector.getExplicitVariables())
        .containsEntry(
            new FunctionCall(
                null,
                CQLWord.fromInternal("MAX"),
                CQLWord.fromCql("col1"),
                CQLWord.fromCql("col2")),
            CQLWord.fromCql("col2"));
  }

  @Test
  void should_detect_function_in_variable() {
    MappingInspector inspector =
        new MappingInspector(
            " fieldA = now(), fieldB = max(col1, col2), fieldC = ttl(a), fieldD = writetime(a)",
            true,
            MAPPED_ONLY,
            INTERNAL_TIMESTAMP_VARNAME,
            INTERNAL_TTL_VARNAME);
    assertThat(inspector.getExplicitVariables())
        .containsEntry(
            new MappedMappingField("fieldA"), new FunctionCall(null, CQLWord.fromInternal("now")))
        .containsEntry(
            new MappedMappingField("fieldB"),
            new FunctionCall(
                null,
                CQLWord.fromInternal("max"),
                CQLWord.fromCql("col1"),
                CQLWord.fromCql("col2")))
        .containsEntry(
            new MappedMappingField("fieldC"),
            new FunctionCall(null, CQLWord.fromInternal("ttl"), CQLWord.fromInternal("a")))
        .containsEntry(
            new MappedMappingField("fieldD"),
            new FunctionCall(null, CQLWord.fromInternal("writetime"), CQLWord.fromInternal("a")));
    assertThat(inspector.getWriteTimeVariables())
        .containsExactly(
            new FunctionCall(null, CQLWord.fromInternal("writetime"), CQLWord.fromInternal("a")));
  }

  @Test
  void should_detect_function_in_variable_case_sensitive() {
    MappingInspector inspector =
        new MappingInspector(
            "fieldA = \"MAX\"(\"My Col 1\", \"My Col 2\")",
            true,
            MAPPED_ONLY,
            INTERNAL_TIMESTAMP_VARNAME,
            INTERNAL_TTL_VARNAME);
    assertThat(inspector.getExplicitVariables())
        .containsEntry(
            new MappedMappingField("fieldA"),
            new FunctionCall(
                null,
                CQLWord.fromInternal("MAX"),
                CQLWord.fromInternal("My Col 1"),
                CQLWord.fromInternal("My Col 2")));
  }

  @Test
  void should_reject_function_in_simple_entry_when_loading() {
    assertThatThrownBy(
            () ->
                new MappingInspector(
                    "a,b,c,now()",
                    true,
                    MAPPED_ONLY,
                    INTERNAL_TIMESTAMP_VARNAME,
                    INTERNAL_TTL_VARNAME))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("simple entries cannot contain function calls when loading");
  }

  @Test
  void should_accept_function_in_simple_entry_when_unloading() {
    MappingInspector inspector =
        new MappingInspector(
            "a,b,c,now()", false, MAPPED_ONLY, INTERNAL_TIMESTAMP_VARNAME, INTERNAL_TTL_VARNAME);
    assertThat(inspector.getExplicitVariables())
        .containsEntry(
            new MappedMappingField("now()"), new FunctionCall(null, CQLWord.fromInternal("now")));
    inspector =
        new MappingInspector(
            "a,b,c,now()", false, INDEXED_ONLY, INTERNAL_TIMESTAMP_VARNAME, INTERNAL_TTL_VARNAME);
    assertThat(inspector.getExplicitVariables())
        .containsEntry(
            new IndexedMappingField(3), new FunctionCall(null, CQLWord.fromInternal("now")));
  }

  @Test
  void should_error_out_if_syntax_error() {
    assertThatThrownBy(
            () ->
                new MappingInspector(
                    " { a = b, c = d ",
                    true,
                    MAPPED_ONLY,
                    INTERNAL_TIMESTAMP_VARNAME,
                    INTERNAL_TTL_VARNAME))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid schema.mapping: mapping could not be parsed");
    assertThatThrownBy(
            () ->
                new MappingInspector(
                    " a = b c = d ",
                    true,
                    MAPPED_ONLY,
                    INTERNAL_TIMESTAMP_VARNAME,
                    INTERNAL_TTL_VARNAME))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid schema.mapping: mapping could not be parsed");
  }

  @Test
  void should_error_out_inferred_token_appears_twice() {
    assertThatThrownBy(
            () ->
                new MappingInspector(
                    " *=*, *=-c2",
                    true,
                    MAPPED_ONLY,
                    INTERNAL_TIMESTAMP_VARNAME,
                    INTERNAL_TTL_VARNAME))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(
            "Invalid schema.mapping: inferred mapping entry (* = *) can be supplied at most once");
  }

  @Test
  void should_accept_same_field_mapped_twice_when_loading() {
    MappingInspector inspector =
        new MappingInspector(
            " a = c1, a = c2", true, MAPPED_ONLY, INTERNAL_TIMESTAMP_VARNAME, INTERNAL_TTL_VARNAME);
    assertThat(inspector.getExplicitVariables())
        .containsEntry(new MappedMappingField("a"), CQLWord.fromInternal("c1"))
        .containsEntry(new MappedMappingField("a"), CQLWord.fromInternal("c2"));
  }

  @Test
  void should_error_out_same_variable_mapped_twice_when_loading() {
    assertThatThrownBy(
            () ->
                new MappingInspector(
                    " a = c1, b = c1, c = c2, d = c2",
                    true,
                    MAPPED_ONLY,
                    INTERNAL_TIMESTAMP_VARNAME,
                    INTERNAL_TTL_VARNAME))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(
            "Invalid schema.mapping: the following variables are mapped to more than one field: c1, c2");
  }

  @Test
  void should_accept_same_variable_mapped_twice_when_unloading() {
    MappingInspector inspector =
        new MappingInspector(
            " a = c1, b = c1",
            false,
            MAPPED_ONLY,
            INTERNAL_TIMESTAMP_VARNAME,
            INTERNAL_TTL_VARNAME);
    assertThat(inspector.getExplicitVariables())
        .containsEntry(new MappedMappingField("a"), CQLWord.fromInternal("c1"))
        .containsEntry(new MappedMappingField("b"), CQLWord.fromInternal("c1"));
  }

  @Test
  void should_error_out_same_field_mapped_twice_when_unloading() {
    assertThatThrownBy(
            () ->
                new MappingInspector(
                    " a = c1, a = c2, b = c3, b = c4",
                    false,
                    MAPPED_ONLY,
                    INTERNAL_TIMESTAMP_VARNAME,
                    INTERNAL_TTL_VARNAME))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(
            "Invalid schema.mapping: the following fields are mapped to more than one variable: a, b");
    assertThatThrownBy(
            () ->
                new MappingInspector(
                    " a = c1, a = c2, b = c3, b = c4",
                    false,
                    MAPPED_ONLY,
                    INTERNAL_TIMESTAMP_VARNAME,
                    INTERNAL_TTL_VARNAME))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(
            "Invalid schema.mapping: the following fields are mapped to more than one variable: a, b");
  }

  @Test
  void should_accept_duplicate_mapping_when_loading_and_unloading() {
    MappingInspector inspector =
        new MappingInspector(
            " a = c1, a = c1", true, MAPPED_ONLY, INTERNAL_TIMESTAMP_VARNAME, INTERNAL_TTL_VARNAME);
    assertThat(inspector.getExplicitVariables())
        .hasSize(1)
        .containsEntry(new MappedMappingField("a"), CQLWord.fromInternal("c1"));
    inspector =
        new MappingInspector(
            " a = c1, a = c1",
            false,
            MAPPED_ONLY,
            INTERNAL_TIMESTAMP_VARNAME,
            INTERNAL_TTL_VARNAME);
    assertThat(inspector.getExplicitVariables())
        .hasSize(1)
        .containsEntry(new MappedMappingField("a"), CQLWord.fromInternal("c1"));
  }

  @Test
  void should_error_out_if_mapped_mapping_but_preference_is_indexed_only() {
    assertThatThrownBy(
            () ->
                new MappingInspector(
                    "* = *, a = b, now() = d",
                    true,
                    INDEXED_ONLY,
                    INTERNAL_TIMESTAMP_VARNAME,
                    INTERNAL_TTL_VARNAME))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(
            "Schema mapping contains named fields, but connector only supports indexed fields");
  }

  @Test
  void should_detect_using_timestamp() {
    MappingInspector inspector =
        new MappingInspector(
            "f1 = c1, f2 = __timestamp",
            true,
            MAPPED_ONLY,
            INTERNAL_TIMESTAMP_VARNAME,
            INTERNAL_TTL_VARNAME);
    assertThat(inspector.hasUsingTimestamp()).isTrue();
    inspector =
        new MappingInspector(
            "f1 = c1, f2 = __ttl",
            true,
            MAPPED_ONLY,
            INTERNAL_TIMESTAMP_VARNAME,
            INTERNAL_TTL_VARNAME);
    assertThat(inspector.hasUsingTimestamp()).isFalse();
    inspector =
        new MappingInspector(
            "f1 = c1, f2 = \"[timestamp]\"",
            true,
            MAPPED_ONLY,
            INTERNAL_TIMESTAMP_VARNAME,
            INTERNAL_TTL_VARNAME);
    assertThat(inspector.hasUsingTimestamp()).isTrue();
    inspector =
        new MappingInspector(
            "f1 = c1, f2 = c2",
            true,
            MAPPED_ONLY,
            CQLWord.fromInternal("c2"),
            INTERNAL_TTL_VARNAME);
    assertThat(inspector.hasUsingTimestamp()).isTrue();
  }

  @Test
  void should_detect_using_ttl() {
    MappingInspector inspector =
        new MappingInspector(
            "f1 = c1, f2 = __ttl",
            true,
            MAPPED_ONLY,
            INTERNAL_TIMESTAMP_VARNAME,
            INTERNAL_TTL_VARNAME);
    assertThat(inspector.hasUsingTTL()).isTrue();
    inspector =
        new MappingInspector(
            "f1 = c1, f2 = __timestamp",
            true,
            MAPPED_ONLY,
            INTERNAL_TIMESTAMP_VARNAME,
            INTERNAL_TTL_VARNAME);
    assertThat(inspector.hasUsingTTL()).isFalse();
    inspector =
        new MappingInspector(
            "f1 = c1, f2 = \"[ttl]\"",
            true,
            MAPPED_ONLY,
            INTERNAL_TIMESTAMP_VARNAME,
            INTERNAL_TTL_VARNAME);
    assertThat(inspector.hasUsingTTL()).isTrue();
    inspector =
        new MappingInspector(
            "f1 = c1, f2 = c2",
            true,
            MAPPED_ONLY,
            INTERNAL_TIMESTAMP_VARNAME,
            CQLWord.fromInternal("c2"));
    assertThat(inspector.hasUsingTTL()).isTrue();
  }

  @Test
  void should_error_when_mapped_ttl_but_no_using_ttl_variable() {
    assertThatThrownBy(
            () -> new MappingInspector("f1 = c1, f2 = __ttl", true, MAPPED_ONLY, null, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "Invalid mapping: __ttl variable is not allowed when schema.query does not contain a USING TTL clause");
  }

  @Test
  void should_error_when_mapped_timestamp_but_no_using_timestamp_variable() {
    assertThatThrownBy(
            () -> new MappingInspector("f1 = c1, f2 = __timestamp", true, MAPPED_ONLY, null, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "Invalid mapping: __timestamp variable is not allowed when schema.query does not contain a USING TIMESTAMP clause");
  }

  @Test
  void should_detect_qualified_function_name() {
    MappingInspector inspector =
        new MappingInspector("\"MyKs1\".\"NOW\"() = c1", true, MAPPED_ONLY, null, null);
    assertThat(inspector.getExplicitVariables())
        .containsEntry(
            new FunctionCall(CQLWord.fromInternal("MyKs1"), CQLWord.fromInternal("NOW")),
            CQLWord.fromInternal("c1"));
    inspector =
        new MappingInspector(
            "f1 = \"MyKs1\".\"PLUS\"(\"C1\",\"C2\")", false, MAPPED_ONLY, null, null);
    assertThat(inspector.getExplicitVariables())
        .containsEntry(
            new MappedMappingField("f1"),
            new FunctionCall(
                CQLWord.fromInternal("MyKs1"),
                CQLWord.fromInternal("PLUS"),
                CQLWord.fromInternal("C1"),
                CQLWord.fromInternal("C2")));
  }
}
