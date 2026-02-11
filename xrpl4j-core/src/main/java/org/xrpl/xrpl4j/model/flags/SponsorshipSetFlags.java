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
import com.google.common.base.Preconditions;
import org.xrpl.xrpl4j.model.transactions.SponsorshipSet;

/**
 * A set of static {@link TransactionFlags} which can be set on {@link SponsorshipSet} transactions.
 *
 * <p>This class will be marked {@link Beta} until the SponsoredFees amendment is enabled on mainnet.
 * Its API is subject to change.</p>
 */
@Beta
public class SponsorshipSetFlags extends TransactionFlags {

  /**
   * Constant {@link SponsorshipSetFlags} for an unset flag.
   */
  public static final SponsorshipSetFlags UNSET = new SponsorshipSetFlags(0);

  /**
   * Constant {@link SponsorshipSetFlags} for the {@code tfSponsorshipSetRequireSignForFee} flag.
   * If set, enables the lsfSponsorshipRequireSignForFee flag on the Sponsorship object.
   */
  public static final SponsorshipSetFlags SET_REQUIRE_SIGN_FOR_FEE = new SponsorshipSetFlags(0x00010000L);

  /**
   * Constant {@link SponsorshipSetFlags} for the {@code tfSponsorshipClearRequireSignForFee} flag.
   * If set, disables the lsfSponsorshipRequireSignForFee flag on the Sponsorship object.
   */
  public static final SponsorshipSetFlags CLEAR_REQUIRE_SIGN_FOR_FEE = new SponsorshipSetFlags(0x00020000L);

  /**
   * Constant {@link SponsorshipSetFlags} for the {@code tfSponsorshipSetRequireSignForReserve} flag.
   * If set, enables the lsfSponsorshipRequireSignForReserve flag on the Sponsorship object.
   */
  public static final SponsorshipSetFlags SET_REQUIRE_SIGN_FOR_RESERVE = new SponsorshipSetFlags(0x00040000L);

  /**
   * Constant {@link SponsorshipSetFlags} for the {@code tfSponsorshipClearRequireSignForReserve} flag.
   * If set, disables the lsfSponsorshipRequireSignForReserve flag on the Sponsorship object.
   */
  public static final SponsorshipSetFlags CLEAR_REQUIRE_SIGN_FOR_RESERVE = new SponsorshipSetFlags(0x00080000L);

  /**
   * Constant {@link SponsorshipSetFlags} for the {@code tfDeleteObject} flag.
   * If set, deletes the Sponsorship object.
   */
  public static final SponsorshipSetFlags DELETE_OBJECT = new SponsorshipSetFlags(0x00100000L);

  private SponsorshipSetFlags(long value) {
    super(value);
  }

  private SponsorshipSetFlags() {
  }

  /**
   * Create a new {@link Builder}.
   *
   * @return A new {@link Builder}.
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Construct {@link SponsorshipSetFlags} with a given value.
   *
   * @param value The long-number encoded flags value of this {@link SponsorshipSetFlags}.
   *
   * @return New {@link SponsorshipSetFlags}.
   */
  public static SponsorshipSetFlags of(long value) {
    SponsorshipSetFlags flags = new SponsorshipSetFlags(value);

    Preconditions.checkArgument(
      !(flags.tfSponsorshipSetRequireSignForFee() && flags.tfSponsorshipClearRequireSignForFee()),
      "tfSponsorshipSetRequireSignForFee and tfSponsorshipClearRequireSignForFee cannot both be set."
    );

    Preconditions.checkArgument(
      !(flags.tfSponsorshipSetRequireSignForReserve() && flags.tfSponsorshipClearRequireSignForReserve()),
      "tfSponsorshipSetRequireSignForReserve and tfSponsorshipClearRequireSignForReserve cannot both be set."
    );

    return flags;
  }

  /**
   * Construct an empty instance of {@link SponsorshipSetFlags}. Transactions with empty flags will
   * not be serialized with a {@code Flags} field.
   *
   * @return An empty {@link SponsorshipSetFlags}.
   */
  public static SponsorshipSetFlags empty() {
    return new SponsorshipSetFlags();
  }

  /**
   * Indicates whether the {@code tfSponsorshipSetRequireSignForFee} flag is set.
   *
   * @return {@code true} if {@code tfSponsorshipSetRequireSignForFee} is set, otherwise {@code false}.
   */
  public boolean tfSponsorshipSetRequireSignForFee() {
    return this.isSet(SET_REQUIRE_SIGN_FOR_FEE);
  }

  /**
   * Indicates whether the {@code tfSponsorshipClearRequireSignForFee} flag is set.
   *
   * @return {@code true} if {@code tfSponsorshipClearRequireSignForFee} is set, otherwise {@code false}.
   */
  public boolean tfSponsorshipClearRequireSignForFee() {
    return this.isSet(CLEAR_REQUIRE_SIGN_FOR_FEE);
  }

