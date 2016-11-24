/**
 * Copyright (c) 2016 Chain4j developers
 * Distributed under the MIT software license, see the accompanying
 * file COPYING or http://www.opensource.org/licenses/mit-license.php.
 */

package org.chain4j.bitcoin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.chain4j.Chain4jException;
import org.chain4j.Chain4jUtil;
import org.chain4j.JvmTimeSupplier;
import org.chain4j.TimeSupplier;
import org.chain4j.ValidatorRegistery;
import org.chain4j.ValidatorRegisteryBase;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@RunWith(MockitoJUnitRunner.class)
public class BitcoinMinerTest {

  Chain4jUtil  chain4j = new Chain4jUtil();
  
  @InjectMocks
  BitcoinMiner miner   = new BitcoinMiner();
  
  @Spy
  TimeSupplier supplier = new JvmTimeSupplier();
  
  @Spy
  ValidatorRegistery validatorProvider = new ValidatorRegisteryBase();

  protected BitcoinBlock block40000() throws Chain4jException, ParseException {
    BitcoinBlock block = new BitcoinBlock(
        1,
        block39999(),
        chain4j
            .fromHexBigEndian("5337f888d7ad3760933c5766186188bdcf6c6dec0270a352f23451afda835ecd"),
        miner.getTimeInSeconds(getTime("2010-02-13 12:04:24")),
        486575299L,
        3474096156L);
    block.setHash(chain4j
        .fromHexBigEndian("00000000504d5fa0ad2cb90af16052a4eb2aea70fa1cba653b90a4583c5193e4"));
    return block;
  }

  @Test
  public void testComputeHashBitCoinBlock40000() throws Chain4jException, ParseException {
    BitcoinBlock block = block40000();
    block.getPrevious().setResolved(true);
    block.setHash(null);
    byte[] hash = miner.computeHash(block, block.getProofOfWork(), 0);
    block.setHash(hash);
    assertEquals("e493513c58a4903b65ba1cfa70ea2aeba45260f10ab92cada05f4d5000000000",
        chain4j.toHex(hash).toLowerCase());
    assertEquals("00000000504d5fa0ad2cb90af16052a4eb2aea70fa1cba653b90a4583c5193e4", chain4j
        .toHexBigEndian(hash).toLowerCase());
    assertEquals(1.82, block.difficulty(), 2);
    assertFalse(block.isLowerThanTarget(hash)); // FIXME Check WHY
    System.out.println(block.toStringFull());
  }

  @Ignore// It tooks around 17min on laptop
  @Test
  public void testMineBlock125552() throws Chain4jException, ParseException {
    miner.setVersion(1);
    miner.setTarget(440711666L);
    BitcoinBlock block = miner.mine(block125551(), transactionsBlock125552());
    System.out.println(block);
  }

  @Test
  public void testTime() throws Chain4jException, ParseException {
    long time = getTime("2011-05-21 17:26:31");
    long expected = chain4j.leToLong(chain4j.fromHex("c7f5d74d"));
    long timeInSeconds = miner.getTimeInSeconds(time);
    assertEquals(expected, timeInSeconds);
  }

