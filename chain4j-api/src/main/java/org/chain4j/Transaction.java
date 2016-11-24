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

public interface Transaction<T extends Transaction<T, I, O>,
                             I extends Input<T, I, O>,
                             O extends Output<T, I, O>>
          extends Hash, Payload, Resolvable<T>, ContractContext {
  /**
   * Transaction Index.
   *
   * @return index
   */
  long getIndex();

  /**
   * Inputs of this transaction.
   * @see Input
   * @return Inputs
   */
  List<I> getInputs();

  /**
   * Outputs of this transaction.
   * @see Output
   * @return Outputs
   */
  List<O> getOutputs();

  /**
   * Time at which transaction was included in a block.
   *
   * @return Time
   */
  long getTime();
}
