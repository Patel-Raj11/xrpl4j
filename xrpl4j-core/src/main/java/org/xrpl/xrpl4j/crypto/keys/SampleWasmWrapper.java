package org.xrpl.xrpl4j.crypto.keys;

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
import com.dylibso.chicory.wasm.Parser;
import com.dylibso.chicory.wasm.WasmModule;
import com.google.common.base.Suppliers;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

/**
 * Sample wrapper class demonstrating how to call functions from a WebAssembly module using the Chicory runtime.
 *
 * <p>This class loads a WASM module from the classpath and exposes its exported functions
 * as Java methods. The WASM module is expected to be located at {@code /sample.wasm} on the classpath.</p>
 *
 * <p>Expected WASM exports:</p>
 * <ul>
 *   <li>{@code add(i32, i32) -> i32} - Adds two integers</li>
 * </ul>
 */
public final class SampleWasmWrapper {

  private static final String WASM_RESOURCE_PATH = "/simple_functions.wasm";

  // Lazily initialized WASM instance - loaded only once and reused
  private static final Supplier<Instance> INSTANCE_SUPPLIER =
    Suppliers.memoize(SampleWasmWrapper::loadWasmInstance);

  /**
   * No-args Constructor to prevent instantiation.
   */
  private SampleWasmWrapper() {
  }

  /**
   * Loads the WASM module from the classpath and instantiates it.
   *
   * @return An {@link Instance} of the WASM module.
   *
   * @throws RuntimeException if the WASM module cannot be loaded or instantiated.
   */
  private static Instance loadWasmInstance() {
    try (InputStream wasmStream = SampleWasmWrapper.class.getResourceAsStream(WASM_RESOURCE_PATH)) {
      if (wasmStream == null) {
        throw new IllegalStateException(
          "WASM module not found on classpath: " + WASM_RESOURCE_PATH
        );
      }

      WasmModule module = Parser.parse(wasmStream);
      return Instance.builder(module).build();
    } catch (IOException e) {
      throw new RuntimeException("Failed to load WASM module from " + WASM_RESOURCE_PATH, e);
    }
  }

  /**
   * Gets the lazily-initialized WASM instance.
   *
   * @return The {@link Instance} of the WASM module.
   */
  private static Instance getInstance() {
    return INSTANCE_SUPPLIER.get();
  }

  /**
   * Adds two integers using the WASM module's {@code add} function.
   *
   * @param a The first integer operand.
   * @param b The second integer operand.
   *
   * @return The sum of {@code a} and {@code b}.
   */
  public static int add(int a, int b) {
    Instance instance = getInstance();

    // Get the exported "add" function from the WASM module
    ExportFunction addFunction = instance.export("add");

    // Call the function with the two integer arguments
    // WASM uses i32 for integers, which maps to Java int/long
    long[] result = addFunction.apply(a, b);

    // The result array contains the return value(s)
    // For a single i32 return, we get one element
    return (int) result[0];
  }
}

