package org.xrpl.xrpl4j.crypto.mptcryptowasm;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.security.SecureRandom;

/**
 * Unit tests for {@link WasmElGamalOperations}.
 *
 * <p>These tests verify that the WASM binary is correctly loaded and the
 * ElGamal cryptographic operations work as expected.</p>
 */
class WasmElGamalOperationsTest {

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  // ============================================================================
  // Key Generation Tests
  // ============================================================================

  @Test
  void generateKeyPair_producesValidKeyPair() {
    // Act
    WasmElGamalKeyPair keyPair = WasmElGamalOperations.generateKeyPair();

    // Assert
    assertThat(keyPair).isNotNull();
    assertThat(keyPair.privateKey()).hasSize(32);
    assertThat(keyPair.publicKey()).hasSize(33);

    // Compressed public keys start with 0x02 or 0x03
    byte firstByte = keyPair.publicKey()[0];
    assertThat(firstByte == 0x02 || firstByte == 0x03)
      .as("Public key should be in compressed format (starting with 0x02 or 0x03)")
      .isTrue();
  }

  @Test
  void generateKeyPair_producesUniqueKeyPairs() {
    // Act
    WasmElGamalKeyPair keyPair1 = WasmElGamalOperations.generateKeyPair();
    WasmElGamalKeyPair keyPair2 = WasmElGamalOperations.generateKeyPair();

    // Assert - each call should produce different keys
    assertThat(keyPair1.privateKey()).isNotEqualTo(keyPair2.privateKey());
    assertThat(keyPair1.publicKey()).isNotEqualTo(keyPair2.publicKey());
  }

  @Test
  void generateKeyPair_privateKeyIsNonZero() {
    // Act
    WasmElGamalKeyPair keyPair = WasmElGamalOperations.generateKeyPair();

    // Assert - private key should not be all zeros
    byte[] privateKey = keyPair.privateKey();
    boolean allZeros = true;
    for (byte b : privateKey) {
      if (b != 0) {
        allZeros = false;
        break;
      }
    }
    assertThat(allZeros).as("Private key should not be all zeros").isFalse();
  }

  // ============================================================================
  // Encryption/Decryption Round-Trip Tests
  // ============================================================================

  @ParameterizedTest
  @ValueSource(longs = {0L, 1L, 100L, 1000L, 10000L, 100000L, 999999L})
  void encryptDecrypt_roundTrip_recoversOriginalAmount(long amount) {
    // Arrange
    WasmElGamalKeyPair keyPair = WasmElGamalOperations.generateKeyPair();
    byte[] blindingFactor = generateRandomBlindingFactor();

    // Act
    byte[] ciphertext = WasmElGamalOperations.encrypt(
      keyPair.publicKey(),
      amount,
      blindingFactor
    );
    long decryptedAmount = WasmElGamalOperations.decrypt(ciphertext, keyPair.privateKey());

    // Assert
    assertThat(decryptedAmount).isEqualTo(amount);
  }

  @Test
  void encrypt_producesCiphertextOfCorrectSize() {
    // Arrange
    WasmElGamalKeyPair keyPair = WasmElGamalOperations.generateKeyPair();
    byte[] blindingFactor = generateRandomBlindingFactor();

    // Act
    byte[] ciphertext = WasmElGamalOperations.encrypt(
      keyPair.publicKey(),
      42L,
      blindingFactor
    );

    // Assert - ciphertext should be 66 bytes (C1: 33 + C2: 33)
    assertThat(ciphertext).hasSize(66);

    // Both C1 and C2 should be valid compressed points
    byte c1FirstByte = ciphertext[0];
    byte c2FirstByte = ciphertext[33];
    assertThat(c1FirstByte == 0x02 || c1FirstByte == 0x03)
      .as("C1 should be a compressed point").isTrue();
    assertThat(c2FirstByte == 0x02 || c2FirstByte == 0x03)
      .as("C2 should be a compressed point").isTrue();
  }

  @Test
  void encrypt_differentBlindingFactors_produceDifferentCiphertexts() {
    // Arrange
    WasmElGamalKeyPair keyPair = WasmElGamalOperations.generateKeyPair();
    long amount = 100L;
    byte[] blindingFactor1 = generateRandomBlindingFactor();
    byte[] blindingFactor2 = generateRandomBlindingFactor();

    // Act
    byte[] ciphertext1 = WasmElGamalOperations.encrypt(keyPair.publicKey(), amount, blindingFactor1);
    byte[] ciphertext2 = WasmElGamalOperations.encrypt(keyPair.publicKey(), amount, blindingFactor2);

    // Assert - same amount with different blinding factors should produce different ciphertexts
    assertThat(ciphertext1).isNotEqualTo(ciphertext2);

    // But both should decrypt to the same amount
    assertThat(WasmElGamalOperations.decrypt(ciphertext1, keyPair.privateKey())).isEqualTo(amount);
    assertThat(WasmElGamalOperations.decrypt(ciphertext2, keyPair.privateKey())).isEqualTo(amount);
  }

  // ============================================================================
  // Helper Methods
  // ============================================================================

  private byte[] generateRandomBlindingFactor() {
    byte[] blindingFactor = new byte[32];
    SECURE_RANDOM.nextBytes(blindingFactor);
    return blindingFactor;
  }
}

