package org.chain4j.bitcoin;



public enum BitcoinScriptWords {
  OP_0(0x00),
  OP_1(0x51),
  OP_2(0x52),
  OP_3(0x53),
  OP_4(0x54),
  OP_5(0x55),
  OP_6(0x56),
  OP_7(0x57),
  OP_8(0x58),
  OP_9(0x59),
  OP_10(0x5a),
  OP_11(0x5b),
  OP_12(0x5c),
  OP_13(0x5d),
  OP_14(0x5e),
  OP_15(0x5f),
  OP_16(0x60),
  CONSTANT(0xFF),
  OP_DUP(0x76),
  OP_HASH160(0xa9),
  OP_EQUALVERIFY(0x88),
  OP_EQUAL(0x87),
  OP_VERIFY(0x69),
  OP_CHECKSIG(0xac),
  OP_CHECKMULTISIG(0xae);
  private final byte code;

  private BitcoinScriptWords(int code) {
    this.code = (byte) (code & 0xFF);
  }

  /**
   * Find word by operation code.
   *
   * @param code Code
   * @return Word
   */
  public static BitcoinScriptWords valueOf(byte code) {
    for (BitcoinScriptWords word : values()) {
      if (word.code == code) {
        return word;
      }
    }
    return CONSTANT;
  }

  public byte getCode() {
    return code;
  }
}
