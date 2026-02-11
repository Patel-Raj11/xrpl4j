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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.annotations.Beta;
import com.google.common.primitives.UnsignedInteger;
import org.immutables.value.Value;
import org.xrpl.xrpl4j.model.flags.SponsorshipFlags;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.Hash256;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

import java.util.Optional;

/**
 * A Sponsorship ledger object represents a sponsorship relationship between a sponsor and a sponsee.
 * The sponsor can pay for the sponsee's transaction fees and/or object reserves.
 *
 * <p>This class will be marked {@link Beta} until the SponsoredFees amendment is enabled on mainnet.
 * Its API is subject to change.</p>
 */
@Beta
@Value.Immutable
@JsonSerialize(as = ImmutableSponsorshipObject.class)
@JsonDeserialize(as = ImmutableSponsorshipObject.class)
public interface SponsorshipObject extends LedgerObject {

  /**
   * Construct a builder for this class.
   *
   * @return An {@link ImmutableSponsorshipObject.Builder}.
   */
  static ImmutableSponsorshipObject.Builder builder() {
    return ImmutableSponsorshipObject.builder();
  }

  /**
   * Indicates that this object is a {@link SponsorshipObject} object.
   *
   * @return Always {@link org.xrpl.xrpl4j.model.ledger.LedgerObject.LedgerEntryType#SPONSORSHIP}.
   */
  @JsonProperty("LedgerEntryType")
  @Value.Derived
  default LedgerEntryType ledgerEntryType() {
    return LedgerEntryType.SPONSORSHIP;
  }

  /**
   * A set of boolean {@link SponsorshipFlags} containing options enabled for this object.
   *
   * @return The {@link SponsorshipFlags} for this object.
   */
  @JsonProperty("Flags")
  @Value.Default
  default SponsorshipFlags flags() {
    return SponsorshipFlags.EMPTY;
  }

  /**
   * The sponsor associated with this relationship. This account also pays for the reserve of this object.
   *
   * @return The {@link Address} of the sponsor (owner).
   */
  @JsonProperty("Owner")
  Address owner();

  /**
   * The sponsee associated with this relationship.
   *
   * @return The {@link Address} of the sponsee.
   */
  @JsonProperty("Sponsee")
  Address sponsee();

  /**
   * The (remaining) amount of XRP that the sponsor has provided for the sponsee to use for fees.
   *
   * @return An {@link Optional} {@link XrpCurrencyAmount} representing the fee amount.
   */
  @JsonProperty("FeeAmount")
  Optional<XrpCurrencyAmount> feeAmount();

  /**
   * The maximum fee per transaction that will be sponsored.
   *
   * @return An {@link Optional} {@link XrpCurrencyAmount} representing the max fee.
   */
  @JsonProperty("MaxFee")
  Optional<XrpCurrencyAmount> maxFee();

  /**
   * The (remaining) number of OwnerCount that the sponsor has provided for the sponsee to use for reserves.
   *
   * @return An {@link Optional} {@link UnsignedInteger} representing the reserve count.
   */
  @JsonProperty("ReserveCount")
  Optional<UnsignedInteger> reserveCount();

  /**
   * A hint indicating which page of the sponsor's owner directory links to this object.
   *
   * @return A {@link String} containing the owner node hint.
   */
  @JsonProperty("OwnerNode")
  String ownerNode();

  /**
   * A hint indicating which page of the sponsee's owner directory links to this object.
   *
   * @return A {@link String} containing the sponsee node hint.
   */
  @JsonProperty("SponseeNode")
  String sponseeNode();

  /**
   * The identifying hash of the transaction that most recently modified this object.
   *
   * @return A {@link Hash256} containing the previous transaction hash.
   */
  @JsonProperty("PreviousTxnID")
  Hash256 previousTxnId();

  /**
   * The index of the ledger that contains the transaction that most recently modified this object.
   *
   * @return A {@link UnsignedInteger} representing the previous transaction sequence.
   */
  @JsonProperty("PreviousTxnLgrSeq")
  UnsignedInteger previousTransactionLedgerSequence();

  /**
   * The unique ID of the {@link SponsorshipObject}.
   *
   * @return A {@link Hash256}.
   */
  Hash256 index();
}

