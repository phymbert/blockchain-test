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
import java.util.Collections;
import java.util.List;

public class TransactionBase<T extends TransactionBase<T, I, O>,
                             I extends InputBase<T, I, O>,
                             O extends OutputBase<T, I, O>>
          extends PayloadResolvableBase<T>
          implements Transaction<T, I, O> {
  private static final long serialVersionUID = 5978505343743647355L;

  private transient long index;
  
  private transient List<I> inputs;

  private transient List<O> outputs;

  private transient long time;

  public TransactionBase() {
  }

  public TransactionBase(long index) {
    this.index = index;
  }

  public TransactionBase(byte[] hash) {
    super(hash);
  }

  public TransactionBase(byte[] hash, byte[] payload) {
    super(hash, payload);
  }

  /**
   * Object oriented constructor.
   *
   * @param inputs Inputs
   * @param outputs Outputs
   */
  public TransactionBase(List<I> inputs, List<O> outputs) {
    this();
    this.inputs = inputs;
    this.outputs = outputs;
  }

  @Override
  public List<I> getInputs() {
    return Collections.unmodifiableList( getInputsInternal());
  }

  protected List<I> getInputsInternal() {
    if (inputs == null) {
      inputs = new ArrayList<>();
    }
    return inputs;
  }

  protected void addInput(I input) {
    int index = getInputsInternal().size();
    getInputsInternal().add(input);
    input.setIndex(index);
  }

  public void setInputs(List<I> inputs) {
    this.inputs = inputs != null ? new ArrayList<>(inputs) : null;
  }

  @Override
  public List<O> getOutputs() {
    return Collections.unmodifiableList(getOutputsInternal());
  }

  protected List<O> getOutputsInternal() {
    if (outputs == null) {
      outputs = new ArrayList<>();
    }
    return outputs;
  }

  protected void addOutput(O output) {
    int index = getOutputsInternal().size();
    getOutputsInternal().add(output);
    output.setIndex(index);
  }

  public void setOutputs(List<O> outputs) {
    this.outputs = outputs != null ? new ArrayList<>(outputs) : null;
  }

  @Override
  public long getIndex() {
    return index;
  }

  public void setIndex(long index) {
    this.index = index;
  }

  @Override
  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder(getClass().getSimpleName());
    builder.append("[hash=");
    builder.append(toHexBigEndian(getHash()));
    builder.append(", index=");
    builder.append(getIndex());
    builder.append(", ");
    builder.append("inputs=");
    builder.append(getInputs());
    builder.append(", ");
    builder.append("outputs=");
    builder.append(getOutputs());
    builder.append(", ");
    builder.append("resolved=");
    builder.append(isResolved());
    builder.append("]");
    return builder.toString();
  }
  
  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  protected Object clone() throws CloneNotSupportedException {
    TransactionBase transactionBase = (TransactionBase) super.clone();
    transactionBase.index = index;
    transactionBase.time = time;
    if (inputs != null) {
      transactionBase.inputs = new ArrayList<>(inputs.size());
      for (I input : inputs) {
        transactionBase.inputs.add(input.clone());
      }
    }
    if (outputs != null) {
      transactionBase.outputs = new ArrayList<>(outputs.size());
      for (O output : outputs) {
        transactionBase.outputs.add(output.clone());
      }
    }
    return transactionBase;
  }
}
