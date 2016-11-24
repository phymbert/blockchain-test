/**
 * Copyright (c) 2016 Chain4j developers
 * Distributed under the MIT software license, see the accompanying
 * file COPYING or http://www.opensource.org/licenses/mit-license.php.
 */

package org.chain4j.bitcoin;

import org.chain4j.TransactionValidator;
import org.chain4j.ValidationResult;
import org.chain4j.ValidatorBase;

public class BitcoinTransactionValidator
                extends ValidatorBase<BitcoinTransaction>
                implements TransactionValidator<BitcoinTransaction,
                                                BitcoinTransactionInput,
                                                BitcoinTransactionOutput> {

  public BitcoinTransactionValidator() {
    super(BitcoinTransaction.class);
  }

  @Override
  protected ValidationResult<BitcoinTransaction> doValidate(BitcoinTransaction transaction) {
    return null;
  }

}
