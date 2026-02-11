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

import com.google.common.annotations.Beta;
import org.xrpl.xrpl4j.model.ledger.SponsorshipObject;

/**
 * A set of static {@link Flags} which can be set on {@link SponsorshipObject}'s.
 *
 * <p>This class will be marked {@link Beta} until the SponsoredFees amendment is enabled on mainnet.
 * Its API is subject to change.</p>
 */
@Beta
public class SponsorshipFlags extends Flags {

  /**
   * Constant for an unset flag.
   */
  public static final SponsorshipFlags UNSET = new SponsorshipFlags(0);

  /**
   * Constant for an empty flag (same as UNSET, used for default values).
   */
  public static final SponsorshipFlags EMPTY = new SponsorshipFlags(0);

  /**
   * Constant {@link SponsorshipFlags} for the {@code lsfSponsorshipRequireSignForFee} flag.
   * If set, the sponsor must co-sign any transaction that uses fee sponsorship from this Sponsorship object.
   */
  public static final SponsorshipFlags REQUIRE_SIGN_FOR_FEE = new SponsorshipFlags(0x00010000);

  /**
   * Constant {@link SponsorshipFlags} for the {@code lsfSponsorshipRequireSignForReserve} flag.
   * If set, the sponsor must co-sign any transaction that uses reserve sponsorship from this Sponsorship object.
   */
  public static final SponsorshipFlags REQUIRE_SIGN_FOR_RESERVE = new SponsorshipFlags(0x00020000);

  private SponsorshipFlags(long value) {
    super(value);
  }

  /**
   * Construct {@link SponsorshipFlags} with a given value.
   *
   * @param value The long-number encoded flags value of this {@link SponsorshipFlags}.
   *
   * @return New {@link SponsorshipFlags}.
   */
  public static SponsorshipFlags of(long value) {
    return new SponsorshipFlags(value);
  }

  /**
   * Indicates whether the sponsor must co-sign any transaction that uses fee sponsorship from this Sponsorship object.
   *
   * @return {@code true} if {@code lsfSponsorshipRequireSignForFee} is set, otherwise {@code false}.
   */
  public boolean lsfSponsorshipRequireSignForFee() {
    return this.isSet(REQUIRE_SIGN_FOR_FEE);
  }

  /**
   * Indicates whether the sponsor must co-sign any transaction that uses reserve sponsorship from this
   * Sponsorship object.
   *
   * @return {@code true} if {@code lsfSponsorshipRequireSignForReserve} is set, otherwise {@code false}.
   */
  public boolean lsfSponsorshipRequireSignForReserve() {
    return this.isSet(REQUIRE_SIGN_FOR_RESERVE);
  }
}

