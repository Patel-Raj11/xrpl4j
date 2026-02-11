package org.xrpl.xrpl4j.model.client.accounts;

/*-
 * ========================LICENSE_START=================================
 * xrpl4j :: model
 * %%
 * Copyright (C) 2020 - 2022 XRPL Foundation and its contributors
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
import org.xrpl.xrpl4j.model.client.common.LedgerIndex;
import org.xrpl.xrpl4j.model.flags.RippleStateFlags;
import org.xrpl.xrpl4j.model.ledger.RippleStateObject;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.Hash256;
import org.xrpl.xrpl4j.model.transactions.IssuedCurrencyAmount;
import org.xrpl.xrpl4j.model.transactions.Marker;

public class AccountSponsoringResultJsonTests extends AbstractJsonTest {

  @Test
  public void testJson() throws JsonProcessingException, JSONException {
    AccountSponsoringResult result = AccountSponsoringResult.builder()
      .account(Address.of("rSponsor1VktvzBz8JF2oJC6qaww6RZ7Lw"))
      .addSponsoredObjects(
        RippleStateObject.builder()
          .balance(IssuedCurrencyAmount.builder()
            .currency("USD")
            .issuer(Address.of("rrrrrrrrrrrrrrrrrrrrBZbvji"))
            .value("100")
            .build())
          .flags(RippleStateFlags.of(65536))
          .highLimit(IssuedCurrencyAmount.builder()
            .currency("USD")
            .issuer(Address.of("rN7n7otQDd6FczFgLdlqtyMVrn3HMfXpf"))
            .value("1000")
            .build())
          .highNode("0000000000000000")
          .highSponsor(Address.of("rSponsor1VktvzBz8JF2oJC6qaww6RZ7Lw"))
          .lowLimit(IssuedCurrencyAmount.builder()
            .currency("USD")
            .issuer(Address.of("rfkDkFai4jUfCvAJiZ5Vm7XvvWjYvDqeYo"))
            .value("0")
            .build())
          .lowNode("0000000000000000")
          .previousTransactionId(Hash256.of("1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF"))
          .previousTransactionLedgerSequence(UnsignedInteger.valueOf(12345678))
          .index(Hash256.of("ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890"))
          .build()
      )
      .ledgerHash(Hash256.of("FEDCBA0987654321FEDCBA0987654321FEDCBA0987654321FEDCBA0987654321"))
      .ledgerIndex(LedgerIndex.of(UnsignedInteger.valueOf(56789012)))
      .limit(UnsignedInteger.valueOf(10))
      .marker(Marker.of("someMarkerValue"))
      .validated(true)
      .status("success")
      .build();

    String json = "{\n" +
      "        \"account\": \"rSponsor1VktvzBz8JF2oJC6qaww6RZ7Lw\",\n" +
      "        \"sponsored_objects\": [\n" +
      "            {\n" +
      "                \"Balance\": {\n" +
      "                    \"currency\": \"USD\",\n" +
      "                    \"issuer\": \"rrrrrrrrrrrrrrrrrrrrBZbvji\",\n" +
      "                    \"value\": \"100\"\n" +
      "                },\n" +
      "                \"Flags\": 65536,\n" +
      "                \"HighLimit\": {\n" +
      "                    \"currency\": \"USD\",\n" +
      "                    \"issuer\": \"rN7n7otQDd6FczFgLdlqtyMVrn3HMfXpf\",\n" +
      "                    \"value\": \"1000\"\n" +
      "                },\n" +
      "                \"HighNode\": \"0000000000000000\",\n" +
      "                \"HighSponsor\": \"rSponsor1VktvzBz8JF2oJC6qaww6RZ7Lw\",\n" +
      "                \"LedgerEntryType\": \"RippleState\",\n" +
      "                \"LowLimit\": {\n" +
      "                    \"currency\": \"USD\",\n" +
      "                    \"issuer\": \"rfkDkFai4jUfCvAJiZ5Vm7XvvWjYvDqeYo\",\n" +
      "                    \"value\": \"0\"\n" +
      "                },\n" +
      "                \"LowNode\": \"0000000000000000\",\n" +
      "                \"PreviousTxnID\": \"1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF\",\n" +
      "                \"PreviousTxnLgrSeq\": 12345678,\n" +
      "                \"index\": \"ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890\"\n" +
      "            }\n" +
      "        ],\n" +
      "        \"ledger_hash\": \"FEDCBA0987654321FEDCBA0987654321FEDCBA0987654321FEDCBA0987654321\",\n" +
      "        \"ledger_index\": 56789012,\n" +
      "        \"limit\": 10,\n" +
      "        \"marker\": \"someMarkerValue\",\n" +
      "        \"status\": \"success\",\n" +
      "        \"validated\": true\n" +
      "    }";

    assertCanSerializeAndDeserialize(result, json);
  }

  @Test
  public void testMinimalJson() throws JsonProcessingException, JSONException {
    AccountSponsoringResult result = AccountSponsoringResult.builder()
      .account(Address.of("rSponsor1VktvzBz8JF2oJC6qaww6RZ7Lw"))
      .ledgerIndex(LedgerIndex.of(UnsignedInteger.valueOf(56789012)))
      .validated(true)
      .status("success")
      .build();

    String json = "{\n" +
      "        \"account\": \"rSponsor1VktvzBz8JF2oJC6qaww6RZ7Lw\",\n" +
      "        \"ledger_index\": 56789012,\n" +
      "        \"status\": \"success\",\n" +
      "        \"validated\": true\n" +
      "    }";

    assertCanSerializeAndDeserialize(result, json);
  }
}

