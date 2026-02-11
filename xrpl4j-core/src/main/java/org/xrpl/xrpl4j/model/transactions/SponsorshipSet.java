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
import com.google.common.primitives.UnsignedInteger;
import org.immutables.value.Value;
import org.xrpl.xrpl4j.model.flags.SponsorshipSetFlags;

import java.util.Optional;

/**
 * A {@link SponsorshipSet} transaction creates, updates, or deletes a Sponsorship object.
 * The Sponsorship object represents a sponsorship relationship between a sponsor and a sponsee.
 *
 * <p>This class will be marked {@link Beta} until the SponsoredFees amendment is enabled on mainnet.
 * Its API is subject to change.</p>
 */
@Beta
@Value.Immutable
@JsonSerialize(as = ImmutableSponsorshipSet.class)
@JsonDeserialize(as = ImmutableSponsorshipSet.class)
public interface SponsorshipSet extends Transaction {

  /**
   * Construct a {@code SponsorshipSet} builder.
   *
   * @return An {@link ImmutableSponsorshipSet.Builder}.
   */
  static ImmutableSponsorshipSet.Builder builder() {
    return ImmutableSponsorshipSet.builder();
  }

  /**
   * Set of {@link SponsorshipSetFlags} for this transaction.
   *
   * @return The {@link SponsorshipSetFlags} for this transaction.
   */
  @JsonProperty("Flags")
  @Value.Default
  default SponsorshipSetFlags flags() {
    return SponsorshipSetFlags.empty();
  }

  /**
   * The sponsor associated with this relationship. This account also pays for the reserve of this object.
   * If this field is included, the Account is assumed to be the Sponsee.
   *
   * @return An {@link Optional} {@link Address} of the sponsor.
   */
  @JsonProperty("Sponsor")
  Optional<Address> sponsor();

  /**
   * The sponsee associated with this relationship.
   * If this field is included, the Account is assumed to be the Sponsor.
   *
   * @return An {@link Optional} {@link Address} of the sponsee.
   */
  @JsonProperty("Sponsee")
  Optional<Address> sponsee();

  /**
   * The (remaining) amount of XRP that the sponsor has provided for the sponsee to use for fees.
   * This value will replace what is currently in the Sponsorship.FeeAmount field (if it exists).
   *
   * @return An {@link Optional} {@link XrpCurrencyAmount} representing the fee amount.
   */
  @JsonProperty("FeeAmount")
  Optional<XrpCurrencyAmount> feeAmount();

  /**
   * The maximum fee per transaction that will be sponsored.
   * This is to prevent abuse/excessive draining of the sponsored fee pool.
   *
   * @return An {@link Optional} {@link XrpCurrencyAmount} representing the max fee.
   */
  @JsonProperty("MaxFee")
  Optional<XrpCurrencyAmount> maxFee();

  /**
   * The (remaining) amount of reserves that the sponsor has provided for the sponsee to use.
   * This value will replace what is currently in the Sponsorship.ReserveCount field (if it exists).
   *
   * @return An {@link Optional} {@link UnsignedInteger} representing the reserve count.
   */
  @JsonProperty("ReserveCount")
  Optional<UnsignedInteger> reserveCount();

  /**
   * Validates that exactly one of {@link #sponsor()} or {@link #sponsee()} is present.
   */
  @Value.Check
  default void validateExactlyOneSponsorOrSponsee() {
    boolean hasSponsor = sponsor().isPresent();
    boolean hasSponsee = sponsee().isPresent();

    Preconditions.checkState(hasSponsor || hasSponsee,
      "Either Sponsor or Sponsee must be specified.");

    Preconditions.checkState(!(hasSponsor && hasSponsee),
      "Both Sponsor and Sponsee cannot be specified at the same time.");
  }

  /**
   * Validates that when tfDeleteObject is set, no other fields or flags can be specified.
   */
  @Value.Check
  default void validateDeleteObjectConstraints() {
    if (flags().tfDeleteObject()) {
      Preconditions.checkState(!feeAmount().isPresent(),
        "FeeAmount cannot be specified when tfDeleteObject is enabled.");

      Preconditions.checkState(!maxFee().isPresent(),
        "MaxFee cannot be specified when tfDeleteObject is enabled.");

      Preconditions.checkState(!reserveCount().isPresent(),
        "ReserveCount cannot be specified when tfDeleteObject is enabled.");

      Preconditions.checkState(!flags().tfSponsorshipSetRequireSignForFee(),
        "tfSponsorshipSetRequireSignForFee cannot be set when tfDeleteObject is enabled.");

      Preconditions.checkState(!flags().tfSponsorshipClearRequireSignForFee(),
        "tfSponsorshipClearRequireSignForFee cannot be set when tfDeleteObject is enabled.");

      Preconditions.checkState(!flags().tfSponsorshipSetRequireSignForReserve(),
        "tfSponsorshipSetRequireSignForReserve cannot be set when tfDeleteObject is enabled.");

      Preconditions.checkState(!flags().tfSponsorshipClearRequireSignForReserve(),
        "tfSponsorshipClearRequireSignForReserve cannot be set when tfDeleteObject is enabled.");
    }
  }

  /**
   * Validates that mutually exclusive flags are not set together.
   */
  @Value.Check
  default void validateMutuallyExclusiveFlags() {
    Preconditions.checkState(
      !(flags().tfSponsorshipSetRequireSignForFee() && flags().tfSponsorshipClearRequireSignForFee()),
      "tfSponsorshipSetRequireSignForFee and tfSponsorshipClearRequireSignForFee cannot both be set."
    );

    Preconditions.checkState(
      !(flags().tfSponsorshipSetRequireSignForReserve() && flags().tfSponsorshipClearRequireSignForReserve()),
      "tfSponsorshipSetRequireSignForReserve and tfSponsorshipClearRequireSignForReserve cannot both be set."
    );
  }

  /**
   * Validates that only the sponsor can create/update the Sponsorship object.
   * If Sponsor is specified (meaning Account is the Sponsee), only tfDeleteObject is allowed.
   */
  @Value.Check
  default void validateSponsorCannotCreateOrUpdate() {
    if (sponsor().isPresent()) {
      // When Sponsor is specified, Account is the Sponsee, and only deletion is allowed
      Preconditions.checkState(flags().tfDeleteObject(),
        "When Sponsor is specified (Account is Sponsee), only tfDeleteObject is allowed. " +
          "Only the sponsor can create or update the Sponsorship object.");
    }
  }
}

