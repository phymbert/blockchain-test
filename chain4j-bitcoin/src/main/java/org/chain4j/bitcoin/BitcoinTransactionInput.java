/**
 * Copyright (c) 2016 Chain4j developers
 * Distributed under the MIT software license, see the accompanying
 * file COPYING or http://www.opensource.org/licenses/mit-license.php.
 */

package org.chain4j.bitcoin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.chain4j.Chain4jException;
import org.chain4j.InputBase;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class BitcoinTransactionInput extends InputBase<BitcoinTransaction,
                                             BitcoinTransactionInput,
                                             BitcoinTransactionOutput> {

  /**
   * serial uid.
   */
  private static final long serialVersionUID = 7416858353696947236L;
  private static final int NO_SEQUENCE = 0xFFFFFFFF;

  private int sequence = NO_SEQUENCE;

  public BitcoinTransactionInput() {
    super();
  }

  /**
   * JSon Object based constructor, chain.so format.
   *
   * @param jo JSon
   * @throws Chain4jException If error
   */
  public BitcoinTransactionInput(JsonObject jo) throws Chain4jException {
    JsonElement element = jo.get("received_from");
    if (element != null && !element.isJsonNull()) {
      JsonObject previous = (JsonObject) element;
      setPreviousTransaction(
          new BitcoinTransaction(fromHexBigEndian(previous.get("txid").getAsString())));
      setOutputIndex(previous.get("output_no").getAsInt());
    }
    JsonElement script = jo.get("script_hex");
    if (script != null && !script.isJsonNull()) {
      addContract(
          new BitcoinContractInput(fromHex(script.getAsString())));
    }
  }

  public BitcoinContractInput getContract() {
    return (BitcoinContractInput) getContracts().get(0);
  }

  @Override
  protected byte[] buildPayload() {
    byte[] wrapped = new byte[size()];
    ByteBuffer buffer = ByteBuffer.wrap(wrapped);
    buffer.order(ByteOrder.LITTLE_ENDIAN);
    buffer.put(getPreviousTransaction().getHash());
    buffer.put(longToLe(getOutputIndex()));
    buffer.put(new VarUInt(getContract().size()).array());
    buffer.put(getContract().dump());
    buffer.putInt(sequence );
    return wrapped;
  }

  @Override
  public int size() {
    return getHashBytesSize()
           + INT_BYTES
           + new VarUInt(getContract().size()).size()
           + getContract().size()
           + INT_BYTES;
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    // TODO Auto-generated method stub
    return super.clone();
  }

  public int getSequence() {
    return sequence;
  }

  public void setSequence(int sequence) {
    this.sequence = sequence;
  }
}
