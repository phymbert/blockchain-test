/**
 * Copyright (c) 2016 Chain4j developers
 * Distributed under the MIT software license, see the accompanying
 * file COPYING or http://www.opensource.org/licenses/mit-license.php.
 */

package org.chain4j.bitcoin;

import com.google.gson.JsonObject;

import org.chain4j.Chain4jException;
import org.chain4j.OutputBase;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;


public class BitcoinTransactionOutput extends OutputBase<BitcoinTransaction,
                                              BitcoinTransactionInput,
                                              BitcoinTransactionOutput> {

  private static final long serialVersionUID = 8416858353696947236L;

  private transient BigInteger amount;

  public BitcoinTransactionOutput() {
    super();
  }

  public BitcoinTransactionOutput(byte[] payload) {
    super(payload);
  }

  public BitcoinTransactionOutput(byte[] hash, byte[] payload) {
    super(hash, payload);
  }

  public BitcoinTransactionOutput(BigInteger amount, List<BitcoinContractOutput> contracts) {
    super(contracts);
    this.amount = amount;
  }

  /**
   * JSon Object based constructor, chain.so format.
   *
   * @param jo JSon
   * @throws Chain4jException If error
   */
  public BitcoinTransactionOutput(JsonObject jo) throws Chain4jException {
    this.amount = jo.get("value").getAsBigDecimal()
                    .multiply(BigDecimal.valueOf(Bitcoin.SATOSHI_IN_BTC)).toBigInteger();
    if (jo.has("script_hex")) {
      addContract(new BitcoinContractOutput(fromHex(jo.get("script_hex").getAsString())));
    }
  }

  public BigInteger getAmount() {
    return amount;
  }

  public void setAmount(BigInteger amount) {
    this.amount = amount;
  }

  public BitcoinContractOutput getContract() {
    return (BitcoinContractOutput) getContracts().get(0);
  }

  @Override
  protected byte[] buildPayload() {
    byte[] wrapped = new byte[size()];
    ByteBuffer buffer = ByteBuffer.wrap(wrapped);
    buffer.put(getAmountAsBytes()); 
    buffer.put(new VarUInt(getContract().size()).array());
    buffer.put(getContract().dump());
    return wrapped;
  }

  private byte[] getAmountAsBytes() {
    byte[] bytes = amount.toByteArray();
    byte[] amountBytes = new byte[LONG_BYTES];
    for (int i = 0; i < bytes.length; i++) {
      amountBytes[bytes.length - i - 1] = bytes[i];
    }
    return amountBytes;
  }

  @Override
  public int size() {
    return   LONG_BYTES
           + new VarUInt(getContract().size()).size()
           + getContract().size();
  }
}
