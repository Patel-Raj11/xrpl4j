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

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.model.AbstractJsonTest;
import org.xrpl.xrpl4j.model.flags.SponsorFlags;
import org.xrpl.xrpl4j.crypto.signing.Signature;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.Hash256;
import org.xrpl.xrpl4j.model.transactions.SponsorSignature;
import org.xrpl.xrpl4j.model.transactions.SponsorshipTransfer;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

/**
 * Unit tests for {@link SponsorshipTransfer} JSON serialization.
 */
public class SponsorshipTransferJsonTest extends AbstractJsonTest {

  @Test
  public void testSponsorshipTransferJson() throws JsonProcessingException, JSONException {
    SponsorshipTransfer sponsorshipTransfer = SponsorshipTransfer.builder()
      .account(Address.of("rfkDkFai4jUfCvAJiZ5Vm7XvvWjYvDqeYo"))
      .objectId(Hash256.of("13F1A9B5C2D3E4F613F1A9B5C2D3E4F613F1A9B5C2D3E4F613F1A9B5C2D3E4F6"))
      .sponsor(Address.of("rNEWSponsor3LNcTz8JF2oJC6qaww6RZ7Lw"))
      .sponsorFlags(SponsorFlags.SPONSOR_RESERVE)
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.valueOf(43))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("ED87987410480E90474F7A02E0DA0CE4E6ABC8A1377864026A1FEE2718688B0B84")
      )
      .build();

    String json = "{" +
      "  \"TransactionType\": \"SponsorshipTransfer\"," +
      "  \"Account\": \"rfkDkFai4jUfCvAJiZ5Vm7XvvWjYvDqeYo\"," +
      "  \"ObjectID\": \"13F1A9B5C2D3E4F613F1A9B5C2D3E4F613F1A9B5C2D3E4F613F1A9B5C2D3E4F6\"," +
      "  \"Sponsor\": \"rNEWSponsor3LNcTz8JF2oJC6qaww6RZ7Lw\"," +
      "  \"SponsorFlags\": 2," +
      "  \"Fee\": \"12\"," +
      "  \"Sequence\": 43," +
      "  \"SigningPubKey\": \"ED87987410480E90474F7A02E0DA0CE4E6ABC8A1377864026A1FEE2718688B0B84\"" +
      "}";

    assertCanSerializeAndDeserialize(sponsorshipTransfer, json);
  }

  @Test
  public void testSponsorshipTransferWithoutObjectIdJson() throws JsonProcessingException, JSONException {
    SponsorshipTransfer sponsorshipTransfer = SponsorshipTransfer.builder()
      .account(Address.of("rfkDkFai4jUfCvAJiZ5Vm7XvvWjYvDqeYo"))
      .sponsor(Address.of("rNEWSponsor3LNcTz8JF2oJC6qaww6RZ7Lw"))
      .sponsorFlags(SponsorFlags.SPONSOR_RESERVE)
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.valueOf(44))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("ED87987410480E90474F7A02E0DA0CE4E6ABC8A1377864026A1FEE2718688B0B84")
      )
      .build();

    String json = "{" +
      "  \"TransactionType\": \"SponsorshipTransfer\"," +
      "  \"Account\": \"rfkDkFai4jUfCvAJiZ5Vm7XvvWjYvDqeYo\"," +
      "  \"Sponsor\": \"rNEWSponsor3LNcTz8JF2oJC6qaww6RZ7Lw\"," +
      "  \"SponsorFlags\": 2," +
      "  \"Fee\": \"12\"," +
      "  \"Sequence\": 44," +
      "  \"SigningPubKey\": \"ED87987410480E90474F7A02E0DA0CE4E6ABC8A1377864026A1FEE2718688B0B84\"" +
      "}";

    assertCanSerializeAndDeserialize(sponsorshipTransfer, json);
  }

  @Test
  public void testSponsorshipTransferDissolvingJson() throws JsonProcessingException, JSONException {
    // Dissolving sponsorship - no Sponsor field
    SponsorshipTransfer sponsorshipTransfer = SponsorshipTransfer.builder()
      .account(Address.of("rfkDkFai4jUfCvAJiZ5Vm7XvvWjYvDqeYo"))
      .objectId(Hash256.of("13F1A9B5C2D3E4F613F1A9B5C2D3E4F613F1A9B5C2D3E4F613F1A9B5C2D3E4F6"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.valueOf(45))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("ED87987410480E90474F7A02E0DA0CE4E6ABC8A1377864026A1FEE2718688B0B84")
      )
      .build();

    String json = "{" +
      "  \"TransactionType\": \"SponsorshipTransfer\"," +
      "  \"Account\": \"rfkDkFai4jUfCvAJiZ5Vm7XvvWjYvDqeYo\"," +
      "  \"ObjectID\": \"13F1A9B5C2D3E4F613F1A9B5C2D3E4F613F1A9B5C2D3E4F613F1A9B5C2D3E4F6\"," +
      "  \"Fee\": \"12\"," +
      "  \"Sequence\": 45," +
      "  \"SigningPubKey\": \"ED87987410480E90474F7A02E0DA0CE4E6ABC8A1377864026A1FEE2718688B0B84\"" +
      "}";

    assertCanSerializeAndDeserialize(sponsorshipTransfer, json);
  }

  @Test
  public void testSponsorWithoutSponsorFlagsThrows() {
    assertThatThrownBy(() -> SponsorshipTransfer.builder()
      .account(Address.of("rfkDkFai4jUfCvAJiZ5Vm7XvvWjYvDqeYo"))
      .objectId(Hash256.of("13F1A9B5C2D3E4F613F1A9B5C2D3E4F613F1A9B5C2D3E4F613F1A9B5C2D3E4F6"))
      .sponsor(Address.of("rNEWSponsor3LNcTz8JF2oJC6qaww6RZ7Lw"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.valueOf(43))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("ED87987410480E90474F7A02E0DA0CE4E6ABC8A1377864026A1FEE2718688B0B84")
      )
      .build()
    )
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("SponsorFlags must be specified when Sponsor is present.");
  }

  @Test
  public void testSponsorWithoutTfSponsorReserveThrows() {
    assertThatThrownBy(() -> SponsorshipTransfer.builder()
      .account(Address.of("rfkDkFai4jUfCvAJiZ5Vm7XvvWjYvDqeYo"))
      .objectId(Hash256.of("13F1A9B5C2D3E4F613F1A9B5C2D3E4F613F1A9B5C2D3E4F613F1A9B5C2D3E4F6"))
      .sponsor(Address.of("rNEWSponsor3LNcTz8JF2oJC6qaww6RZ7Lw"))
      .sponsorFlags(SponsorFlags.SPONSOR_FEE) // Only fee, not reserve
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.valueOf(43))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("ED87987410480E90474F7A02E0DA0CE4E6ABC8A1377864026A1FEE2718688B0B84")
      )
      .build()
    )
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("SponsorFlags must include tfSponsorReserve when Sponsor is present.");
  }

  @Test
  public void testSponsorFlagsWithTfSponsorReserveWithoutSponsorThrows() {
    assertThatThrownBy(() -> SponsorshipTransfer.builder()
      .account(Address.of("rfkDkFai4jUfCvAJiZ5Vm7XvvWjYvDqeYo"))
      .objectId(Hash256.of("13F1A9B5C2D3E4F613F1A9B5C2D3E4F613F1A9B5C2D3E4F613F1A9B5C2D3E4F6"))
      .sponsorFlags(SponsorFlags.SPONSOR_RESERVE)
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.valueOf(43))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("ED87987410480E90474F7A02E0DA0CE4E6ABC8A1377864026A1FEE2718688B0B84")
      )
      .build()
    )
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("Sponsor must be specified when SponsorFlags includes tfSponsorReserve.");
  }

  @Test
  public void testSponsorSignatureWithoutSponsorThrows() {
    assertThatThrownBy(() -> SponsorshipTransfer.builder()
      .account(Address.of("rfkDkFai4jUfCvAJiZ5Vm7XvvWjYvDqeYo"))
      .objectId(Hash256.of("13F1A9B5C2D3E4F613F1A9B5C2D3E4F613F1A9B5C2D3E4F613F1A9B5C2D3E4F6"))
      .sponsorSignature(SponsorSignature.builder()
        .signingPublicKey(PublicKey.fromBase16EncodedPublicKey("ED87987410480E90474F7A02E0DA0CE4E6ABC8A1377864026A1FEE2718688B0B84"))
        .transactionSignature(Signature.fromBase16("0000000000000000000000000000000000000000000000000000000000000000"))
        .build())
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.valueOf(43))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("ED87987410480E90474F7A02E0DA0CE4E6ABC8A1377864026A1FEE2718688B0B84")
      )
      .build()
    )
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("Sponsor must be specified when SponsorSignature is present.");
  }
}