  /**
   * Indicates whether the {@code tfSponsorshipSetRequireSignForReserve} flag is set.
   *
   * @return {@code true} if {@code tfSponsorshipSetRequireSignForReserve} is set, otherwise {@code false}.
   */
  public boolean tfSponsorshipSetRequireSignForReserve() {
    return this.isSet(SET_REQUIRE_SIGN_FOR_RESERVE);
  }

  /**
   * Indicates whether the {@code tfSponsorshipClearRequireSignForReserve} flag is set.
   *
   * @return {@code true} if {@code tfSponsorshipClearRequireSignForReserve} is set, otherwise {@code false}.
   */
  public boolean tfSponsorshipClearRequireSignForReserve() {
    return this.isSet(CLEAR_REQUIRE_SIGN_FOR_RESERVE);
  }

  /**
   * Indicates whether the {@code tfDeleteObject} flag is set.
   *
   * @return {@code true} if {@code tfDeleteObject} is set, otherwise {@code false}.
   */
  public boolean tfDeleteObject() {
    return this.isSet(DELETE_OBJECT);
  }

  /**
   * A builder class for {@link SponsorshipSetFlags} flags.
   */
  public static class Builder {
    private boolean tfSponsorshipSetRequireSignForFee = false;
    private boolean tfSponsorshipClearRequireSignForFee = false;
    private boolean tfSponsorshipSetRequireSignForReserve = false;
    private boolean tfSponsorshipClearRequireSignForReserve = false;
    private boolean tfDeleteObject = false;

    /**
     * Set {@code tfSponsorshipSetRequireSignForFee} to the given value.
     *
     * @param value A boolean value.
     *
     * @return The same {@link Builder}.
     */
    public Builder tfSponsorshipSetRequireSignForFee(boolean value) {
      this.tfSponsorshipSetRequireSignForFee = value;
      return this;
    }

    /**
     * Set {@code tfSponsorshipClearRequireSignForFee} to the given value.
     *
     * @param value A boolean value.
     *
     * @return The same {@link Builder}.
     */
    public Builder tfSponsorshipClearRequireSignForFee(boolean value) {
      this.tfSponsorshipClearRequireSignForFee = value;
      return this;
    }

    /**
     * Set {@code tfSponsorshipSetRequireSignForReserve} to the given value.
     *
     * @param value A boolean value.
     *
     * @return The same {@link Builder}.
     */
    public Builder tfSponsorshipSetRequireSignForReserve(boolean value) {
      this.tfSponsorshipSetRequireSignForReserve = value;
      return this;
    }

    /**
     * Set {@code tfSponsorshipClearRequireSignForReserve} to the given value.
     *
     * @param value A boolean value.
     *
     * @return The same {@link Builder}.
     */
    public Builder tfSponsorshipClearRequireSignForReserve(boolean value) {
      this.tfSponsorshipClearRequireSignForReserve = value;
      return this;
    }

    /**
     * Set {@code tfDeleteObject} to the given value.
     *
     * @param value A boolean value.
     *
     * @return The same {@link Builder}.
     */
    public Builder tfDeleteObject(boolean value) {
      this.tfDeleteObject = value;
      return this;
    }

    /**
     * Build a new {@link SponsorshipSetFlags} from the current boolean values.
     *
     * @return A new {@link SponsorshipSetFlags}.
     */
    public SponsorshipSetFlags build() {
      Preconditions.checkArgument(
        !(tfSponsorshipSetRequireSignForFee && tfSponsorshipClearRequireSignForFee),
        "tfSponsorshipSetRequireSignForFee and tfSponsorshipClearRequireSignForFee cannot both be set."
      );

      Preconditions.checkArgument(
        !(tfSponsorshipSetRequireSignForReserve && tfSponsorshipClearRequireSignForReserve),
        "tfSponsorshipSetRequireSignForReserve and tfSponsorshipClearRequireSignForReserve cannot both be set."
      );

      long value = TransactionFlags.FULLY_CANONICAL_SIG.getValue();
      if (tfSponsorshipSetRequireSignForFee) {
        value |= SET_REQUIRE_SIGN_FOR_FEE.getValue();
      }
      if (tfSponsorshipClearRequireSignForFee) {
        value |= CLEAR_REQUIRE_SIGN_FOR_FEE.getValue();
      }
      if (tfSponsorshipSetRequireSignForReserve) {
        value |= SET_REQUIRE_SIGN_FOR_RESERVE.getValue();
      }
      if (tfSponsorshipClearRequireSignForReserve) {
        value |= CLEAR_REQUIRE_SIGN_FOR_RESERVE.getValue();
      }
      if (tfDeleteObject) {
        value |= DELETE_OBJECT.getValue();
      }
      return new SponsorshipSetFlags(value);
    }
  }
}

