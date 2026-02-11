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

/**
 * A set of static {@link Flags} which can be set on the {@code SponsorFlags} field of a transaction.
 * These flags indicate what type of sponsorship is being requested.
 *
 * <p>This class will be marked {@link Beta} until the SponsoredFees amendment is enabled on mainnet.
 * Its API is subject to change.</p>
 */
@Beta
public class SponsorFlags extends Flags {

  /**
   * Constant for an unset flag.
   */
  public static final SponsorFlags UNSET = new SponsorFlags(0);

  /**
   * Constant for an empty flag (same as UNSET).
   */
  public static final SponsorFlags EMPTY = UNSET;

  /**
   * Constant {@link SponsorFlags} for the {@code tfSponsorFee} flag.
   * If set, the sponsor pays the transaction fee.
   */
  public static final SponsorFlags SPONSOR_FEE = new SponsorFlags(0x00000001);

  /**
   * Constant {@link SponsorFlags} for the {@code tfSponsorReserve} flag.
   * If set, the sponsor pays the reserve for any objects created by the transaction.
   */
  public static final SponsorFlags SPONSOR_RESERVE = new SponsorFlags(0x00000002);

  private SponsorFlags(long value) {
    super(value);
  }

  /**
   * Construct {@link SponsorFlags} with a given value.
   *
   * @param value The long-number encoded flags value of this {@link SponsorFlags}.
   *
   * @return New {@link SponsorFlags}.
   */
  public static SponsorFlags of(long value) {
    return new SponsorFlags(value);
  }

  /**
   * Construct {@link SponsorFlags} from one or more {@link SponsorFlags} by performing a bitwise OR on all.
   *
   * @param flag   The first {@link SponsorFlags}.
   * @param others Zero or more other {@link SponsorFlags} to include.
   *
   * @return A new {@link SponsorFlags}.
   */
  public static SponsorFlags of(SponsorFlags flag, SponsorFlags... others) {
    long value = flag.getValue();
    for (SponsorFlags other : others) {
      value |= other.getValue();
    }
    return new SponsorFlags(value);
  }

  /**
   * Indicates whether the sponsor pays the transaction fee.
   *
   * @return {@code true} if {@code tfSponsorFee} is set, otherwise {@code false}.
   */
  public boolean tfSponsorFee() {
    return this.isSet(SPONSOR_FEE);
  }

  /**
   * Indicates whether the sponsor pays the reserve for any objects created by the transaction.
   *
   * @return {@code true} if {@code tfSponsorReserve} is set, otherwise {@code false}.
   */
  public boolean tfSponsorReserve() {
    return this.isSet(SPONSOR_RESERVE);
  }
}

