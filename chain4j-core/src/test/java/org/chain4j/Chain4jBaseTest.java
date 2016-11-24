package org.chain4j;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class Chain4jBaseTest {
  
  Chain4jBase chain4j = new Chain4jBase();

  @Test
  public void testToHex256() {
    assertEquals("0000000000000000000000000000000000000000000000000000000000000001",
          chain4j.toHex256(new BigInteger("1").toByteArray()));
    assertEquals("0000000000000000000000000000000000000000000000000000000000007F7F",
        chain4j.toHex256(new byte[] {Byte.MAX_VALUE, Byte.MAX_VALUE}));
    assertEquals("000000000000000000000000000000000000000000000000000000000000FFFF",
        chain4j.toHex256(new byte[] {-1, -1}));
    assertEquals("0000000000000000000000000000000000000000000000000000000000000000",
        chain4j.toHex256(new byte[] {0, 0}));
  }

  @Test
  public void testFromHex() throws Chain4jException {
    assertEquals("000000000000000000000000000000000000000000000000000000000000FFFF",
        chain4j.toHex256(chain4j.fromHex("ffff")));
  }

  @Test
  public void testFromHexBigEndian() throws Chain4jException {
    assertEquals("000000000000000000000000000000000000000000000000000000000000FFFF",
        chain4j.toHex(
        chain4j.fromHexBigEndian(
           "FFFF000000000000000000000000000000000000000000000000000000000000")));
  }

  @Test
  public void testHash() throws Chain4jException {
    assertEquals("CA2FD00FA001190744C15C317643AB092E7048CE086A243E2BE9437C898DE1BB",
          chain4j.toHex256(chain4j.hash(chain4j.fromHex("ffff"))));
  }

  @Test
  public void testHashTransactions() throws Chain4jException {
    assertEquals("B8A71F52DDC5AC5FD6DA30E2F9E8583F279830E6BF27CD6CEB4A948B5766E376",
          chain4j.toHex256(chain4j.merkelHash(transactions2())));
  }

  private List<Transaction> transactions2() throws Chain4jException {
    List<Transaction> transactions = new ArrayList<>();
    transactions.add(new TransactionBase(chain4j
          .fromHex("8a0ca611757f40c2d6f5e4aa6c428e64e91c82e5a2b6d811b61a17666b73de68"), null));
    transactions.add(new TransactionBase(chain4j
        .fromHex("203e06172d13f1c57f7dee696005673b6fc09fb99e79564d6346a42f2bc6082d"), null));
    return transactions;
  }
  
  @Test
  public void testLeToLong() throws Chain4jException {
    assertEquals(1L,
        chain4j.leToLong(chain4j.fromHex("01000000")));
    assertEquals(2_504_433_986L,
        chain4j.leToLong(chain4j.fromHex("42a14695")));
    assertEquals(440_711_666L,
        chain4j.leToLong(chain4j.fromHex("f2b9441a")));
    assertEquals(1_305_998_791L,
        chain4j.leToLong(chain4j.fromHex("c7f5d74d")));
  }

  @Test
  public void testLongToLe() throws Chain4jException {
    assertEquals("01000000",
        chain4j.toHex(chain4j.longToLe(1L))); // Version of Block 125552
    assertEquals("42A14695",
        chain4j.toHex(chain4j.longToLe(2_504_433_986L))); // nonce Block 125552
    assertEquals("F2B9441A",
        chain4j.toHex(chain4j.longToLe(440_711_666L))); // Bits Block 125552
    assertEquals("C7F5D74D",
        chain4j.toHex(chain4j.longToLe(1_305_998_791L))); // Time of Block 125552
    assertEquals(1_305_998_791L,
        chain4j.leToLong(chain4j.longToLe(1_305_998_791L)));
  }
}
