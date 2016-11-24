/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the LICENSE.txt file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.chain4j;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Chain4jBase {
  private static Logger logger = LogManager.getLogger(Chain4jBase.class);

  public static final float NANO = 1_000_000f;

  public static final int BYTE = 8;

  public static final int SHORT_BYTES = 16 / BYTE;
  public static final int USHORT_BYTES = SHORT_BYTES;

  public static final int INT_BYTES = 32 / BYTE;
  public static final int UINT_BYTES = INT_BYTES;

  public static final int LONG_BYTES = 64 / BYTE;
  public static final int ULONG_BYTES = LONG_BYTES;
  public static final long MAX_UINT = 0xFFFF_FFFFL;
  public static final long MAX_U64  = 0xFFFF_FFFF_FFFF_FFFFL;

  protected Digest getHashDigest() {
    return new SHA256Digest();
  }

  protected int getHashBytesSize() {
    return getHashDigest().getDigestSize();
  }

  protected float nanoDiff2Milli(long startTime, long nanoTime) {
    return nanoDiff(startTime, nanoTime) / NANO;
  }

  protected float nanoDiff(long startTime, long nanoTime) {
    return (nanoTime - startTime);
  }
  
  protected String toHexBigEndian(byte[] hash) {
    byte[] bigEndianHash = toBigEndian(hash);
    return toHex(bigEndianHash, bigEndianHash.length);
  }

  protected byte[] toBigEndian(byte[] hash) {
    byte[] bigEndianHash = new byte[hash.length];
    for (int i = 0; i < bigEndianHash.length; i++) {
      bigEndianHash[i] = hash[hash.length - i - 1];
    }
    return bigEndianHash;
  }

  protected BigInteger bigInteger(byte[] hashBigEndian) {
    return new BigInteger(1, hashBigEndian);
  }

  protected String toHex256(byte[] hash) {
    return toHex(hash, 256 / BYTE);
  }
  
  protected String toHex(byte[] hash) {
    if (hash == null) {
      return null;
    }
    return toHex(hash, hash.length);
  }
  
  protected String toHex(byte oneByte) {
    return toHex(new byte[] {oneByte}, 1);
  }

  protected String toHex(byte[] hash, int size) {
    if (hash == null) {
      return null;
    }
    final int sizeHex = size * 2;
    StringBuilder builder = new StringBuilder(sizeHex);
    builder.append(Hex.encodeHex(hash, false));
    builder.insert(0, StringUtils.repeat('0', sizeHex - builder.length()));
    return builder.toString();
  }

  protected byte[] fromHex(String hex) throws Chain4jException {
    try {
      return Hex.decodeHex(hex.toCharArray());
    } catch (DecoderException e) {
      throw new Chain4jException(e);
    }
  }

  protected byte[] fromHexBigEndian(String hex) throws Chain4jException {
    byte[] array = fromHex(hex);
    ArrayUtils.reverse(array);
    return array;
  }

  protected byte[] merkelHash(List<? extends Hash> transactions) {
    if (transactions == null || transactions.isEmpty()) {
      return new byte[getHashBytesSize()];
    }

    List<byte[]> hashes = new ArrayList<>();
    int size = 0;
    for (Hash hashBean : transactions) {
      byte[] hash = hashBean.getHash();
      size = Math.max(hash.length, size);
      hashes.add(hash);
    }

    return merkeHashes(hashes);
  }

  protected byte[] merkeHashes(List<byte[]> hashes) {
    while (hashes.size() != 1) {
      hashes = merkelHashes(hashes);
    }

    return hashes.get(0);
  }

  private List<byte[]> merkelHashes(List<byte[]> hashes) {
    List<byte[]> parentHash = new ArrayList<byte[]>();
    int index = 0;
    while (index < hashes.size()) {
      // left
      byte[] left = hashes.get(index++);

      // right
      byte[] right = null;
      if (index < hashes.size()) {
        right = hashes.get(index++);
      } else {
        right = left; // that's an odd number, so we take the left twice
      }

      byte[] leftAndright = ByteBuffer
          .allocate(left.length + right.length)
          .put(left)
          .put(right)
          .array();

      parentHash.add(customHash(leftAndright));
    }
    return parentHash;
  }

  protected byte[] customHash(byte[] input) {
    return hash(input);
  }

  protected byte[] hash(byte[] input) {
    return hash(input, getHashDigest());
  }

  /**
   * Generate hash of the given input using the given Digest.
   * 
   * @param input
   *            byte[] input
   * @param digest
   *            The {@link Digest} to use for hashing
   * @return Hash
   */
  protected byte[] hash(byte[] input, Digest digest) {
    byte[] hash = new byte[digest.getDigestSize()];
    digest.update(input, 0, input.length);
    digest.doFinal(hash, 0);
    return hash;
  }

  protected boolean ecdsaCheckSignature(byte[] pub, byte[] sig, byte[] hash) {
    boolean result = false;
    X9ECParameters curve = SECNamedCurves.getByName("secp256k1");
    ECDomainParameters domain = new ECDomainParameters(
        curve.getCurve(), curve.getG(), curve.getN(), curve.getH());
    ASN1InputStream asn1 = new ASN1InputStream(sig);
    try {
      ECDSASigner signer = new ECDSASigner();
      signer.init(false, new ECPublicKeyParameters(curve.getCurve().decodePoint(pub), domain));

      DLSequence seq = (DLSequence) asn1.readObject();
      BigInteger rr = ((ASN1Integer) seq.getObjectAt(0)).getPositiveValue();
      BigInteger ss = ((ASN1Integer) seq.getObjectAt(1)).getPositiveValue();
      result = signer.verifySignature(hash, rr, ss);
    } catch ( Exception e ) {
      logger .trace("Signature check failed " + e, e);
      // Consider exception as verification failed
      result = false;
    } finally {
      try {
        asn1.close();
      } catch ( IOException e ) {
        logger.warn("Unable to close stream " + e, e);
      }
    }
    return result;
  }

  protected long leToLong(byte[] bytes) {
    if (bytes.length != 4) {
      throw new IllegalArgumentException("Unsigned long only " + bytes.length + " != 4");
    }
    // FIXME Optimize
    return ((ByteBuffer) ByteBuffer.allocate(8)
        .order(java.nio.ByteOrder.LITTLE_ENDIAN)
        .put(bytes)
        .position(0)).getLong();
  }

  protected byte[] longToLe(long val) {
    if (val < 0) {
      throw new IllegalArgumentException("Unsigned long only " + val + " < 0");
    }
    // FIXME Optimize
    byte[] bytes = ByteBuffer.allocate(8)
        .order(java.nio.ByteOrder.LITTLE_ENDIAN)
        .putLong(val).array();
    byte[] fourBytes = new byte[INT_BYTES];
    for (int i = 0; i < fourBytes.length; i++) {
      if (bytes[bytes.length - i - 1] != 0) {
        throw new BufferOverflowException();
      }
      fourBytes[i] = bytes[i];
    }
    return fourBytes;
  }
}
