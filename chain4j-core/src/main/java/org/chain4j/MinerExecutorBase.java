/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the LICENSE.txt file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.chain4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public abstract class MinerExecutorBase<B extends BlockBase<B, T, I, O>,
                                        T extends TransactionBase<T, I, O>,
                                        I extends InputBase<T, I, O>,
                                        O extends OutputBase<T, I, O>>
              extends MinerBase<B, T, I, O> {

  private static final int      CPU         = Runtime.getRuntime().availableProcessors();
  private static final long     TRUNK_SIZE  = MAX_UINT / CPU;
  private static final Logger   logger      = LogManager.getLogger(MinerExecutorBase.class);
  private static final boolean  infoEnabled = logger.isInfoEnabled();
  private final ExecutorService executor    = Executors.newFixedThreadPool(CPU);

  @Override
  protected Optional<HashProofOfWork> doResolve(final B block, AtomicLong attempts) {
    final long startTime = System.nanoTime();
    final AtomicReference<HashProofOfWork> resolved = new AtomicReference<>();

    long maxTry = maxHashTried();
    for (long i = 0; i < maxTry; i += TRUNK_SIZE) {
      if (resolved.get() != null) {
        break;
      }
      final long start = i;
      executor
          .submit(() -> {
            long end = start + TRUNK_SIZE;
            for (long nonce = start; nonce < end; nonce++) {
              if (resolved.get() != null) {
                return;
              }
              long localAttempts = attempts.incrementAndGet();
              byte[] hash = computeHash(block, nonce, localAttempts);
              if (localAttempts % 1_000_000 == 0) {
                float diffInNano = nanoDiff(startTime, System.nanoTime());
                if (infoEnabled) {
                  logger
                      .info("{} GAttempts/s {}ns per custom hash"
                          + " out of {} attempts got current {} {}<={}<{}",
                            localAttempts / diffInNano * 1_000 * NANO / 1_000_000,
                            diffInNano / localAttempts,
                            localAttempts,
                            toHexBigEndian(hash),
                            start,
                            nonce,
                            end);
                }
              }
              if (isResolved(block, hash, nonce)) {
                HashProofOfWork hashAndProof = new HashProofOfWork(hash, nonce);
                resolved.set(hashAndProof);
              }
            }
          });
    }
    executor.shutdownNow();
    try {
      executor.awaitTermination(1, TimeUnit.HOURS);
    } catch (InterruptedException e) {
      logger.error("Not resolved " + e, e);
    }
    return Optional.ofNullable(resolved.get());
  }

}
