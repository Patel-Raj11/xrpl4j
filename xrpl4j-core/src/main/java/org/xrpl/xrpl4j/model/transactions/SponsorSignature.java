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
import org.immutables.value.Value;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.crypto.signing.Signature;

import java.util.List;
import java.util.Optional;

/**
 * Represents the sponsor's signature for a sponsored transaction.
 *
 * <p>Either {@link #signingPublicKey()} + {@link #transactionSignature()} (for single-signing)
 * or {@link #signers()} (for multi-signing) must be included in the transaction.</p>
 *
 * <p>This class will be marked {@link Beta} until the SponsoredFees amendment is enabled on mainnet.
 * Its API is subject to change.</p>
 */
@Beta
@Value.Immutable
@JsonSerialize(as = ImmutableSponsorSignature.class)
@JsonDeserialize(as = ImmutableSponsorSignature.class)
public interface SponsorSignature {

  /**
   * Construct a builder for this class.
   *
   * @return An {@link ImmutableSponsorSignature.Builder}.
   */
  static ImmutableSponsorSignature.Builder builder() {
    return ImmutableSponsorSignature.builder();
  }

  /**
   * The public key used to create the signature, if single-signing.
   *
   * @return An {@link Optional} {@link PublicKey}.
   */
  @JsonProperty("SigningPubKey")
  Optional<PublicKey> signingPublicKey();

  /**
   * A signature of the transaction from the sponsor, to indicate their approval of this transaction,
   * if single-signing.
   *
   * @return An {@link Optional} {@link Signature}.
   */
  @JsonProperty("TxnSignature")
  Optional<Signature> transactionSignature();

  /**
   * An array of signatures of the transaction from the sponsor's signers to indicate their approval
   * of this transaction, if the sponsor is multi-signing.
   *
   * @return A {@link List} of {@link SignerWrapper}s.
   */
  @JsonProperty("Signers")
  List<SignerWrapper> signers();
}

