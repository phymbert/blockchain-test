/**
 * Copyright (c) 2016 Chain4j developers
 * Distributed under the MIT software license, see the accompanying
 * file COPYING or http://www.opensource.org/licenses/mit-license.php.
 */

package org.chain4j.bitcoin;

import static org.junit.Assert.assertEquals;

import org.chain4j.Chain4jException;
import org.chain4j.Chain4jUtil;
import org.junit.Test;

public class BitcoinBlockTest {
  
  BitcoinBlock block = new BitcoinBlock();
  
  Chain4jUtil chain4j = new Chain4jUtil();

  @Test
  public void testTargetToBytesBigEndian() throws Chain4jException {
    assertEquals("00000000000404CB00000000000000000000000000000000000000",
        chain4j.toHex(block.targetToBytesBigEndian(453248203)));
    assertEquals("000000000044B9F2000000000000000000000000000000000000",
        chain4j.toHex(block.targetToBytesBigEndian(chain4j.leToLong(chain4j.fromHex("f2b9441a")))));
    assertEquals("000000000000FFFF000000000000000000000000000000000000000000",
        chain4j.toHex(block.targetToBytesBigEndian(0x1d00ffff)));
  }

  @Test
  public void testDifficulty() throws Chain4jException {
    assertEquals(244_112.49,
          block.difficulty(440711666), 2);
    assertEquals(1,
        block.difficulty(BitcoinBlock.GENESIS_TARGET), 2);
    assertEquals(281_800_917_193.2,
        block.difficulty(402908884), 2);
  }
}
