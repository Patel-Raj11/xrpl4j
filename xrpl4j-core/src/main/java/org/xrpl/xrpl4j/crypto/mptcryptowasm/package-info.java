/**
 * This package contains WASM-based implementations of MPT (Multi-Party Token) cryptographic operations.
 *
 * <p>The WASM binary provides the same functionality as the C implementation in the {@code mpt} package,
 * allowing for comparison and testing between the native Java port and the WASM-based approach.</p>
 *
 * <p>Key functionality includes:</p>
 * <ul>
 *   <li>ElGamal encryption/decryption for confidential balances</li>
 *   <li>Bulletproof range proofs</li>
 *   <li>Equality proofs</li>
 *   <li>Zero-knowledge proofs for confidential transfers</li>
 * </ul>
 *
 * @see org.xrpl.xrpl4j.crypto.mpt
 */
package org.xrpl.xrpl4j.crypto.mptcryptowasm;

