package org.chain4j.bitcoin;

import org.chain4j.BlockValidator;
import org.chain4j.ValidationResult;
import org.chain4j.ValidatorBase;

public class BitcoinBlockValidator
              extends ValidatorBase<BitcoinBlock>
              implements BlockValidator<BitcoinBlock,
                                        BitcoinTransaction,
                                        BitcoinTransactionInput,
                                        BitcoinTransactionOutput> {

  public BitcoinBlockValidator() {
    super(BitcoinBlock.class);
  }

  @Override
  protected ValidationResult<BitcoinBlock> doValidate(BitcoinBlock block) {
    return null;
  }

}
