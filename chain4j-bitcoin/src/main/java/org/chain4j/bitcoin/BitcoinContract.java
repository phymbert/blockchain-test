/**
 * Copyright (c) 2016 Chain4j developers
 * Distributed under the MIT software license, see the accompanying
 * file COPYING or http://www.opensource.org/licenses/mit-license.php.
 */

package org.chain4j.bitcoin;

import org.chain4j.Chain4jRuntimeException;
import org.chain4j.ContractBase;
import org.chain4j.ContractDefinition;
import org.chain4j.ContractException;

public class BitcoinContract<D extends ContractDefinition<BitcoinTransaction>>
                             extends ContractBase<BitcoinTransaction, D> {

  private static final long serialVersionUID = -6411237762001542655L;

  public BitcoinContract() {
  }
  
  public BitcoinContract(byte[] hash) {
    super(hash);
  }

  public BitcoinContract(byte[] hash, byte[] payload) {
    super(hash, payload);
  }
  
  @Override
  public String getName() {
    return "BitcoinScript";
  }

  @Override
  public boolean verify(BitcoinTransaction context, D contractable)
      throws ContractException {
    return true;
  }
  
  @Override
  protected byte[] buildPayload() {
    byte[] payload = super.getPayload();
    if (payload == null) {
      throw new Chain4jRuntimeException();
    }
    return payload;
  }
}
