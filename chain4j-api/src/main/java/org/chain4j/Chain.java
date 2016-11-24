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

import java.util.Collection;


public interface Chain<B extends Block<B, T, I, O>,
                       T extends Transaction<T, I, O>,
                       I extends Input<T, I, O>,
                       O extends Output<T, I, O>> {

  B getGenesis();

  B getLastBlock();

  void addBlock(B block) throws ValidationException;

  Collection<B> getResolvedBlocks();

  Collection<T> getResolvedTransactions();

  T lookupTransaction(Hash hash) throws ResolveException;

  T lookupTransaction(long index) throws ResolveException;

  B lookupBlock(Hash hash) throws ResolveException;

  B lookupBlock(long index) throws ResolveException;
}
