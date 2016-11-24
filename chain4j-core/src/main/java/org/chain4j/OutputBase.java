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

import java.util.ArrayList;
import java.util.List;


public class OutputBase<T extends TransactionBase<T, I, O>,
                                            I extends InputBase<T, I, O>,
                                            O extends OutputBase<T, I, O>>
          extends PayloadBase
          implements Output<T, I, O> {
  private static final long serialVersionUID = 5978505343743647355L;
  
  private transient int index;

  private transient List<? extends ContractBase<T, O>> contracts;

  public OutputBase() {
  }

  public OutputBase(List<? extends ContractBase<T, O>> contracts) {
    this.contracts = contracts;
  }

  public OutputBase(byte[] hash) {
    super(hash);
  }

  public OutputBase(byte[] hash, byte[] payload) {
    super(hash, payload);
  }

  @Override
  public List<? extends Contract<T, O>> getContracts() {
    if (contracts == null) {
      contracts = new ArrayList<>();
    }
    return contracts;
  }

  @Override
  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  @SuppressWarnings({"unchecked", "rawtypes"}) // FIXME
  protected <C extends ContractBase<T, O>> void addContract(C contract) {
    ((List) getContracts()).add(contract);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder(getClass().getSimpleName());
    builder.append(" [");
    if (getContracts() != null) {
      builder.append("getContracts()=");
      builder.append(getContracts());
    }
    builder.append("]");
    return builder.toString();
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  protected Object clone() throws CloneNotSupportedException {
    OutputBase outputBase = (OutputBase) super.clone();
    outputBase.index = index;
    if (contracts != null) {
      outputBase.contracts = new ArrayList<>(contracts.size());
      for (ContractBase contract : contracts) {
        outputBase.contracts.add(contract.clone());
      }
    }
    return outputBase;
  }
}
