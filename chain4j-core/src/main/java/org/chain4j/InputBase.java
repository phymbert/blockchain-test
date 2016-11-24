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


public class InputBase<T extends TransactionBase<T, I, O>,
                                           I extends InputBase<T, I, O>,
                                           O extends OutputBase<T, I, O>>
          extends PayloadBase
          implements Input<T, I, O> {

  private static final long serialVersionUID = 5978505343743647355L;

  private transient T previousTransaction;
  
  private transient int index;
  
  private transient int outputIndex;

  private transient List<? extends ContractBase<T, I>> contracts;

  public InputBase() {
  }

  public InputBase(List<? extends ContractBase<T, I>> contracts) {
    this.contracts = contracts;
  }

  public InputBase(byte[] hash) {
    super(hash);
  }

  public InputBase(byte[] hash, byte[] payload) {
    super(hash, payload);
  }

  @Override
  public List<? extends Contract<T, I>> getContracts() {
    if (contracts == null) {
      contracts = new ArrayList<>();
    }
    return contracts;
  }
  
  @SuppressWarnings({"unchecked", "rawtypes"}) // FIXME
  protected <C extends ContractBase<T, I>> void addContract(C contract) {
    ((List) getContracts()).add(contract);
  }

  @Override
  public T getPreviousTransaction() {
    return previousTransaction;
  }

  public void setPreviousTransaction(T previousTransaction) {
    this.previousTransaction = previousTransaction;
  }

  @Override
  public int getOutputIndex() {
    return outputIndex;
  }

  public void setOutputIndex(int outputIndex) {
    this.outputIndex = outputIndex;
  }

  @Override
  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder(getClass().getSimpleName());
    builder.append(" [");
    if (getContracts() != null) {
      builder.append("getContracts()=");
      builder.append(getContracts());
      builder.append(", ");
    }
    if (getPreviousTransaction() != null) {
      builder.append("getPreviousTransaction()=");
      builder.append(getPreviousTransaction());
      builder.append(", ");
    }
    builder.append("getOutputIndex()=");
    builder.append(getOutputIndex());
    builder.append("]");
    return builder.toString();
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  protected Object clone() throws CloneNotSupportedException {
    InputBase outputBase = (InputBase) super.clone();
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
