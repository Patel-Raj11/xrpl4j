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
 * Unit tests for {@link SponsorshipFlags}.
 */
public class SponsorshipFlagsTest extends AbstractFlagsTest {

  @Test
  void testUnset() {
    SponsorshipFlags flags = SponsorshipFlags.UNSET;
    assertThat(flags.isEmpty()).isFalse();
    assertThat(flags.lsfSponsorshipRequireSignForFee()).isFalse();
    assertThat(flags.lsfSponsorshipRequireSignForReserve()).isFalse();
    assertThat(flags.getValue()).isEqualTo(0L);
  }

  @Test
  void testRequireSignForFee() {
    SponsorshipFlags flags = SponsorshipFlags.REQUIRE_SIGN_FOR_FEE;
    assertThat(flags.isEmpty()).isFalse();
    assertThat(flags.lsfSponsorshipRequireSignForFee()).isTrue();
    assertThat(flags.lsfSponsorshipRequireSignForReserve()).isFalse();
    assertThat(flags.getValue()).isEqualTo(0x00010000L);
  }

  @Test
  void testRequireSignForReserve() {
    SponsorshipFlags flags = SponsorshipFlags.REQUIRE_SIGN_FOR_RESERVE;
    assertThat(flags.isEmpty()).isFalse();
    assertThat(flags.lsfSponsorshipRequireSignForFee()).isFalse();
    assertThat(flags.lsfSponsorshipRequireSignForReserve()).isTrue();
    assertThat(flags.getValue()).isEqualTo(0x00020000L);
  }

  @Test
  void testBothFlags() {
    SponsorshipFlags flags = SponsorshipFlags.of(0x00010000L | 0x00020000L);
    assertThat(flags.isEmpty()).isFalse();
    assertThat(flags.lsfSponsorshipRequireSignForFee()).isTrue();
    assertThat(flags.lsfSponsorshipRequireSignForReserve()).isTrue();
    assertThat(flags.getValue()).isEqualTo(0x00030000L);
  }

  @Test
  void testJson() throws JSONException, JsonProcessingException {
    FlagsWrapper wrapper = FlagsWrapper.of(SponsorshipFlags.REQUIRE_SIGN_FOR_FEE);
    String json = String.format("{" +
      "  \"flags\": %s" +
      "}", SponsorshipFlags.REQUIRE_SIGN_FOR_FEE.getValue());

    assertCanSerializeAndDeserialize(wrapper, json);
  }
}

