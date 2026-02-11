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
 * Unit tests for {@link SponsorFlags}.
 */
public class SponsorFlagsTest extends AbstractFlagsTest {

  @Test
  void testUnset() {
    SponsorFlags flags = SponsorFlags.UNSET;
    assertThat(flags.isEmpty()).isFalse();
    assertThat(flags.tfSponsorFee()).isFalse();
    assertThat(flags.tfSponsorReserve()).isFalse();
    assertThat(flags.getValue()).isEqualTo(0L);
  }

  @Test
  void testSponsorFee() {
    SponsorFlags flags = SponsorFlags.SPONSOR_FEE;
    assertThat(flags.isEmpty()).isFalse();
    assertThat(flags.tfSponsorFee()).isTrue();
    assertThat(flags.tfSponsorReserve()).isFalse();
    assertThat(flags.getValue()).isEqualTo(0x00000001L);
  }

  @Test
  void testSponsorReserve() {
    SponsorFlags flags = SponsorFlags.SPONSOR_RESERVE;
    assertThat(flags.isEmpty()).isFalse();
    assertThat(flags.tfSponsorFee()).isFalse();
    assertThat(flags.tfSponsorReserve()).isTrue();
    assertThat(flags.getValue()).isEqualTo(0x00000002L);
  }

  @Test
  void testBothFlags() {
    SponsorFlags flags = SponsorFlags.of(0x00000001L | 0x00000002L);
    assertThat(flags.isEmpty()).isFalse();
    assertThat(flags.tfSponsorFee()).isTrue();
    assertThat(flags.tfSponsorReserve()).isTrue();
    assertThat(flags.getValue()).isEqualTo(0x00000003L);
  }

  @Test
  void testJson() throws JSONException, JsonProcessingException {
    FlagsWrapper wrapper = FlagsWrapper.of(SponsorFlags.SPONSOR_FEE);
    String json = String.format("{" +
      "  \"flags\": %s" +
      "}", SponsorFlags.SPONSOR_FEE.getValue());

    assertCanSerializeAndDeserialize(wrapper, json);
  }
}

