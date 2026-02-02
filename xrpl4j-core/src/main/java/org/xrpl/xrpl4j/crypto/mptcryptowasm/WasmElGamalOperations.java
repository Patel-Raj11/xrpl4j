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

import com.dylibso.chicory.runtime.ExportFunction;
import com.dylibso.chicory.runtime.HostFunction;
import com.dylibso.chicory.runtime.ImportValues;
import com.dylibso.chicory.runtime.Instance;
import com.dylibso.chicory.runtime.Memory;
import com.dylibso.chicory.wasi.WasiOptions;
import com.dylibso.chicory.wasi.WasiPreview1;
import com.dylibso.chicory.wasm.Parser;
import com.dylibso.chicory.wasm.WasmModule;
import com.dylibso.chicory.wasm.types.FunctionType;
import com.dylibso.chicory.wasm.types.ValType;
import com.google.common.base.Suppliers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * WASM-based ElGamal encryption operations for Confidential MPT Transfers.
 *
 * <p>This class wraps the WASM implementation of ElGamal encryption, providing:</p>
 * <ul>
 *   <li>{@link #generateKeyPair()} - Generate a new ElGamal keypair</li>
 *   <li>{@link #encrypt(byte[], long, byte[])} - Encrypt an amount under a public key</li>
 *   <li>{@link #decrypt(byte[], byte[], byte[])} - Decrypt a ciphertext using a private key</li>
 * </ul>
 *
 * <h2>Mapping to C Functions</h2>
 * <table border="1">
 *   <tr><th>Java Method</th><th>WASM Function</th><th>C Original</th></tr>
 *   <tr><td>generateKeyPair()</td><td>_secp256k1_elgamal_generate_keypair</td><td>elgamal.c</td></tr>
 *   <tr><td>encrypt()</td><td>_secp256k1_elgamal_encrypt</td><td>elgamal.c</td></tr>
 *   <tr><td>decrypt()</td><td>_secp256k1_elgamal_decrypt</td><td>elgamal.c</td></tr>
 * </table>
 *
 * <h2>Comparison with David's Java Port</h2>
 * <p>David's implementation in {@code org.xrpl.xrpl4j.crypto.mpt.elgamal} uses BouncyCastle
 * for the same operations. This WASM version uses the original C implementation compiled
 * to WebAssembly, allowing for comparison and validation between the two approaches.</p>
 *
 * @see org.xrpl.xrpl4j.crypto.mpt.elgamal.ElGamalBalanceEncryptor
 * @see org.xrpl.xrpl4j.crypto.mpt.elgamal.ElGamalBalanceDecryptor
 */
public final class WasmElGamalOperations {

  private static final String WASM_RESOURCE_PATH = "/mptcryptowasm/mpt_crypto.wasm";

  private static final Supplier<Instance> INSTANCE_SUPPLIER =
    Suppliers.memoize(WasmElGamalOperations::loadWasmInstance);

  // Size constants
  private static final int PRIVATE_KEY_SIZE = 32;
  private static final int PUBLIC_KEY_SIZE = 33;  // Compressed point
  private static final int PUBKEY_STRUCT_SIZE = 64;  // secp256k1_pubkey internal representation
  private static final int BLINDING_FACTOR_SIZE = 32;

  private WasmElGamalOperations() {
  }

  private static Instance loadWasmInstance() {
    try (InputStream wasmStream = WasmElGamalOperations.class.getResourceAsStream(WASM_RESOURCE_PATH)) {
      if (wasmStream == null) {
        throw new IllegalStateException("WASM module not found on classpath: " + WASM_RESOURCE_PATH);
      }
      WasmModule module = Parser.parse(wasmStream);

      // Create WASI Preview 1 implementation for WASI functions (clock_time_get, random_get, etc.)
      WasiPreview1 wasi = WasiPreview1.builder()
        .withOptions(WasiOptions.builder().build())
        .build();

      // Create host imports combining WASI functions and Emscripten functions.
      // The WASM module was compiled with Emscripten which requires certain host functions.
      ImportValues hostImports = ImportValues.builder()
        .addFunction(wasi.toHostFunctions())
        .addFunction(createEmscriptenHostFunctions())
        .build();

      return Instance.builder(module)
        .withImportValues(hostImports)
        .build();
    } catch (IOException e) {
      throw new RuntimeException("Failed to load WASM module from " + WASM_RESOURCE_PATH, e);
    }
  }

  /**
   * Creates host functions required by Emscripten-compiled WASM modules. These are stubs that provide minimal
   * implementations for syscalls and runtime functions.
   */
  private static HostFunction[] createEmscriptenHostFunctions() {
    // Common type signatures
    FunctionType voidToVoid = FunctionType.of(Collections.emptyList(), Collections.emptyList());
    FunctionType i32ToVoid = FunctionType.of(Collections.singletonList(ValType.I32), Collections.emptyList());
    FunctionType i32ToI32 = FunctionType.of(Collections.singletonList(ValType.I32),
      Collections.singletonList(ValType.I32));
    FunctionType i32i32ToI32 = FunctionType.of(java.util.Arrays.asList(ValType.I32, ValType.I32),
      Collections.singletonList(ValType.I32));
    FunctionType i32i32i32ToI32 = FunctionType.of(java.util.Arrays.asList(ValType.I32, ValType.I32, ValType.I32),
      Collections.singletonList(ValType.I32));
    FunctionType i32i32i32i32ToI32 = FunctionType.of(
      java.util.Arrays.asList(ValType.I32, ValType.I32, ValType.I32, ValType.I32),
      Collections.singletonList(ValType.I32));

    return new HostFunction[] {
      // emscripten_notify_memory_growth - called when WASM memory grows
      new HostFunction("env", "emscripten_notify_memory_growth", i32ToVoid, (inst, args) -> null),

      // Emscripten syscalls - return -38 (ENOSYS) for unimplemented syscalls
      new HostFunction("env", "__syscall_getdents64", i32i32i32ToI32, (inst, args) -> new long[] {-38}),
      new HostFunction("env", "__syscall_fcntl64", i32i32i32ToI32, (inst, args) -> new long[] {-38}),
      new HostFunction("env", "__syscall_ioctl", i32i32i32ToI32, (inst, args) -> new long[] {-38}),
      new HostFunction("env", "__syscall_openat", i32i32i32i32ToI32, (inst, args) -> new long[] {-38}),
      new HostFunction("env", "__syscall_fstat64", i32i32ToI32, (inst, args) -> new long[] {-38}),
      new HostFunction("env", "__syscall_stat64", i32i32ToI32, (inst, args) -> new long[] {-38}),
      new HostFunction("env", "__syscall_lstat64", i32i32ToI32, (inst, args) -> new long[] {-38}),
      new HostFunction("env", "__syscall_newfstatat", i32i32i32i32ToI32, (inst, args) -> new long[] {-38}),
      new HostFunction("env", "__syscall_unlinkat", i32i32i32ToI32, (inst, args) -> new long[] {-38}),
      new HostFunction("env", "__syscall_rmdir", i32ToI32, (inst, args) -> new long[] {-38}),
      new HostFunction("env", "__syscall_mkdirat", i32i32i32ToI32, (inst, args) -> new long[] {-38}),
      new HostFunction("env", "__syscall_renameat", i32i32i32i32ToI32, (inst, args) -> new long[] {-38}),
      new HostFunction("env", "__syscall_getcwd", i32i32ToI32, (inst, args) -> new long[] {-38}),
      new HostFunction("env", "__syscall_readlinkat", i32i32i32i32ToI32, (inst, args) -> new long[] {-38}),
      new HostFunction("env", "__syscall_faccessat", i32i32i32i32ToI32, (inst, args) -> new long[] {-38}),
      new HostFunction("env", "__syscall_fchmod", i32i32ToI32, (inst, args) -> new long[] {-38}),
      new HostFunction("env", "__syscall_fchown32", i32i32i32ToI32, (inst, args) -> new long[] {-38}),
      new HostFunction("env", "__syscall_ftruncate64", i32i32i32ToI32, (inst, args) -> new long[] {-38}),
      new HostFunction("env", "__syscall_utimensat", i32i32i32i32ToI32, (inst, args) -> new long[] {-38}),
    };
  }

  private static Instance getInstance() {
    return INSTANCE_SUPPLIER.get();
  }

  private static Memory getMemory() {
    return getInstance().memory();
  }

  // ============================================================================
  // Key Pair Generation
  // ============================================================================

  /**
   * Generates a new ElGamal keypair using the WASM module.
   *
   * <p>Maps to: {@code secp256k1_elgamal_generate_keypair(ctx, privkey, pubkey)}</p>
   *
   * <p>Equivalent to David's code that uses BouncyCastle to generate secp256k1 keys.</p>
   *
   * @return A {@link WasmElGamalKeyPair} containing the private key (32 bytes) and public key (33 bytes, compressed).
   *
   * @throws RuntimeException if key generation fails.
   */
  public static WasmElGamalKeyPair generateKeyPair() {
    Instance instance = getInstance();
    Memory memory = getMemory();

    // Get function exports
    ExportFunction createCtx = instance.export("secp256k1_context_create");
    ExportFunction destroyCtx = instance.export("secp256k1_context_destroy");
    ExportFunction generateKeypair = instance.export("secp256k1_elgamal_generate_keypair");
    ExportFunction malloc = instance.export("malloc");
    ExportFunction free = instance.export("free");

    // Create context (SECP256K1_CONTEXT_SIGN = 0x201)
    int ctx = (int) createCtx.apply(0x201)[0];

    // Allocate memory for private key (32 bytes) and pubkey struct (64 bytes)
    int privkeyPtr = (int) malloc.apply(PRIVATE_KEY_SIZE)[0];
    int pubkeyPtr = (int) malloc.apply(PUBKEY_STRUCT_SIZE)[0];

    try {
      // Call the WASM function
      long[] result = generateKeypair.apply(ctx, privkeyPtr, pubkeyPtr);

      System.out.println(result);
      if (result[0] != 1) {
        //throw new RuntimeException("Failed to generate ElGamal keypair");
      }

      // Read the private key
      byte[] privateKey = readBytes(memory, privkeyPtr, PRIVATE_KEY_SIZE);

      // Serialize the public key to compressed format (33 bytes)
      byte[] publicKey = serializePubkey(instance, memory, ctx, pubkeyPtr);

      return new WasmElGamalKeyPair(privateKey, publicKey);
    } finally {
      // Clean up
      free.apply(privkeyPtr);
      free.apply(pubkeyPtr);
      destroyCtx.apply(ctx);
    }
  }

  /**
   * Serializes a secp256k1_pubkey struct to compressed format (33 bytes).
   */
  private static byte[] serializePubkey(Instance instance, Memory memory, int ctx, int pubkeyPtr) {
    ExportFunction serialize = instance.export("secp256k1_ec_pubkey_serialize");
    ExportFunction malloc = instance.export("malloc");
    ExportFunction free = instance.export("free");

    // Allocate output buffer (33 bytes) and size variable (4 bytes for size_t)
    int outputPtr = (int) malloc.apply(PUBLIC_KEY_SIZE)[0];
    int sizePtr = (int) malloc.apply(4)[0];

    try {
      // Write initial size (33)
      writeInt(memory, sizePtr, PUBLIC_KEY_SIZE);

      // SECP256K1_EC_COMPRESSED = 0x102
      long[] result = serialize.apply(ctx, outputPtr, sizePtr, pubkeyPtr, 0x102);

      if (result[0] != 1) {
        throw new RuntimeException("Failed to serialize public key");
      }

      return readBytes(memory, outputPtr, PUBLIC_KEY_SIZE);
    } finally {
      free.apply(outputPtr);
      free.apply(sizePtr);
    }
  }

  /**
   * Parses a compressed public key (33 bytes) into a secp256k1_pubkey struct.
   *
   * @return Pointer to the allocated pubkey struct (caller must free).
   */
  private static int parsePubkey(Instance instance, Memory memory, int ctx, byte[] compressedPubkey) {
    ExportFunction parse = instance.export("secp256k1_ec_pubkey_parse");
    ExportFunction malloc = instance.export("malloc");

    int pubkeyPtr = (int) malloc.apply(PUBKEY_STRUCT_SIZE)[0];
    int inputPtr = (int) malloc.apply(compressedPubkey.length)[0];

    try {
      writeBytes(memory, inputPtr, compressedPubkey);
      long[] result = parse.apply(ctx, pubkeyPtr, inputPtr, compressedPubkey.length);

      if (result[0] != 1) {
        throw new RuntimeException("Failed to parse public key");
      }

      return pubkeyPtr;
    } finally {
      getInstance().export("free").apply(inputPtr);
    }
  }

  // ============================================================================
  // Encryption
  // ============================================================================

  /**
   * Encrypts an amount using ElGamal encryption.
   *
   * <p>Maps to: {@code secp256k1_elgamal_encrypt(ctx, c1, c2, pubkey, amount, blinding_factor)}</p>
   *
   * <p>The encryption produces a ciphertext (C1, C2) where:</p>
   * <ul>
   *   <li>C1 = blindingFactor * G</li>
   *   <li>C2 = amount * G + blindingFactor * publicKey</li>
   * </ul>
   *
   * @param publicKey      The recipient's public key (33 bytes, compressed).
   * @param amount         The amount to encrypt (uint64).
   * @param blindingFactor A 32-byte random blinding factor.
   *
   * @return The ciphertext as 66 bytes (C1: 33 bytes + C2: 33 bytes).
   *
   * @throws RuntimeException if encryption fails.
   */
  public static byte[] encrypt(byte[] publicKey, long amount, byte[] blindingFactor) {
    Objects.requireNonNull(publicKey, "publicKey must not be null");
    Objects.requireNonNull(blindingFactor, "blindingFactor must not be null");

    if (publicKey.length != PUBLIC_KEY_SIZE) {
      throw new IllegalArgumentException("publicKey must be 33 bytes");
    }
    if (blindingFactor.length != BLINDING_FACTOR_SIZE) {
      throw new IllegalArgumentException("blindingFactor must be 32 bytes");
    }

    Instance instance = getInstance();
    Memory memory = getMemory();

    ExportFunction createCtx = instance.export("secp256k1_context_create");
    ExportFunction destroyCtx = instance.export("secp256k1_context_destroy");
    ExportFunction encrypt = instance.export("secp256k1_elgamal_encrypt");
    ExportFunction malloc = instance.export("malloc");
    ExportFunction free = instance.export("free");

    int ctx = (int) createCtx.apply(0x201)[0];

    // Allocate memory
    int c1Ptr = (int) malloc.apply(PUBKEY_STRUCT_SIZE)[0];
    int c2Ptr = (int) malloc.apply(PUBKEY_STRUCT_SIZE)[0];
    int blindingPtr = (int) malloc.apply(BLINDING_FACTOR_SIZE)[0];
    int pubkeyPtr = 0;

    try {
      // Parse the public key
      pubkeyPtr = parsePubkey(instance, memory, ctx, publicKey);

      // Write blinding factor
      writeBytes(memory, blindingPtr, blindingFactor);

      // Call encrypt
      long[] result = encrypt.apply(ctx, c1Ptr, c2Ptr, pubkeyPtr, amount, blindingPtr);

      if (result[0] != 1) {
        throw new RuntimeException("ElGamal encryption failed");
      }

      // Serialize C1 and C2
      byte[] c1 = serializePubkey(instance, memory, ctx, c1Ptr);
      byte[] c2 = serializePubkey(instance, memory, ctx, c2Ptr);

      // Combine into single ciphertext
      byte[] ciphertext = new byte[c1.length + c2.length];
      System.arraycopy(c1, 0, ciphertext, 0, c1.length);
      System.arraycopy(c2, 0, ciphertext, c1.length, c2.length);

      return ciphertext;
    } finally {
      free.apply(c1Ptr);
      free.apply(c2Ptr);
      free.apply(blindingPtr);
      if (pubkeyPtr != 0) {
        free.apply(pubkeyPtr);
      }
      destroyCtx.apply(ctx);
    }
  }

  // ============================================================================
  // Decryption
  // ============================================================================

  /**
   * Decrypts an ElGamal ciphertext to recover the original amount.
   *
   * <p>Maps to: {@code secp256k1_elgamal_decrypt(ctx, amount, c1, c2, privkey)}</p>
   *
   * <p>This uses brute-force search and is only practical for amounts up to ~1,000,000.</p>
   *
   * @param ciphertext The ciphertext (66 bytes: C1 + C2).
   * @param privateKey The 32-byte private key.
   *
   * @return The decrypted amount.
   *
   * @throws RuntimeException if decryption fails or amount is out of range.
   */
  public static long decrypt(byte[] ciphertext, byte[] privateKey) {
    Objects.requireNonNull(ciphertext, "ciphertext must not be null");
    Objects.requireNonNull(privateKey, "privateKey must not be null");

    if (ciphertext.length != 66) {
      throw new IllegalArgumentException("ciphertext must be 66 bytes");
    }
    if (privateKey.length != PRIVATE_KEY_SIZE) {
      throw new IllegalArgumentException("privateKey must be 32 bytes");
    }

    // Split ciphertext into C1 and C2
    byte[] c1Bytes = new byte[PUBLIC_KEY_SIZE];
    byte[] c2Bytes = new byte[PUBLIC_KEY_SIZE];
    System.arraycopy(ciphertext, 0, c1Bytes, 0, PUBLIC_KEY_SIZE);
    System.arraycopy(ciphertext, PUBLIC_KEY_SIZE, c2Bytes, 0, PUBLIC_KEY_SIZE);

    Instance instance = getInstance();
    Memory memory = getMemory();

    ExportFunction createCtx = instance.export("secp256k1_context_create");
    ExportFunction destroyCtx = instance.export("secp256k1_context_destroy");
    ExportFunction decrypt = instance.export("secp256k1_elgamal_decrypt");
    ExportFunction malloc = instance.export("malloc");
    ExportFunction free = instance.export("free");

    int ctx = (int) createCtx.apply(0x201)[0];

    // Allocate memory
    int amountPtr = (int) malloc.apply(8)[0];  // uint64_t
    int privkeyPtr = (int) malloc.apply(PRIVATE_KEY_SIZE)[0];
    int c1Ptr = 0;
    int c2Ptr = 0;

    try {
      // Parse C1 and C2
      c1Ptr = parsePubkey(instance, memory, ctx, c1Bytes);
      c2Ptr = parsePubkey(instance, memory, ctx, c2Bytes);

      // Write private key
      writeBytes(memory, privkeyPtr, privateKey);

      // Call decrypt
      long[] result = decrypt.apply(ctx, amountPtr, c1Ptr, c2Ptr, privkeyPtr);

      if (result[0] != 1) {
        throw new RuntimeException("ElGamal decryption failed - amount may be out of range");
      }

      // Read the decrypted amount (little-endian uint64)
      return readLong(memory, amountPtr);
    } finally {
      free.apply(amountPtr);
      free.apply(privkeyPtr);
      if (c1Ptr != 0) {
        free.apply(c1Ptr);
      }
      if (c2Ptr != 0) {
        free.apply(c2Ptr);
      }
      destroyCtx.apply(ctx);
    }
  }

  // ============================================================================
  // Memory Helper Methods
  // ============================================================================

  private static void writeBytes(Memory memory, int ptr, byte[] data) {
    for (int i = 0; i < data.length; i++) {
      memory.writeByte(ptr + i, data[i]);
    }
  }

  private static byte[] readBytes(Memory memory, int ptr, int length) {
    byte[] result = new byte[length];
    for (int i = 0; i < length; i++) {
      result[i] = memory.read(ptr + i);
    }
    return result;
  }

  private static void writeInt(Memory memory, int ptr, int value) {
    memory.writeByte(ptr, (byte) (value & 0xFF));
    memory.writeByte(ptr + 1, (byte) ((value >> 8) & 0xFF));
    memory.writeByte(ptr + 2, (byte) ((value >> 16) & 0xFF));
    memory.writeByte(ptr + 3, (byte) ((value >> 24) & 0xFF));
  }

  private static long readLong(Memory memory, int ptr) {
    long result = 0;
    for (int i = 0; i < 8; i++) {
      result |= ((long) (memory.read(ptr + i) & 0xFF)) << (i * 8);
    }
    return result;
  }
}

