package org.chain4j.bitcoin;

import static org.junit.Assert.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.apache.commons.io.IOUtils;
import org.chain4j.Chain4jException;
import org.chain4j.Chain4jUtil;
import org.junit.Test;

import java.io.IOException;

public class BitcoinContractInputTest {
  Chain4jUtil util = new Chain4jUtil();
  JsonParser parser = new JsonParser();

  @Test
  public void testVerify2846513() throws Chain4jException, JsonSyntaxException, IOException {
    JsonObject trJson = parser.parse(resource("/transaction2846513.json"))
                                  .getAsJsonObject();
    BitcoinTransaction transaction = new BitcoinTransaction(trJson);
    trJson = parser.parse(resource("/transaction2820700.json"))
        .getAsJsonObject();
    BitcoinTransactionInput input = transaction.getInputs().get(0);
    BitcoinTransaction previousTransaction = new BitcoinTransaction(trJson);
    previousTransaction.setResolved(true);
    input.setPreviousTransaction(previousTransaction);
    assertTrue(input.getContract().verify(transaction, transaction.getInputs().get(0)));
  }

  @Test
  public void testVerify316036() throws Chain4jException, JsonSyntaxException, IOException {
    JsonObject trJson = parser.parse(resource("/transaction316036.json"))
                                  .getAsJsonObject();
    BitcoinTransaction transaction = new BitcoinTransaction(trJson);
    trJson = parser.parse(resource("/transaction315943.json"))
        .getAsJsonObject();
    BitcoinTransactionInput input = transaction.getInputs().get(0);
    BitcoinTransaction previousTransaction = new BitcoinTransaction(trJson);
    previousTransaction.setResolved(true);
    input.setPreviousTransaction(previousTransaction);
    assertTrue(input.getContract().verify(transaction, transaction.getInputs().get(0)));
  }

  @Test
  public void testVerify2006013() throws Chain4jException, JsonSyntaxException, IOException {
    JsonObject trJson = parser.parse(resource("/transaction2006013.json"))
                                  .getAsJsonObject();
    BitcoinTransaction transaction = new BitcoinTransaction(trJson);
    trJson = parser.parse(resource("/transaction1390408.json"))
        .getAsJsonObject();
    BitcoinTransactionInput input = transaction.getInputs().get(0);
    BitcoinTransaction previousTransaction = new BitcoinTransaction(trJson);
    previousTransaction.setResolved(true);
    input.setPreviousTransaction(previousTransaction);
    assertTrue(input.getContract().verify(transaction, transaction.getInputs().get(0)));
  }

  private String resource(String resource) throws IOException {
    return IOUtils.toString(getClass().getResource(resource));
  }
}
