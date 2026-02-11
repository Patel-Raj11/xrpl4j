package org.xrpl.xrpl4j.model.transactions;

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
import com.google.common.base.Preconditions;
import org.immutables.value.Value;
import org.xrpl.xrpl4j.model.flags.SponsorFlags;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;

import java.util.Optional;

/**
 * A {@link SponsorshipTransfer} transaction transfers a sponsor relationship for a particular
 * ledger object's reserve. The sponsor relationship can either be passed on to a new sponsor,
 * or dissolved entirely (with the sponsee taking on the reserve).
 *
 * <p>Either the sponsor or sponsee may submit this transaction at any point in time.</p>
 *
 * <p>This class will be marked {@link Beta} until the SponsoredFees amendment is enabled on mainnet.
 * Its API is subject to change.</p>
 */
@Beta
@Value.Immutable
@JsonSerialize(as = ImmutableSponsorshipTransfer.class)
@JsonDeserialize(as = ImmutableSponsorshipTransfer.class)
public interface SponsorshipTransfer extends Transaction {

  /**
   * Construct a {@code SponsorshipTransfer} builder.
   *
   * @return An {@link ImmutableSponsorshipTransfer.Builder}.
   */
  static ImmutableSponsorshipTransfer.Builder builder() {
    return ImmutableSponsorshipTransfer.builder();
  }

  /**
   * Set of {@link TransactionFlags} for this transaction.
   *
   * @return The {@link TransactionFlags} for this transaction.
   */
  @JsonProperty("Flags")
  @Value.Default
  default TransactionFlags flags() {
    return TransactionFlags.EMPTY;
  }

  /**
   * The ID of the object to transfer sponsorship.
   * If not included, refers to the account sending the transaction.
   *
   * @return An {@link Optional} {@link Hash256} representing the object ID.
   */
  @JsonProperty("ObjectID")
  Optional<Hash256> objectId();

  /**
   * The new sponsor of the object.
   * If included with the tfSponsorReserve flag, the reserve sponsorship for the provided object
   * will be transferred to this sponsor.
   *
   * @return An {@link Optional} {@link Address} of the new sponsor.
   */
  @JsonProperty("Sponsor")
  Optional<Address> sponsor();

  /**
   * Flags on the sponsorship, indicating what type of sponsorship this is (fee vs. reserve).
   *
   * @return An {@link Optional} {@link SponsorFlags}.
   */
  @JsonProperty("SponsorFlags")
  Optional<SponsorFlags> sponsorFlags();

  /**
   * This field contains all the signing information for the sponsorship happening in the transaction.
   * It is included if the transaction is fee- and/or reserve-sponsored.
   *
   * @return An {@link Optional} {@link SponsorSignature}.
   */
  @JsonProperty("SponsorSignature")
  Optional<SponsorSignature> sponsorSignature();

  /**
   * Validates that when Sponsor is present, SponsorFlags must also be present with tfSponsorReserve set.
   * This is required for transferring sponsorship to a new sponsor.
   */
  @Value.Check
  default void validateSponsorRequiresSponsorFlags() {
    if (sponsor().isPresent()) {
      Preconditions.checkState(sponsorFlags().isPresent(),
        "SponsorFlags must be specified when Sponsor is present.");

      Preconditions.checkState(sponsorFlags().get().tfSponsorReserve(),
        "SponsorFlags must include tfSponsorReserve when Sponsor is present.");
    }
  }

  /**
   * Validates that SponsorFlags is only present when Sponsor is present.
   */
  @Value.Check
  default void validateSponsorFlagsRequiresSponsor() {
    if (sponsorFlags().isPresent() && sponsorFlags().get().tfSponsorReserve()) {
      Preconditions.checkState(sponsor().isPresent(),
        "Sponsor must be specified when SponsorFlags includes tfSponsorReserve.");
    }
  }

  /**
   * Validates that SponsorSignature is only present when Sponsor is present.
   */
  @Value.Check
  default void validateSponsorSignatureRequiresSponsor() {
    if (sponsorSignature().isPresent()) {
      Preconditions.checkState(sponsor().isPresent(),
        "Sponsor must be specified when SponsorSignature is present.");
    }
  }
}

