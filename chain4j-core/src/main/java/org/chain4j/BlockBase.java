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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockBase<B extends BlockBase<B, T, I, O>,
                       T extends TransactionBase<T, I, O>,
                       I extends InputBase<T, I, O>,
                       O extends OutputBase<T, I, O>>
             extends PayloadResolvableBase<B>
             implements Block<B, T, I, O> {
  private static final long serialVersionUID = 4705254276283283457L;

  private transient long index;
  private transient long version;
  private transient long time;
  private transient long target;
  private transient long proofOfWork;

  private transient B previous;
  private transient byte[] transactionsHash;
  private transient List<T> transactions;

  public BlockBase() {
  }

  public BlockBase(long index) {
    this.index = index;
  }

  public BlockBase(byte[] hash) {
    super(hash);
  }

  public BlockBase(String hash) throws Chain4jException {
    super(hash);
  }

  public BlockBase(String hash, String payload) throws Chain4jException {
    super(hash, payload);
  }

  /**
   * Block based constructor.
   *
   * @param version Version
   * @param previous Previous block
   * @param transactions Transactions inside this block
   * @param time Time of the block
   * @param target Difficulty target in bits
   * @param proofOfWork Proof of work of mined block
   */
  public BlockBase(long version, B previous,
      List<T> transactions, long time,
      long target, long proofOfWork) {
    super();
    this.version = version;
    this.previous = previous;
    this.transactions = transactions;
    this.transactionsHash = merkelHash(transactions);
    this.time = time;
    this.target = target;
    this.proofOfWork = proofOfWork;
  }

  /**
   * Block based constructor.
   *
   * @param version Version
   * @param previous Previous block
   * @param transactionsHash Transactions hash
   * @param time Time of the block
   * @param target Difficulty target in bits
   * @param proofOfWork Proof of work of mined block
   */
  public BlockBase(long version, B previous,
      byte[] transactionsHash, long time,
      long target, long proofOfWork) {
    super();
    this.version = version;
    this.previous = previous;
    this.transactionsHash = transactionsHash;
    this.time = time;
    this.target = target;
    this.proofOfWork = proofOfWork;
  }

  @Override
  public long getVersion() {
    return version;
  }

  public void setVersion(long version) {
    this.version = version;
  }

  @Override
  public long getTime() {
    return time;
  }
  
  public void setTime(long time) {
    this.time = time;
  }

  @Override
  public long getTarget() {
    return target;
  }

  public void setTarget(long target) {
    this.target = target;
  }

  @Override
  public long getProofOfWork() {
    return proofOfWork;
  }

  public void setProofOfWork(long proofOfWork) {
    this.proofOfWork = proofOfWork;
  }

  @Override
  public B getPrevious() {
    resolvePreviousBlock();
    return getRawPrevious();
  }

  public B getRawPrevious() {
    return previous;
  }

  protected void resolvePreviousBlock() {
    if (isResolved() && !previous.isResolved()) {
      if (getResolver() == null) {
        throw new NotResolvedException(previous);
      }
      try {
        getResolver().resolve(previous);
      } catch (ResolveException e) {
        throw new NotResolvedException(previous, e);
      }
    }
  }

  public void setPrevious(B previous) {
    this.previous = previous;
  }

  @Override
  public List<T> getTransactions() {
    if (transactions == null) {
      transactions = new ArrayList<>();
    }
    return transactions;
  }
  
  public void setTransactions(List<T> transactions) {
    this.transactions = transactions;
  }

  @Override
  public byte[] getTransactionsHash() {
    return transactionsHash;
  }

  public void setTransactionsHash(byte[] transactionsHash) {
    this.transactionsHash = transactionsHash;
  }


  @Override
  public long getIndex() {
    return index;
  }

  public void setIndex(long index) {
    this.index = index;
  }

  @Override
  public boolean isGenesis() {
    return getPrevious().hashCode() == 0;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder(getClass().getSimpleName());
    if (getHash() != null) {
      builder.append("[hash=");
      builder.append(toHexBigEndian(getHash()));
    }
    builder.append(" [index=");
    builder.append(index);
    builder.append(", version=");
    builder.append(version);
    builder.append(", time=");
    builder.append(time);
    builder.append(", target=");
    builder.append(target);
    builder.append(", proofOfWork=");
    builder.append(proofOfWork);
    builder.append(", ");
    builder.append("resolved=");
    builder.append(isResolved());
    builder.append(", ");
    if (getRawPrevious() != null) {
      builder.append("previous=");
      builder.append(getRawPrevious());
      builder.append(", ");
    }
    if (getTransactionsHash() != null) {
      builder.append("transactionsHash=");
      builder.append(toHexBigEndian(getTransactionsHash()));
      builder.append(", ");
    }
    if (transactions != null) {
      builder.append("transactions=");
      builder.append(getTransactions());
    }
    builder.append("]");
    return builder.toString();
  }
  
  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  protected Object clone() throws CloneNotSupportedException {
    BlockBase blockBase = (BlockBase) super.clone();
    blockBase.index = index;
    blockBase.version = version;
    blockBase.time = time;
    blockBase.target = target;
    blockBase.proofOfWork = proofOfWork;
    if (transactionsHash != null) {
      blockBase.transactionsHash = Arrays.copyOf(transactionsHash, transactionsHash.length);
    }
    if (transactions != null) {
      blockBase.transactions = new ArrayList<>(transactions.size());
      for (T transaction : transactions) {
        blockBase.transactions.add(transaction.clone());
      }
    }
    if (previous != null) {
      blockBase.previous = (BlockBase) previous.clone();
    }

    return blockBase;
  }
}
