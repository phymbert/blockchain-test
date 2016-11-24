/**
 * Copyright (c) 2016 Chain4j developers
 * Distributed under the MIT software license, see the accompanying
 * file COPYING or http://www.opensource.org/licenses/mit-license.php.
 */

package org.chain4j.bitcoin;

import org.chain4j.Hash;
import org.chain4j.MinerExecutorBase;

import java.util.List;

public class BitcoinMiner
      extends MinerExecutorBase<BitcoinBlock,
                                BitcoinTransaction,
                                BitcoinTransactionInput,
                                BitcoinTransactionOutput> {

//  private static final Logger logger = LogManager.getLogger(BitcoinMiner.class);
  private long target;
  private long version;

  @Override
  protected BitcoinBlock createBlock(BitcoinBlock previous,
      List<BitcoinTransaction> transactions) {
    final long version = getVersion();
    final long time = getTimeInSeconds();
    final long target = getTarget();

    BitcoinBlock block = new BitcoinBlock(version,
                                          previous,
                                          transactions,
                                          time,
                                          target,
                                          0);
    return block;
  }

  protected long getTimeInSeconds() {
    return getTimeInSeconds(now());
  }

  protected long getTimeInSeconds(long timeMillis) {
    return timeMillis / 1_000;
  }

  @Override
  protected boolean isResolved(BitcoinBlock block, byte[] hash, long nonce) {
    return block.isLowerThanTarget(hash);
  }

  @Override
  protected byte[] computeHash(BitcoinBlock block, long nounce, long attempts) {
    byte[] payload = block.buildPayload(nounce);
    byte[] hash = customHash(payload);
    return hash;
  }

  @Override
  protected byte[] customHash(byte[] input) {
    return hash(hash(input));
  }

  @Override
  protected byte[] merkelHash(List<? extends Hash> transactions) {
    return super.merkelHash(transactions);
  }

  public long getVersion() {
    return version;
  }

  public long getTarget() {
    return target;
  }

  public void setTarget(long target) {
    this.target = target;
  }

  public void setVersion(long version) {
    this.version = version;
  }

}
