package org.chain4j.bitcoin;

import org.chain4j.ContractException;

public enum SignatureHashType {
  SIGHASH_ALL(0x01),
  SIGHASH_NONE(0x02),
  SIGHASH_SINGLE(0x03),
  SIGHASH_ANYONECANPAY(0x80),
  UNSET(0);
  
  public final byte code;

  private SignatureHashType(int code) {
    this.code = (byte) (code & 0xFF);
  }

  /**
   * Provide hash type based on code.
   *
   * @param code Code
   * @return Hashtype
   * @throws ContractException If wrong code
   */
  public static SignatureHashType from(byte code) throws ContractException {
    if (code == SIGHASH_ALL.code) {
      return SIGHASH_ALL;
    }
    if ((code & 0x1F) == SIGHASH_NONE.code) {
      return SIGHASH_NONE;
    }
    if ((code & 0x1F) == SIGHASH_SINGLE.code) {
      return SIGHASH_SINGLE ;
    }
    if (code == SIGHASH_ANYONECANPAY.code) {
      return SIGHASH_SINGLE ;
    }
    if (code == UNSET.code) {
      return SIGHASH_ALL;
    }
    throw new ContractException("Unknown signature hash type " + code);
  }
}
