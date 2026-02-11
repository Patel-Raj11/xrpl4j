package org.xrpl.xrpl4j.model.flags;

/*-
 * ========================LICENSE_START=================================
 * xrpl4j :: core
 * %%
 * Copyright (C) 2020 - 2023 XRPL Foundation and its contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link SponsorshipSetFlags}.
 */
public class SponsorshipSetFlagsTest extends AbstractFlagsTest {

  @Test
  void testEmpty() {
    SponsorshipSetFlags flags = SponsorshipSetFlags.empty();
    assertThat(flags.isEmpty()).isTrue();
    assertThat(flags.tfSponsorshipSetRequireSignForFee()).isFalse();
    assertThat(flags.tfSponsorshipClearRequireSignForFee()).isFalse();
    assertThat(flags.tfSponsorshipSetRequireSignForReserve()).isFalse();
    assertThat(flags.tfSponsorshipClearRequireSignForReserve()).isFalse();
    assertThat(flags.tfDeleteObject()).isFalse();
    assertThat(flags.getValue()).isEqualTo(0L);
  }

  @Test
  void testSetRequireSignForFee() {
    SponsorshipSetFlags flags = SponsorshipSetFlags.SET_REQUIRE_SIGN_FOR_FEE;
    assertThat(flags.isEmpty()).isFalse();
    assertThat(flags.tfSponsorshipSetRequireSignForFee()).isTrue();
    assertThat(flags.tfSponsorshipClearRequireSignForFee()).isFalse();
    assertThat(flags.getValue()).isEqualTo(0x00010000L);
  }

  @Test
  void testClearRequireSignForFee() {
    SponsorshipSetFlags flags = SponsorshipSetFlags.CLEAR_REQUIRE_SIGN_FOR_FEE;
    assertThat(flags.isEmpty()).isFalse();
    assertThat(flags.tfSponsorshipSetRequireSignForFee()).isFalse();
    assertThat(flags.tfSponsorshipClearRequireSignForFee()).isTrue();
    assertThat(flags.getValue()).isEqualTo(0x00020000L);
  }

  @Test
  void testSetRequireSignForReserve() {
    SponsorshipSetFlags flags = SponsorshipSetFlags.SET_REQUIRE_SIGN_FOR_RESERVE;
    assertThat(flags.isEmpty()).isFalse();
    assertThat(flags.tfSponsorshipSetRequireSignForReserve()).isTrue();
    assertThat(flags.tfSponsorshipClearRequireSignForReserve()).isFalse();
    assertThat(flags.getValue()).isEqualTo(0x00040000L);
  }

  @Test
  void testClearRequireSignForReserve() {
    SponsorshipSetFlags flags = SponsorshipSetFlags.CLEAR_REQUIRE_SIGN_FOR_RESERVE;
    assertThat(flags.isEmpty()).isFalse();
    assertThat(flags.tfSponsorshipSetRequireSignForReserve()).isFalse();
    assertThat(flags.tfSponsorshipClearRequireSignForReserve()).isTrue();
    assertThat(flags.getValue()).isEqualTo(0x00080000L);
  }

  @Test
  void testDeleteObject() {
    SponsorshipSetFlags flags = SponsorshipSetFlags.DELETE_OBJECT;
    assertThat(flags.isEmpty()).isFalse();
    assertThat(flags.tfDeleteObject()).isTrue();
    assertThat(flags.getValue()).isEqualTo(0x00100000L);
  }

  @Test
  void testBuilder() {
    SponsorshipSetFlags flags = SponsorshipSetFlags.builder()
      .tfSponsorshipSetRequireSignForFee(true)
      .tfSponsorshipSetRequireSignForReserve(true)
      .build();

    assertThat(flags.tfSponsorshipSetRequireSignForFee()).isTrue();
    assertThat(flags.tfSponsorshipSetRequireSignForReserve()).isTrue();
    assertThat(flags.tfSponsorshipClearRequireSignForFee()).isFalse();
    assertThat(flags.tfSponsorshipClearRequireSignForReserve()).isFalse();
    assertThat(flags.tfDeleteObject()).isFalse();
    // Builder adds FULLY_CANONICAL_SIG (0x80000000L) by default
    assertThat(flags.getValue()).isEqualTo(0x80000000L | 0x00010000L | 0x00040000L);
  }

  @Test
  void testJson() throws JSONException, JsonProcessingException {
    TransactionFlagsWrapper wrapper = TransactionFlagsWrapper.of(SponsorshipSetFlags.DELETE_OBJECT);
    String json = String.format("{" +
      "  \"flags\": %s" +
      "}", SponsorshipSetFlags.DELETE_OBJECT.getValue());

    assertCanSerializeAndDeserialize(wrapper, json);
  }
}

