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

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;


public class ChainBase<B extends BlockBase<B, T, I, O>,
                       T extends TransactionBase<T, I, O>,
                       I extends InputBase<T, I, O>,
                       O extends OutputBase<T, I, O>>
         implements Chain<B, T, I, O>, Serializable {

  private static final long serialVersionUID = 4805254276283283457L;
  
  private final Class<B> blocksType;
  
  private final Class<T> transactionsType;

  private B genesis;

  private B lastBlock;

  private transient Map<Long, B> blocksIndexes = new ConcurrentHashMap<>();

  private transient Map<Integer, B> blocksHashes = new ConcurrentHashMap<>();

  private transient Map<Long, T> transactionsIndexes = new ConcurrentHashMap<>();

  private transient Map<Integer, T> transactionsHashes = new ConcurrentHashMap<>();

  @Inject
  private ValidatorRegistery validatorRegistery;

  @Inject
  private PayloadResolverRegistery resolverRegistery;

  public ChainBase(Class<B> blocksType, Class<T> transactionsType) {
    this.blocksType = blocksType;
    this.transactionsType = transactionsType;
  }

  /**
   * Chain constructor.
   *
   * @param blocksType Blocks type
   * @param transactionsType Transactions type
   * @param genesis Genesis of the block
   * @throws InvalidChainState If issue with genesis block
   */
  public ChainBase(Class<B> blocksType, Class<T> transactionsType,
                   B genesis) {
    this(blocksType, transactionsType);
    this.genesis = genesis;
    try {
      addBlock(genesis);
    } catch (ValidationException e) {
      throw new InvalidChainState(e);
    }
  }

  /**
   * Chain constructor.
   *
   * @param blocksType Blocks type
   * @param transactionsType Transactions type
   * @param genesis Genesis of the block
   * @param lastBlock Last block of the chain
   * @throws ValidationException If issue with genesis or last block
   */
  public ChainBase(Class<B> blocksType, Class<T> transactionsType,
                   B genesis, B lastBlock) throws ValidationException {
    this(blocksType, transactionsType, genesis);
    addBlock(lastBlock);
  }

  @Override
  public final B getGenesis() {
    return genesis;
  }

  @Override
  public final B getLastBlock() {
    return lastBlock;
  }

  @Override
  public Collection<B> getResolvedBlocks() {
    return blocksHashes.values();
  }

  @Override
  public Collection<T> getResolvedTransactions() {
    return transactionsHashes.values();
  }

  @Override
  public void addBlock(B block) throws ValidationException {
    validatorRegistery.validateAndThrow(block);

    if (!lastBlock.equals(block.getPrevious())) {
      throw new ValidationException(
          BlockValidationCode.result(BlockValidationCode.NOT_MAIN_CHAIN));
    }

    if (lastBlock.getTime() > block.getTime()) {
      throw new ValidationException(
          BlockValidationCode.result(BlockValidationCode.OUTDATED));
    }

    if (lastBlock.getVersion() > block.getVersion()) {
      throw new ValidationException(
          BlockValidationCode.result(BlockValidationCode.OUTDATED));
    }

    if (block.isResolved()) {
      registerBlock(block);
    } else {
      throw new ValidationException(
          BlockValidationCode.result(BlockValidationCode.NOT_RESOLVED));
    }

    this.lastBlock = block;
  }

  protected void registerBlock(B block) {
    blocksIndexes.put(block.getIndex(), block);
    blocksHashes.put(block.hashCode(), block);
    for (T transaction : block.getTransactions()) {
      if (transaction.isResolved()) {
        registerTransaction(transaction);
      }
    }
  }

  protected void registerTransaction(T transaction) {
    transactionsIndexes.put(transaction.getIndex(), transaction);
    transactionsHashes.put(transaction.hashCode(), transaction);
  }

  @Override
  public T lookupTransaction(Hash hash) throws ResolveException {
    T transaction = transactionsHashes.get(hash.hashCode());
    if (transaction == null) {
      transaction = resolverRegistery.resolve(transactionsType, hash);
      if (transaction == null) {
        throw new ResolveException(); // FIXME details
      }
      registerTransaction(transaction);
    }
    return transaction;
  }

  @Override
  public T lookupTransaction(long index) throws ResolveException {
    T transaction = transactionsIndexes.get(index);
    if (transaction == null) {
      transaction = resolverRegistery.resolve(transactionsType, index);
      if (transaction == null) {
        throw new ResolveException(); // FIXME details
      }
      registerTransaction(transaction);
    }
    return transaction;
  }

  @Override
  public B lookupBlock(Hash hash) throws ResolveException {
    B block = blocksIndexes.get(hash.hashCode());
    if (block == null) {
      block = resolverRegistery.resolve(blocksType, hash);
      if (block == null) {
        throw new ResolveException(); // FIXME details
      }
      registerBlock(block);
    }
    return block;
  }

  @Override
  public B lookupBlock(long index) throws ResolveException {
    B block = blocksIndexes.get(index);
    if (block == null) {
      block = resolverRegistery.resolve(blocksType, index);
      if (block == null) {
        throw new ResolveException(); // FIXME details
      }
      registerBlock(block);
    }
    return block;
  }
}
