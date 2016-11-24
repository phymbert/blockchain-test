package org.chain4j.bitcoin;

import org.chain4j.Chain4jException;
import org.chain4j.Chain4jUtil;
import org.chain4j.ChainBase;

public class Bitcoin extends ChainBase<BitcoinBlock,
                                       BitcoinTransaction,
                                       BitcoinTransactionInput,
                                       BitcoinTransactionOutput> {

  private static final long serialVersionUID = 1250675262372159466L;
  public static final int SATOSHI_IN_BTC = 100_000_000;
  private static final BitcoinBlock GENESIS;

  static {
    try {
      Chain4jUtil util = new Chain4jUtil();
      GENESIS  = new BitcoinBlock(
            1, new BitcoinBlock(
                  "0000000000000000000000000000000000000000000000000000000000000000"),
              util.fromHexBigEndian(
                  "4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b"),
                  0, 486604799,
                  2083236893); // FIXME
    } catch (Chain4jException e) {
      throw new IllegalStateException(e);
    }
  }

  public Bitcoin() {
    super(BitcoinBlock.class, BitcoinTransaction.class, GENESIS);
  }
}
