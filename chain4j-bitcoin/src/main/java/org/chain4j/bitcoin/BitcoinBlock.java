/**
 * Copyright (c) 2016 Chain4j developers
 * Distributed under the MIT software license, see the accompanying
 * file COPYING or http://www.opensource.org/licenses/mit-license.php.
 */

package org.chain4j.bitcoin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.chain4j.BlockBase;
import org.chain4j.Chain4jException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public class BitcoinBlock extends BlockBase<BitcoinBlock,
                                            BitcoinTransaction,
                                            BitcoinTransactionInput,
                                            BitcoinTransactionOutput> {

  private static final long serialVersionUID = -3557050205622780114L;

  private static final Logger logger = LogManager.getLogger(BitcoinBlock.class);
  private static final boolean traceEnabled = logger.isTraceEnabled();

  public static final long GENESIS_TARGET = 0x1d00ffff;


  private transient byte[] versionAsLittleEndian;
  private transient byte[] timeAsLittleEndian;
  private transient byte[] targetAsLittleEndian;
  private transient byte[] targetExpandedAsLittleEndian;
  private transient byte[] targetExpandedAsBigEndian;

  public BitcoinBlock() {
  }
  
  public BitcoinBlock(String hash) throws Chain4jException {
    super(hash);
  }
  
  public BitcoinBlock(byte[] hash) {
    super(hash);
  }

  public BitcoinBlock(long version, BitcoinBlock previous,
                      List<BitcoinTransaction> transactions, long time,
                      long target, long nonce) {
    super(version, previous, transactions, time, target, nonce);
  }

  public BitcoinBlock(long version, BitcoinBlock previous,
                      byte[] transactionsHash, long time,
                      long target, long nonce) {
    super(version, previous, transactionsHash, time, target, nonce);
  }

  /**
   * Json based constructor.
   *
   * @param jo Json Object
   * @throws Chain4jException If error during unmarshalling
   */
  public BitcoinBlock(JsonObject jo) throws Chain4jException {
    setHash(fromHexBigEndian(jo.get("blockhash").getAsString()));
    setIndex(jo.get("block_no").getAsLong());
    setVersion(jo.get("version").getAsLong());
    setTime(jo.get("time").getAsLong());
    setPrevious(new BitcoinBlock(fromHexBigEndian(jo.get("previous_blockhash").getAsString())));
    setTransactionsHash(fromHexBigEndian(jo.get("merkleroot").getAsString()));
    setTarget(leToLong(fromHexBigEndian(jo.get("bits").getAsString())));
    setProofOfWork(jo.get("nonce").getAsLong());

    for (JsonElement txElem : jo.get("txs").getAsJsonArray()) {
      BitcoinTransaction transaction = new BitcoinTransaction(txElem.getAsJsonObject());
      if (transaction.getVersion() == 0) {
        transaction.setVersion(getVersion());
      }
      getTransactions().add(transaction);
    }
  }

  public double difficulty() {
    return difficulty(getTarget());
  }

  protected double difficulty(long target) {
    return              new BigDecimal(bigInteger(targetToBytesBigEndian(GENESIS_TARGET)))
          .divide(new BigDecimal(bigInteger(targetToBytesBigEndian(target))),
            2, RoundingMode.HALF_UP).doubleValue();
  }

  protected byte[] targetToBytesBigEndian(long target) {
    return targetToBytesBigEndian(target, -1);
  }

  protected byte[] targetToBytesBigEndian(long target, int size) {
    int targetSize = (int) target >> 24;
    long targetWord = target & 0x007fffff;
    ByteBuffer buffer = ByteBuffer.allocate(Math.max(targetSize, size))
          .order(ByteOrder.BIG_ENDIAN)
          .putLong(targetWord);

    return buffer.array();
  }

  protected byte[] targetToBytesLittleEndian(long target, int size) {
    int targetSize = (int) target >> 24;
    int finalSize = Math.max(targetSize, size);
    long targetWord  = target & 0x007fffff;
    ByteBuffer buffer = ((ByteBuffer) ByteBuffer.allocate(finalSize)
          .order(ByteOrder.LITTLE_ENDIAN)
          .position(finalSize - LONG_BYTES))
          .putLong(targetWord);
    return buffer.array();
  }

  protected String toStringFull() {
    StringBuilder builder = new StringBuilder(getClass().getSimpleName());
    builder.append(" [hash=" + toHexBigEndian(getHash()));
    // FIXME
    builder.append(", version=" + getVersion() + ", previous="
        + toHexBigEndian(getPrevious().getHash()) + ", transactions="
        + toHexBigEndian(getTransactionsHash()) + ", time=" + getTime()
        + ", target=" + getTarget() + ", nounce=" + getProofOfWork()
        + ", difficulty=" + difficulty() + "]");
    return builder.toString();
  }

  @Override
  protected byte[] buildPayload() {
    return buildPayload(getProofOfWork());
  }

  protected byte[] buildPayload(long nounce) {
    byte[] wrapped = new byte[getHeaderSize()];
    ByteBuffer buffer = ByteBuffer.wrap(wrapped);
    buffer.put(getVersionAsLittleEndian());
    buffer.put(getPrevious().getHash());
    buffer.put(getTransactionsHash());
    buffer.put(getTimeAsLittleEndian());
    buffer.put(getTargetAsLittleEndian());
    buffer.put(longToLe(nounce));
    return wrapped;
  }

  protected byte[] getVersionAsLittleEndian() {
    if (versionAsLittleEndian == null) {
      synchronized (this) {
        if (versionAsLittleEndian == null) {
          versionAsLittleEndian = longToLe(getVersion());
        }
      }
    }
    return versionAsLittleEndian;
  }

  protected byte[] getTimeAsLittleEndian() {
    if (timeAsLittleEndian == null) {
      synchronized (this) {
        if (timeAsLittleEndian == null) {
          timeAsLittleEndian = longToLe(getTime());
        }
      }
    }
    return timeAsLittleEndian;
  }

  protected byte[] getTargetAsLittleEndian() {
    if (targetAsLittleEndian == null) {
      synchronized (this) {
        if (targetAsLittleEndian == null) {
          targetAsLittleEndian = longToLe(getTarget());
        }
      }
    }
    return targetAsLittleEndian;
  }

  protected byte[] getTargetExpandedAsLittleEndian() {
    if (targetExpandedAsLittleEndian == null) {
      synchronized (this) {
        if (targetExpandedAsLittleEndian == null) {
          targetExpandedAsLittleEndian = targetToBytesLittleEndian(getTarget(), getHashBytesSize());
        }
      }
    }
    return targetExpandedAsLittleEndian;
  }

  protected byte[] getTargetExpandedAsBigEndian() {
    if (targetExpandedAsBigEndian == null) {
      synchronized (this) {
        if (targetExpandedAsBigEndian == null) {
          targetExpandedAsBigEndian = targetToBytesBigEndian(getTarget(), getHashBytesSize());
        }
      }
    }
    return targetExpandedAsBigEndian;
  }

  private int getHeaderSize() {
    return  UINT_BYTES
          + getPrevious().getHash().length
          + getTransactionsHash().length
          + UINT_BYTES
          + UINT_BYTES
          + UINT_BYTES;
  }

  protected boolean isLowerThanTarget(byte[] hash) {
    byte[] target = getTargetExpandedAsLittleEndian();
    if (traceEnabled) {
      logger.trace("{} ?> {}", toHex(target), toHex(hash));
    }
    if (hash.length != target.length) {
      throw new IllegalArgumentException(hash.length
          + " != " + target.length);
    }
    for (int i = hash.length - 1; i >= 0; i--) {
      int targetValue = target[i] & 0xFF;
      int hashValue = hash[i] & 0xFF;
      if (hashValue == targetValue) {
        continue;
      } else if (hashValue > targetValue) {
        return false;
      } else {
        break;
      }
    }
    return true;
  }
}
