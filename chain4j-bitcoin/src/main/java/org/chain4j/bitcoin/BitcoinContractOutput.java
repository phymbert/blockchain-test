/**
 * Copyright (c) 2016 Chain4j developers
 * Distributed under the MIT software license, see the accompanying
 * file COPYING or http://www.opensource.org/licenses/mit-license.php.
 */

package org.chain4j.bitcoin;


public class BitcoinContractOutput extends BitcoinContract<BitcoinTransactionOutput> {

  private static final long serialVersionUID = 6411237762001542655L;

  public BitcoinContractOutput() {
  }

  public BitcoinContractOutput(byte[] payload) {
    super(null, payload);
  }
}
