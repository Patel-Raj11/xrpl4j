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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.annotations.Beta;
import com.google.common.primitives.UnsignedInteger;
import org.immutables.value.Value;
import org.xrpl.xrpl4j.model.client.XrplRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountObjectsRequestParams.AccountObjectType;
import org.xrpl.xrpl4j.model.client.common.LedgerSpecifier;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.Marker;

import java.util.Optional;

/**
 * Represents the request parameters for an "account_sponsoring" Clio RPC call.
 *
 * <p>This API is used to fetch a list of objects that an account is sponsoring; namely, a list of objects
 * where the Sponsor is the given account.</p>
 *
 * <p>This interface is marked as {@link Beta} because the Sponsored Fees feature is not yet enabled on mainnet.</p>
 */
@Value.Immutable
@JsonSerialize(as = ImmutableAccountSponsoringRequestParams.class)
@JsonDeserialize(as = ImmutableAccountSponsoringRequestParams.class)
@Beta
public interface AccountSponsoringRequestParams extends XrplRequestParams {

  /**
   * Construct a builder for this class.
   *
   * @return An {@link ImmutableAccountSponsoringRequestParams.Builder}.
   */
  static ImmutableAccountSponsoringRequestParams.Builder builder() {
    return ImmutableAccountSponsoringRequestParams.builder();
  }

  /**
   * Construct an {@link AccountSponsoringRequestParams} for a given account and otherwise default parameters.
   *
   * @param classicAddress The classic {@link Address} of the sponsor account to request sponsored objects for.
   *
   * @return An {@link AccountSponsoringRequestParams} for the given {@link Address}.
   */
  static AccountSponsoringRequestParams of(Address classicAddress) {
    return builder()
      .account(classicAddress)
      .ledgerSpecifier(LedgerSpecifier.CURRENT)
      .build();
  }

  /**
   * The sponsor account in question.
   *
   * @return The unique XRPL {@link Address} for the sponsor account.
   */
  Address account();

  /**
   * If included, filter results to include only this type of ledger object.
   *
   * @return An optionally-present {@link AccountObjectType} to filter by.
   */
  Optional<AccountObjectType> type();

  /**
   * If true, the response only includes {@link org.xrpl.xrpl4j.model.ledger.LedgerObject}s that would block this
   * account from being deleted. The default is false.
   *
   * @return {@code true} if requesting only ledger objects that would block this account from being deleted, otherwise
   *   {@code false}.
   */
  @JsonProperty("deletion_blockers_only")
  @Value.Default
  default boolean deletionBlockersOnly() {
    return false;
  }

  /**
   * Specifies the ledger version to request. A ledger version can be specified by ledger hash, numerical ledger index,
   * or a shortcut value.
   *
   * @return A {@link LedgerSpecifier} specifying the ledger version to request.
   */
  @JsonUnwrapped
  LedgerSpecifier ledgerSpecifier();

  /**
   * The maximum number of {@link org.xrpl.xrpl4j.model.ledger.LedgerObject}s to include in the resulting
   * {@link AccountSponsoringResult#sponsoredObjects()}.
   *
   * @return An optionally-present {@link UnsignedInteger} denoting the response limit.
   */
  Optional<UnsignedInteger> limit();

  /**
   * Value from a previous paginated response. Resume retrieving data where that response left off.
   *
   * @return An optionally-present {@link Marker} containing the marker.
   */
  Optional<Marker> marker();
}

