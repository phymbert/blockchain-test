package org.chain4j.bitcoin;

import static org.junit.Assert.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.chain4j.Chain4jException;
import org.chain4j.Chain4jUtil;
import org.junit.Test;

import java.io.IOException;

public class BitcoinTransactionTest {
  Chain4jUtil          util     = new Chain4jUtil();
  BitcoinContractInput contract = new BitcoinContractInput();
  JsonParser           parser   = new JsonParser();

  @Test
  public void testPayload() throws Chain4jException, JsonSyntaxException, IOException {
    JsonObject trJson = parser.parse(resource("/transaction558438.json"))
        .getAsJsonObject();
    BitcoinTransaction transaction = new BitcoinTransaction(trJson);
    assertEquals(resource("/transaction558438.raw").toUpperCase(),
                 util.toHex(transaction.getPayload()));
  }

  @Test
  public void testHash() throws Chain4jException, JsonSyntaxException, IOException {
    JsonObject trJson = parser.parse(resource("/transaction558438.json"))
        .getAsJsonObject();
    BitcoinTransaction transaction = new BitcoinTransaction(trJson);
    String expectedHash = util.toHex(transaction.getHash());
    SHA256Digest digest = new SHA256Digest();
    byte[] hash = util.hash(util.hash(transaction.getPayload(), digest), digest);
    assertEquals(expectedHash, util.toHex(hash));
  }

  private String resource(String resource) throws IOException {
    return IOUtils.toString(getClass().getResource(resource));
  }
}
