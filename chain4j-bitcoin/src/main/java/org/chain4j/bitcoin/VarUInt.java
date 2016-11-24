/**
 * Copyright (c) 2016 Chain4j developers
 * Distributed under the MIT software license, see the accompanying
 * file COPYING or http://www.opensource.org/licenses/mit-license.php.
 */

package org.chain4j.bitcoin;

import org.chain4j.Chain4jBase;

import java.nio.BufferOverflowException;

public class VarUInt extends Chain4jBase {
  private final byte[] value;

  public VarUInt(byte[] value) {
    super();
    this.value = value;
  }

  public VarUInt(long value) {
    super();
    this.value = from(value);
  }

  private byte[] from(long longValue) {
    if (longValue < 0) {
      throw new IllegalArgumentException("Unsigned long only " + longValue + " < 0");
    }
    int size = 1;
    byte prefix = 0;
    if (longValue < 0xFDL) {
      size = 1;
    } else if (longValue <= 0xFFFFL) {
      size = 3;
      prefix = (byte) 0xFD;
    } else if (longValue <= 0xFFFF_FFFFL) {
      size = 5;
      prefix = (byte) 0xFE;
    } else {
      size = 9;
      prefix = (byte) 0xFF;
    }
    byte[] value = new byte[size];
    if (size == 1) {
      value[0] = (byte) longValue;
    } else {
      value[0] = prefix;
      for (int i = 1; i < value.length; i++) {
        value[i] = (byte) ((longValue >> (8 * (i - 1))) & 0xFF);
      }
    }
    return value;
  }

  /**
   * Convert this var uint representation as long.
   *
   * @return This Var unsigned int as long
   */
  public long get() {
    if (value.length == 1) {
      return value[0] & 0xFFL;
    } else {
      long valueLong = 0;
      for (int i = value.length - 1; i > 0; i--) {
        int shift = (i - 1) * 8;
        long unsigned = value[i] & 0xFF;
        if (unsigned != 0 && i > 8 || i == 8 && unsigned > 0x7FL) {
          throw new BufferOverflowException();
        }
        valueLong += unsigned << shift;
      }
      return valueLong;
    }
  }

  @Override
  public String toString() {
    if (value.length == 1) {
      return toHex(value);
    } else {
      byte[] bytes = new byte[value.length - 1];
      System.arraycopy(value, 1, bytes, 0, bytes.length);
      StringBuilder builder = new StringBuilder();
      builder.append(toHex(value[0]));
      builder.append('-');
      builder.append(toHexBigEndian(bytes));
      return builder.toString();
    }
  }

  public int size() {
    return value.length;
  }

  public byte[] array() {
    return value;
  }
}
