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
import com.dylibso.chicory.runtime.Instance;
import com.dylibso.chicory.runtime.Memory;
import com.dylibso.chicory.wasm.Parser;
import com.dylibso.chicory.wasm.WasmModule;
import com.google.common.base.Suppliers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Low-level wrapper for the MPT Crypto WASM module.
 *
 * <p>This class provides direct access to the WASM functions for ElGamal encryption,
 * Bulletproof range proofs, and other cryptographic operations needed for
 * Confidential Transfers on the XRP Ledger.</p>
 *
 * <p>The WASM module is loaded lazily on first use and cached for subsequent calls.</p>
 *
 * <h2>Memory Management</h2>
 * <p>WASM functions operate on linear memory. This class handles:</p>
 * <ul>
 *   <li>Allocating memory for input/output buffers using {@code malloc}</li>
 *   <li>Copying data between Java byte arrays and WASM memory</li>
 *   <li>Freeing allocated memory using {@code free}</li>
 * </ul>
 *
 * <h2>secp256k1 Context</h2>
 * <p>Most cryptographic functions require a secp256k1 context. Create one with
 * {@link #createContext()} and destroy it with {@link #destroyContext(int)} when done.</p>
 */
public final class MptCryptoWasm {

  private static final String WASM_RESOURCE_PATH = "/mptcryptowasm/mpt_crypto.wasm";

  // Lazily initialized WASM instance
  private static final Supplier<Instance> INSTANCE_SUPPLIER =
    Suppliers.memoize(MptCryptoWasm::loadWasmInstance);

  // Size constants for cryptographic data
  public static final int PRIVATE_KEY_SIZE = 32;
  public static final int PUBLIC_KEY_SIZE = 33;  // Compressed secp256k1 public key
  public static final int BLINDING_FACTOR_SIZE = 32;
  public static final int CIPHERTEXT_SIZE = 66;  // Two compressed points: C1 (33) + C2 (33)

  private MptCryptoWasm() {
  }

  /**
   * Loads the WASM module from the classpath.
   */
  private static Instance loadWasmInstance() {
    try (InputStream wasmStream = MptCryptoWasm.class.getResourceAsStream(WASM_RESOURCE_PATH)) {
      if (wasmStream == null) {
        throw new IllegalStateException("WASM module not found on classpath: " + WASM_RESOURCE_PATH);
      }
      WasmModule module = Parser.parse(wasmStream);
      return Instance.builder(module).build();
    } catch (IOException e) {
      throw new RuntimeException("Failed to load WASM module from " + WASM_RESOURCE_PATH, e);
    }
  }

  private static Instance getInstance() {
    return INSTANCE_SUPPLIER.get();
  }

  private static Memory getMemory() {
    return getInstance().memory();
  }

  // ============================================================================
  // Memory Management Functions
  // ============================================================================

  /**
   * Allocates memory in the WASM linear memory.
   *
   * @param size Number of bytes to allocate.
   * @return Pointer (offset) to the allocated memory.
   */
  public static int malloc(int size) {
    ExportFunction mallocFn = getInstance().export("malloc");
    long[] result = mallocFn.apply(size);
    return (int) result[0];
  }

  /**
   * Frees previously allocated memory.
   *
   * @param ptr Pointer to the memory to free.
   */
  public static void free(int ptr) {
    ExportFunction freeFn = getInstance().export("free");
    freeFn.apply(ptr);
  }

  // ============================================================================
  // secp256k1 Context Management
  // ============================================================================

  /**
   * Creates a secp256k1 context required for cryptographic operations.
   *
   * <p>The context must be destroyed with {@link #destroyContext(int)} when no longer needed.</p>
   *
   * @return Pointer to the created context.
   */
  public static int createContext() {
    ExportFunction createCtxFn = getInstance().export("secp256k1_context_create");
    // SECP256K1_CONTEXT_SIGN | SECP256K1_CONTEXT_VERIFY = 0x301
    long[] result = createCtxFn.apply(0x301);
    return (int) result[0];
  }

  /**
   * Destroys a secp256k1 context.
   *
   * @param ctx Pointer to the context to destroy.
   */
  public static void destroyContext(int ctx) {
    ExportFunction destroyCtxFn = getInstance().export("secp256k1_context_destroy");
    destroyCtxFn.apply(ctx);
  }

  // ============================================================================
  // Helper Methods for Memory Operations
  // ============================================================================

  /**
   * Writes a byte array to WASM memory at the specified pointer.
   */
  static void writeBytes(int ptr, byte[] data) {
    Memory memory = getMemory();
    for (int i = 0; i < data.length; i++) {
      memory.writeByte(ptr + i, data[i]);
    }
  }

  /**
   * Reads bytes from WASM memory into a new byte array.
   */
  static byte[] readBytes(int ptr, int length) {
    Memory memory = getMemory();
    byte[] result = new byte[length];
    for (int i = 0; i < length; i++) {
      result[i] = memory.read(ptr + i);
    }
    return result;
  }
}

