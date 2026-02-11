package org.xrpl.xrpl4j.model.ledger;

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
import org.xrpl.xrpl4j.model.AbstractJsonTest;
import org.xrpl.xrpl4j.model.flags.SponsorshipFlags;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.Hash256;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

/**
 * Unit tests for {@link SponsorshipObject} JSON serialization.
 */
public class SponsorshipObjectJsonTest extends AbstractJsonTest {

  @Test
  public void testJson() throws JsonProcessingException, JSONException {
    SponsorshipObject sponsorshipObject = SponsorshipObject.builder()
      .owner(Address.of("rN7n7otQDd6FczFgLdlqtyMVrn3HMfXpf"))
      .sponsee(Address.of("rfkDkFai4jUfCvAJiZ5Vm7XvvWjYvDqeYo"))
      .feeAmount(XrpCurrencyAmount.ofDrops(1000000))
      .maxFee(XrpCurrencyAmount.ofDrops(1000))
      .reserveCount(UnsignedInteger.valueOf(5))
      .ownerNode("0")
      .sponseeNode("0")
      .previousTxnId(Hash256.of("FC7C6F49B7264CD1984C58E68B2F30B6580AE5EC94D215737E266C1404E3DEFF"))
      .previousTransactionLedgerSequence(UnsignedInteger.valueOf(3105995))
      .index(Hash256.of("5A47EEAD6C185E66C20D4A61BDE8F38181B43250FA37F22EBB604C1F97C8166E"))
      .flags(SponsorshipFlags.REQUIRE_SIGN_FOR_FEE)
      .build();

    String json = "{" +
      "  \"Owner\": \"rN7n7otQDd6FczFgLdlqtyMVrn3HMfXpf\"," +
      "  \"Sponsee\": \"rfkDkFai4jUfCvAJiZ5Vm7XvvWjYvDqeYo\"," +
      "  \"FeeAmount\": \"1000000\"," +
      "  \"MaxFee\": \"1000\"," +
      "  \"ReserveCount\": 5," +
      "  \"OwnerNode\": \"0\"," +
      "  \"SponseeNode\": \"0\"," +
      "  \"Flags\": 65536," +
      "  \"LedgerEntryType\": \"Sponsorship\"," +
      "  \"PreviousTxnID\": \"FC7C6F49B7264CD1984C58E68B2F30B6580AE5EC94D215737E266C1404E3DEFF\"," +
      "  \"PreviousTxnLgrSeq\": 3105995," +
      "  \"index\": \"5A47EEAD6C185E66C20D4A61BDE8F38181B43250FA37F22EBB604C1F97C8166E\"" +
      "}";

    assertCanSerializeAndDeserialize(sponsorshipObject, json);
  }

  @Test
  public void testJsonWithEmptyFlags() throws JsonProcessingException, JSONException {
    SponsorshipObject sponsorshipObject = SponsorshipObject.builder()
      .owner(Address.of("rN7n7otQDd6FczFgLdlqtyMVrn3HMfXpf"))
      .sponsee(Address.of("rfkDkFai4jUfCvAJiZ5Vm7XvvWjYvDqeYo"))
      .feeAmount(XrpCurrencyAmount.ofDrops(500000))
      .reserveCount(UnsignedInteger.valueOf(3))
      .ownerNode("0")
      .sponseeNode("0")
      .previousTxnId(Hash256.of("FC7C6F49B7264CD1984C58E68B2F30B6580AE5EC94D215737E266C1404E3DEFF"))
      .previousTransactionLedgerSequence(UnsignedInteger.valueOf(3105995))
      .index(Hash256.of("5A47EEAD6C185E66C20D4A61BDE8F38181B43250FA37F22EBB604C1F97C8166E"))
      .flags(SponsorshipFlags.EMPTY)
      .build();

    String json = "{" +
      "  \"Owner\": \"rN7n7otQDd6FczFgLdlqtyMVrn3HMfXpf\"," +
      "  \"Sponsee\": \"rfkDkFai4jUfCvAJiZ5Vm7XvvWjYvDqeYo\"," +
      "  \"FeeAmount\": \"500000\"," +
      "  \"ReserveCount\": 3," +
      "  \"OwnerNode\": \"0\"," +
      "  \"SponseeNode\": \"0\"," +
      "  \"Flags\": 0," +
      "  \"LedgerEntryType\": \"Sponsorship\"," +
      "  \"PreviousTxnID\": \"FC7C6F49B7264CD1984C58E68B2F30B6580AE5EC94D215737E266C1404E3DEFF\"," +
      "  \"PreviousTxnLgrSeq\": 3105995," +
      "  \"index\": \"5A47EEAD6C185E66C20D4A61BDE8F38181B43250FA37F22EBB604C1F97C8166E\"" +
      "}";

    assertCanSerializeAndDeserialize(sponsorshipObject, json);
  }
}

