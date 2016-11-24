/**
 * Copyright (c) 2016 Chain4j developers
 * Distributed under the MIT software license, see the accompanying
 * file COPYING or http://www.opensource.org/licenses/mit-license.php.
 */

package org.chain4j;


public abstract class BlockResolverBase<B extends BlockBase<B, T, I, O>,
                                        T extends TransactionBase<T, I, O>,
                                        I extends InputBase<T, I, O>,
                                        O extends OutputBase<T, I, O>>
                    extends PayloadResolverBase<B>
                    implements BlockResolver<B, T, I, O> {

  public BlockResolverBase(Class<B> clazz) {
    super(clazz);
  }

  @Override
  protected void map(B resolved, B blockToResolve) {
    super.map(resolved, blockToResolve);
    blockToResolve.setIndex(resolved.getIndex());
    blockToResolve.setHash(resolved.getHash());
    blockToResolve.setVersion(resolved.getVersion());
    blockToResolve.setTime(resolved.getTime());
    blockToResolve.setTarget(resolved.getTarget());
    blockToResolve.setPrevious(resolved.getPrevious());
    blockToResolve.setTransactions(resolved.getTransactions());
  }
}