  protected long getTime(String time) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    Date date = sdf.parse(time);
    return date.getTime();
  }

  @Test
  public void testRootMerkle() throws Chain4jException {
    assertEquals("2b12fcf1b09288fcaff797d71e950e71ae42b91e8bdb2304758dfcffc2b620e3",
        chain4j.toHexBigEndian(
            miner.merkelHash(Arrays.asList(transactionsBlock125552()))
            ).toLowerCase());
  }

  @Test
  public void testComputeHashBitCoinBlock125552() throws Chain4jException, ParseException {
    BitcoinBlock block = block125552();
    block.setHash(null);
    byte[] hash = miner.computeHash(block, block.getProofOfWork(), 0);
    block.setHash(hash);
    assertEquals("1dbd981fe6985776b644b173a4d0385ddc1aa2a829688d1e0000000000000000",
        chain4j.toHex(hash).toLowerCase());
    assertEquals("00000000000000001e8d6829a8a21adc5d38d0a473b144b6765798e61f98bd1d", chain4j
        .toHexBigEndian(hash).toLowerCase());
    assertEquals(244_112.49, block.difficulty(), 2);
    assertTrue(block.isLowerThanTarget(hash));
    System.out.println(block.toStringFull());
  }

  @Test
  public void testIsLowerThanTarget() throws Chain4jException, ParseException {
    BitcoinBlock block = block125552();
    assertTrue(block.isLowerThanTarget(block.getHash()));
    assertFalse(block.isLowerThanTarget(chain4j.fromHexBigEndian(
        "FF0000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f")));
  }

  private BitcoinTransaction[] transactionsBlock40000() throws Chain4jException {
    List<BitcoinTransaction> transactions = new ArrayList<>();
    transactions
        .add(new BitcoinTransaction(chain4j
            .fromHexBigEndian("5337f888d7ad3760933c5766186188bdcf6c6dec0270a352f23451afda835ecd"),
            null));
    return transactions.toArray(new BitcoinTransaction[0]);
  }

  private BitcoinTransaction[] transactionsBlock125552() throws Chain4jException {
    List<BitcoinTransaction> transactions = new ArrayList<>();
    transactions
        .add(new BitcoinTransaction(chain4j
            .fromHexBigEndian("51d37bdd871c9e1f4d5541be67a6ab625e32028744d7d4609d0c37747b40cd2d"),
            null));
    transactions
        .add(new BitcoinTransaction(chain4j
            .fromHexBigEndian("60c25dda8d41f8d3d7d5c6249e2ea1b05a25bf7ae2ad6d904b512b31f997e1a1"),
            null));
    transactions
        .add(new BitcoinTransaction(chain4j
            .fromHexBigEndian("01f314cdd8566d3e5dbdd97de2d9fbfbfd6873e916a00d48758282cbb81a45b9"),
            null));
    transactions
        .add(new BitcoinTransaction(chain4j
            .fromHexBigEndian("b519286a1040da6ad83c783eb2872659eaf57b1bec088e614776ffe7dc8f6d01"),
            null));
    return transactions.toArray(new BitcoinTransaction[0]);
  }

  private BitcoinTransaction[] transactionsBlockGenesis() throws Chain4jException {
    List<BitcoinTransaction> transactions = new ArrayList<>();
    transactions
        .add(new BitcoinTransaction(chain4j
            .fromHexBigEndian("4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b"),
            null));
    return transactions.toArray(new BitcoinTransaction[0]);
  }

  private BitcoinBlock block39999() throws Chain4jException, ParseException {
    BitcoinBlock block = new BitcoinBlock(1,
        new BitcoinBlock(chain4j.fromHexBigEndian(
            "000000000f6eddf413649f8efeecbf82e7037b5e79c33644e79aaba59f66959f")),
        chain4j.fromHexBigEndian(
            "4b479cdb2930d80f3376d322ed32cceead0f34c8845a23a838d039a076e647a7"),
        miner.getTimeInSeconds(getTime("2010-02-13 12:01:25")),
        486575299L,
        18568067L);
    block.setResolved(true);
    block.setTransactions(Arrays.asList(transactionsBlock39999()));
    block.setHash(chain4j.fromHexBigEndian(
        "000000002509f3a013a1d21b89fe33306a45b1fcbb485dd4d310e4f9b2c09945"));
    return block;
  }

  private BitcoinBlock block125551() throws Chain4jException, ParseException {
    BitcoinBlock block = new BitcoinBlock(1,
        new BitcoinBlock(chain4j.fromHexBigEndian(
            "00000000000008a3a41b85b8b29ad444def299fee21793cd8b9e567eab02cd81")),
        chain4j.fromHexBigEndian(
            "92207fb363dfc98b2d7016fb50f6bb43d273087acb3447add320ff0ba86ace40"),
        miner.getTimeInSeconds(getTime("2011-05-21 17:25:03")),
        440711666L,
        18568067L);
    block.setResolved(true);
    block.setHash(chain4j.fromHexBigEndian(
        "00000000000008a3a41b85b8b29ad444def299fee21793cd8b9e567eab02cd81"));
    return block;
  }

  private BitcoinTransaction[] transactionsBlock39999() throws Chain4jException {
    List<BitcoinTransaction> transactions = new ArrayList<>();
    transactions
        .add(new BitcoinTransaction(chain4j
            .fromHexBigEndian("4b479cdb2930d80f3376d322ed32cceead0f34c8845a23a838d039a076e647a7"),
            null));
    return transactions.toArray(new BitcoinTransaction[0]);
  }

  protected BitcoinBlock block125552() throws Chain4jException, ParseException {
    BitcoinBlock block = new BitcoinBlock(
        1,
        block125551(),
        chain4j.fromHex("e320b6c2fffc8d750423db8b1eb942ae710e951ed797f7affc8892b0f1fc122b"),
        chain4j.leToLong(chain4j.fromHex("c7f5d74d")),
        chain4j.leToLong(chain4j.fromHex("f2b9441a")),
        chain4j.leToLong(chain4j.fromHex("42a14695")));
    block.setHash(chain4j
        .fromHex("1dbd981fe6985776b644b173a4d0385ddc1aa2a829688d1e0000000000000000"));
    return block;
  }

}
