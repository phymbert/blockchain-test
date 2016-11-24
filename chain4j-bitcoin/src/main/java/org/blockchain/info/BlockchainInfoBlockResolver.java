/**
 * Copyright (c) 2016 Chain4j developers
 * Distributed under the MIT software license, see the accompanying
 * file COPYING or http://www.opensource.org/licenses/mit-license.php.
 */

package org.blockchain.info;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.chain4j.BlockResolverBase;
import org.chain4j.Hash;
import org.chain4j.ResolveException;
import org.chain4j.bitcoin.BitcoinBlock;
import org.chain4j.bitcoin.BitcoinTransaction;
import org.chain4j.bitcoin.BitcoinTransactionInput;
import org.chain4j.bitcoin.BitcoinTransactionOutput;

import java.util.Collections;

import javax.inject.Inject;

public class BlockchainInfoBlockResolver extends BlockResolverBase<BitcoinBlock,
                                                                  BitcoinTransaction,
                                                                  BitcoinTransactionInput,
                                                                  BitcoinTransactionOutput> {

  public BlockchainInfoBlockResolver() {
    super(BitcoinBlock.class);
  }

  @Inject
  private HttpClient httpClient;

  @Override
  protected BitcoinBlock doResolve(Hash hash) throws ResolveException {
    try {
      String hashString = hash.toHex();
      String blockJsonString = httpClient.get("block/BTC/" + hashString, Collections.emptyMap());
      if (blockJsonString == null) {
        throw new ResolveException();
      }
      JsonObject blockJson = new JsonParser().parse(blockJsonString).getAsJsonObject();
      if (blockJson.has("data")) {
        blockJson = blockJson.getAsJsonObject();
      }
      return new BitcoinBlock(blockJson);
    } catch (ResolveException e) {
      throw e;
    } catch (Exception e) {
      throw new ResolveException(e);
    }
  }

  @Override
  protected BitcoinBlock doResolve(long index) throws ResolveException {
    try {
      String blockJsonString = httpClient.get("block/BTC/" + index, Collections.emptyMap());
      if (blockJsonString == null) {
        throw new ResolveException();
      }
      JsonObject blockJson = new JsonParser().parse(blockJsonString).getAsJsonObject();
      return new BitcoinBlock(blockJson);
    } catch (ResolveException e) {
      throw e;
    } catch (Exception e) {
      throw new ResolveException(e);
    }
  }
}
