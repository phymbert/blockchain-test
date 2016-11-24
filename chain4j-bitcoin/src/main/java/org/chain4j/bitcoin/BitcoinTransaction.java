/**
 * Copyright (c) 2016 Chain4j developers
 * Distributed under the MIT software license, see the accompanying
 * file COPYING or http://www.opensource.org/licenses/mit-license.php.
 */

package org.chain4j.bitcoin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.chain4j.Chain4jException;
import org.chain4j.Chain4jRuntimeException;
import org.chain4j.TransactionBase;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public class BitcoinTransaction
    extends TransactionBase<BitcoinTransaction,
                            BitcoinTransactionInput,
                            BitcoinTransactionOutput> {

  /**
   * serial uid.
   */
  private static final long serialVersionUID = 7416858353696947236L;

  private long version;

  public BitcoinTransaction() {
    super();
  }

  public BitcoinTransaction(long index) {
    super(index);
  }

  public BitcoinTransaction(byte[] hash) {
    super(hash);
  }

  public BitcoinTransaction(byte[] hash, byte[] payload) {
    super(hash, payload);
  }

  public BitcoinTransaction(long version,
                            List<BitcoinTransactionInput> inputs,
                            List<BitcoinTransactionOutput> outputs) {
    super(inputs, outputs);
    this.version = version;
  }

  /**
   * JSon Object based constructor, chain.so format.
   *
   * @param jo JSon
   * @throws Chain4jException If any
   */
  public BitcoinTransaction(JsonObject jo) throws Chain4jException {
    setHash(fromHexBigEndian(jo.get("txid").getAsString()));
    if (jo.has("version")) {
      setVersion(jo.get("version").getAsLong());
    }

    for (JsonElement inputElem : jo.get("inputs").getAsJsonArray()) {
      addInput(new BitcoinTransactionInput(inputElem.getAsJsonObject()));
    }

    for (JsonElement outputElem : jo.get("outputs").getAsJsonArray()) {
      addOutput(new BitcoinTransactionOutput(outputElem.getAsJsonObject()));
    }
  }

  public long getVersion() {
    return version;
  }

  public void setVersion(long version) {
    this.version = version;
  }

  @Override
  protected byte[] buildPayload() {
    byte[] wrapped = new byte[size()];
    ByteBuffer buffer = ByteBuffer.wrap(wrapped);
    buffer.order(ByteOrder.LITTLE_ENDIAN);
    buffer.put(longToLe(getVersion()));
    buffer.put(new VarUInt(getInputs().size()).array());
    for (BitcoinTransactionInput input : getInputs()) {
      buffer.put(input.dump());
    }
    buffer.put(new VarUInt(getOutputs().size()).array());
    for (BitcoinTransactionOutput output : getOutputs()) {
      buffer.put(output.dump());
    }
    buffer.put(longToLe(getTime()));
    return wrapped;
  }

  @Override
  public int size() {
    return   INT_BYTES
           + new VarUInt(getInputs().size()).size()
           + getInputsSize()
           + new VarUInt(getOutputs().size()).size()
           + getOutputsSize()
           + INT_BYTES;
  }

  private int getInputsSize() {
    int inputsSize = 0;
    for (BitcoinTransactionInput input : getInputs()) {
      inputsSize += input.size();
    }
    return inputsSize;
  }

  private int getOutputsSize() {
    int outputsSize = 0;
    for (BitcoinTransactionOutput output : getOutputs()) {
      outputsSize += output.size();
    }
    return outputsSize;
  }

  @Override
  protected Object clone() {
    BitcoinTransaction transaction;
    try {
      transaction = (BitcoinTransaction) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new Chain4jRuntimeException(e);
    }
    transaction.version = version;
    return transaction;
  }
}
