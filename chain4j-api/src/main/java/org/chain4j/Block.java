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

import java.util.List;

public interface Block<B extends Block<B, T, I, O>,
                       T extends Transaction<T, I, O>,
                       I extends Input<T, I, O>,
                       O extends Output<T, I, O>>
        extends Hash, Payload, Resolvable<B> {
  /**
   * Block Index.
   *
   * @return index
   */
  long getIndex();

  /**
   * Current version of the software used to mine this block.
   *
   * @return Version
   */
  long getVersion();

  /**
   * Time when the block was mined.
   *
   * @return Mined time
   */
  long getTime();

  /**
   * Provides the previous block chain.
   *
   * @return Previous
   */
  B getPrevious();

  /**
   * Difficulty target.
   *
   * @return target
   */
  long getTarget();

  /**
   * Proof of work of the miner (also called as nonce in bitcoin).
   *
   * @return Nonce
   */
  long getProofOfWork();

  /**
   * All transactions contained in the block.
   *
   * @return All transactions
   */
  List<T> getTransactions();

  /**
   * Transaction hash, usually a merkle hashed based.
   *
   * @return Transaction hash
   */
  byte[] getTransactionsHash();
  
  /**
   * True if this block is the genesis block.
   *
   * @return True if genesis
   */
  boolean isGenesis();
}
