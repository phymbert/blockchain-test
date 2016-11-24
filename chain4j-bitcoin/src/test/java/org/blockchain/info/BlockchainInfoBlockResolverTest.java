/**
 * Copyright (c) 2016 Chain4j developers
 * Distributed under the MIT software license, see the accompanying
 * file COPYING or http://www.opensource.org/licenses/mit-license.php.
 */

package org.blockchain.info;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import org.apache.commons.io.IOUtils;
import org.chain4j.Chain4jException;
import org.chain4j.Chain4jUtil;
import org.chain4j.HashBase;
import org.chain4j.bitcoin.BitcoinBlock;
import org.chain4j.bitcoin.BitcoinTransaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.text.ParseException;

@RunWith(MockitoJUnitRunner.class)
public class BlockchainInfoBlockResolverTest {

  Chain4jUtil  chain4j = new Chain4jUtil();

  @Mock
  HttpClient httpClient;

  @InjectMocks
  BlockchainInfoBlockResolver resolver = new BlockchainInfoBlockResolver();

  @Before
  public void before() throws BlochainInfoException, IOException {
    when(httpClient.get(
        eq("block/BTC/00000000000000001E8D6829A8A21ADC5D38D0A473B144B6765798E61F98BD1D"),
        any()))
        .thenReturn(resource("/block125552.json"));
  }

  private String resource(String resource) throws IOException {
    return IOUtils.toString(getClass().getResource(resource));
  }

  @Test
  public void testResolveBlock125552() throws Chain4jException, ParseException {
    BitcoinBlock block = resolver.resolve(
        new HashBase("00000000000000001E8D6829A8A21ADC5D38D0A473B144B6765798E61F98BD1D"));
    System.out.println(block.toString());
    assertEquals("1dbd981fe6985776b644b173a4d0385ddc1aa2a829688d1e0000000000000000",
        chain4j.toHex(block.getHash()).toLowerCase());
    assertEquals("2b12fcf1b09288fcaff797d71e950e71ae42b91e8bdb2304758dfcffc2b620e3",
        chain4j.toHexBigEndian(block.getTransactionsHash()).toLowerCase());
    assertEquals(440711666L, block.getTarget());
    assertEquals(2504433986L, block.getProofOfWork());
    assertEquals(244_112.49, block.difficulty(), 2);
    assertEquals(4, block.getTransactions().size());
    assertTrue(block.isResolved());
    for (BitcoinTransaction transaction : block.getTransactions()) {
      assertTrue(transaction.isResolved());
    }
  }
}
