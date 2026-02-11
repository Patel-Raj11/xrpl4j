package org.xrpl.xrpl4j.model.transactions.json;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.model.AbstractJsonTest;
import org.xrpl.xrpl4j.model.flags.SponsorshipSetFlags;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.SponsorshipSet;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

/**
 * Unit tests for {@link SponsorshipSet} JSON serialization.
 */
public class SponsorshipSetJsonTest extends AbstractJsonTest {

  @Test
  public void testSponsorshipSetJson() throws JsonProcessingException, JSONException {
    SponsorshipSet sponsorshipSet = SponsorshipSet.builder()
      .account(Address.of("rN7n7otQDd6FczFgLdlqtyMVrn3HMfXpf"))
      .sponsee(Address.of("rfkDkFai4jUfCvAJiZ5Vm7XvvWjYvDqeYo"))
      .feeAmount(XrpCurrencyAmount.ofDrops(1000000))
      .maxFee(XrpCurrencyAmount.ofDrops(1000))
      .reserveCount(UnsignedInteger.valueOf(5))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.valueOf(42))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("ED87987410480E90474F7A02E0DA0CE4E6ABC8A1377864026A1FEE2718688B0B84")
      )
      .build();

    String json = "{" +
      "  \"TransactionType\": \"SponsorshipSet\"," +
      "  \"Account\": \"rN7n7otQDd6FczFgLdlqtyMVrn3HMfXpf\"," +
      "  \"Sponsee\": \"rfkDkFai4jUfCvAJiZ5Vm7XvvWjYvDqeYo\"," +
      "  \"FeeAmount\": \"1000000\"," +
      "  \"MaxFee\": \"1000\"," +
      "  \"ReserveCount\": 5," +
      "  \"Fee\": \"12\"," +
      "  \"Sequence\": 42," +
      "  \"SigningPubKey\": \"ED87987410480E90474F7A02E0DA0CE4E6ABC8A1377864026A1FEE2718688B0B84\"" +
      "}";

    assertCanSerializeAndDeserialize(sponsorshipSet, json);
  }

  @Test
  public void testSponsorshipSetWithSponsorJson() throws JsonProcessingException, JSONException {
    SponsorshipSet sponsorshipSet = SponsorshipSet.builder()
      .account(Address.of("rfkDkFai4jUfCvAJiZ5Vm7XvvWjYvDqeYo"))
      .sponsor(Address.of("rN7n7otQDd6FczFgLdlqtyMVrn3HMfXpf"))
      .flags(SponsorshipSetFlags.DELETE_OBJECT)
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.valueOf(43))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("ED87987410480E90474F7A02E0DA0CE4E6ABC8A1377864026A1FEE2718688B0B84")
      )
      .build();

    String json = "{" +
      "  \"TransactionType\": \"SponsorshipSet\"," +
      "  \"Account\": \"rfkDkFai4jUfCvAJiZ5Vm7XvvWjYvDqeYo\"," +
      "  \"Sponsor\": \"rN7n7otQDd6FczFgLdlqtyMVrn3HMfXpf\"," +
      "  \"Fee\": \"12\"," +
      "  \"Flags\": 1048576," +
      "  \"Sequence\": 43," +
      "  \"SigningPubKey\": \"ED87987410480E90474F7A02E0DA0CE4E6ABC8A1377864026A1FEE2718688B0B84\"" +
      "}";

    assertCanSerializeAndDeserialize(sponsorshipSet, json);
  }

  @Test
  public void testSponsorshipSetWithFlagsJson() throws JsonProcessingException, JSONException {
    SponsorshipSet sponsorshipSet = SponsorshipSet.builder()
      .account(Address.of("rN7n7otQDd6FczFgLdlqtyMVrn3HMfXpf"))
      .sponsee(Address.of("rfkDkFai4jUfCvAJiZ5Vm7XvvWjYvDqeYo"))
      .feeAmount(XrpCurrencyAmount.ofDrops(1000000))
      .flags(SponsorshipSetFlags.builder()
        .tfSponsorshipSetRequireSignForFee(true)
        .tfSponsorshipSetRequireSignForReserve(true)
        .build())
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.valueOf(44))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("ED87987410480E90474F7A02E0DA0CE4E6ABC8A1377864026A1FEE2718688B0B84")
      )
      .build();

    // Flags = FULLY_CANONICAL_SIG (0x80000000) | SET_REQUIRE_SIGN_FOR_FEE (0x00010000) | SET_REQUIRE_SIGN_FOR_RESERVE (0x00040000)
    // = 2147811328
    String json = "{" +
      "  \"TransactionType\": \"SponsorshipSet\"," +
      "  \"Account\": \"rN7n7otQDd6FczFgLdlqtyMVrn3HMfXpf\"," +
      "  \"Sponsee\": \"rfkDkFai4jUfCvAJiZ5Vm7XvvWjYvDqeYo\"," +
      "  \"FeeAmount\": \"1000000\"," +
      "  \"Fee\": \"12\"," +
      "  \"Flags\": 2147811328," +
      "  \"Sequence\": 44," +
      "  \"SigningPubKey\": \"ED87987410480E90474F7A02E0DA0CE4E6ABC8A1377864026A1FEE2718688B0B84\"" +
      "}";

    assertCanSerializeAndDeserialize(sponsorshipSet, json);
  }
}

