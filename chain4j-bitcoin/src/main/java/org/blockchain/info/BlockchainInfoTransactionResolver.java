/**
 * Copyright (c) 2016 Chain4j developers
 * Distributed under the MIT software license, see the accompanying
 * file COPYING or http://www.opensource.org/licenses/mit-license.php.
 */

package org.blockchain.info;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.chain4j.Hash;
import org.chain4j.ResolveException;
import org.chain4j.TransactionResolverBase;
import org.chain4j.bitcoin.BitcoinTransaction;
import org.chain4j.bitcoin.BitcoinTransactionInput;
import org.chain4j.bitcoin.BitcoinTransactionOutput;

import java.util.Collections;

import javax.inject.Inject;

public class BlockchainInfoTransactionResolver
                                  extends TransactionResolverBase<BitcoinTransaction,
                                                                  BitcoinTransactionInput,
                                                                  BitcoinTransactionOutput> {

  public BlockchainInfoTransactionResolver() {
    super(BitcoinTransaction.class);
  }

  @Inject
  private HttpClient httpClient;
  
  @Override
  protected BitcoinTransaction doResolve(Hash hash) throws ResolveException {
    try {
      String hashString = hash.toHex();
      String blockJsonString = httpClient.get("tx/BTC/" + hashString, Collections.emptyMap());
      JsonObject blockJson = new JsonParser().parse(blockJsonString).getAsJsonObject();
      BitcoinTransaction transaction = new BitcoinTransaction(blockJson);
      markAsResolved(transaction);
      return transaction;
    } catch (Exception e) {
      throw new ResolveException(e);
    }
  }
  
  @Override
  protected BitcoinTransaction doResolve(long index) throws ResolveException {
    try {
      String blockJsonString = httpClient.get("tx/BTC/" + index, Collections.emptyMap());
      JsonObject blockJson = new JsonParser().parse(blockJsonString).getAsJsonObject();
      BitcoinTransaction transaction = new BitcoinTransaction(blockJson);
      markAsResolved(transaction);
      return transaction;
    } catch (Exception e) {
      throw new ResolveException(e);
    }
  }
}
