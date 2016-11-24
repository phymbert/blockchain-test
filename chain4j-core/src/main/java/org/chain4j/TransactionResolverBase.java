/**
 * Copyright (c) 2016 Chain4j developers
 * Distributed under the MIT software license, see the accompanying
 * file COPYING or http://www.opensource.org/licenses/mit-license.php.
 */

package org.chain4j;


public abstract class TransactionResolverBase<T extends TransactionBase<T, I, O>,
                                              I extends InputBase<T, I, O>,
                                              O extends OutputBase<T, I, O>>
                    extends PayloadResolverBase<T>
                    implements TransactionResolver<T, I, O> {

  public TransactionResolverBase(Class<T> clazz) {
    super(clazz);
  }

  @Override
  protected void map(T resolved, T toResolve) {
    super.map(resolved, toResolve);
    toResolve.setIndex(resolved.getIndex());
    toResolve.setHash(resolved.getHash());
    toResolve.setInputs(resolved.getInputs());
    toResolve.setOutputs(resolved.getOutputs());
  }
}
