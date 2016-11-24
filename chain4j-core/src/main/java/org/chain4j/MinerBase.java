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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

public abstract class MinerBase<B extends BlockBase<B, T, I, O>,
                                T extends TransactionBase<T, I, O>,
                                I extends InputBase<T, I, O>,
                                O extends OutputBase<T, I, O>>
        extends Chain4jBase
        implements Miner<B, T, I, O> {

  private static final Logger logger = LogManager.getLogger(MinerBase.class);

  @Inject
  private ValidatorRegistery validatorProvider;

  @Inject
  private TimeSupplier timeSupplier;

  @SuppressWarnings("unchecked")
  @Override
  public B mine(B previous, T... transactions) throws MiningException, ValidationException {
    final long startTime = System.nanoTime();

    // 0 - Check eligible transactions
    List<T> validatedTransactions = validateTransactions(transactions);

    // 1 - Create new block
    B mined = createBlock(previous, validatedTransactions);

    // 2 - Resolve / proof of work
    resolve(mined);

    // 3 - Final validation of the mined block 
    validatorProvider.validateAndThrow(mined);

    logger.info("Block mined in {} ms header = {}",
            nanoDiff2Milli(startTime, System.nanoTime()), mined);
    logger.trace("Block mined {}", mined);
    return mined;
  }

  protected List<T> validateTransactions(T[] transactions) {
    List<T> validatedTransactions = new ArrayList<>();
    for (T transaction : transactions) {
      ValidationResult<T> result;
      try {
        result = validatorProvider.validate(transaction);
        if (result == null || result.isOk()) {
          validatedTransactions.add(transaction);
        }
      } catch (ValidationException e) {
        logger.warn("Validation exception on {} got {}", transaction, e.getMessage(), e);
      }
    }
    return validatedTransactions;
  }

  protected void resolve(final B block) throws MiningException {
    AtomicLong attempts = new AtomicLong();

    Optional<HashProofOfWork> resolvedHash = doResolve(block, attempts);

    if (resolvedHash.isPresent()) {
      block.setProofOfWork(resolvedHash.get().getProofOfWork());
      block.setHash(resolvedHash.get().getHash());
    }
    if (block.getHash() == null) {
      throw new MiningException("Unable to resolve block header after "
          + attempts.get() + " attempts");
    }
  }

  protected long maxHashTried() {
    return MAX_UINT;
  }

  protected long now() {
    return timeSupplier.get();
  }

  protected abstract Optional<HashProofOfWork> doResolve(final B block, AtomicLong attempts)
      throws MiningException;

  protected abstract B createBlock(B previous, List<T> transactions);

  protected abstract boolean isResolved(B block, byte[] hash, long proofOfWork);

  protected abstract byte[] computeHash(B block, long nounce, long attempts);
}
