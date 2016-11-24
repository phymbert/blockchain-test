/**
 * Copyright (c) 2016 Chain4j developers
 * Distributed under the MIT software license, see the accompanying
 * file COPYING or http://www.opensource.org/licenses/mit-license.php.
 */

package org.chain4j.bitcoin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.chain4j.Chain4jRuntimeException;
import org.chain4j.ContractException;
import org.chain4j.ResolveException;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;

public class BitcoinContractInput extends BitcoinContract<BitcoinTransactionInput> {

  private static final long serialVersionUID = 6411237762001542655L;
  private static final Logger logger = LogManager.getLogger(BitcoinContractInput.class);
  private static final byte[] TRUE = new byte[]  {1};
  private static final byte[] FALSE = new byte[] {0};

  public BitcoinContractInput() {
  }


  public BitcoinContractInput(byte[] payload) {
    super(null, payload);
  }

  @Override
  public boolean verify(BitcoinTransaction transaction, BitcoinTransactionInput input)
      throws ContractException {
    Deque<byte[]> stack = new ArrayDeque<>();
    final byte[] scriptSig = getPayload();
    processScript(scriptSig, stack, transaction, input.getIndex());
    final byte[] scriptPubKey = getPreviousOutput(transaction, input).getContract().getPayload();
    processScript(scriptPubKey, stack, transaction, input.getIndex());
    opVerify(stack); // Check we have a true result
    return true; // We throw ContractException if fail
  }

  private void processScript(byte[] script, Deque<byte[]> stack,
                             BitcoinTransaction transaction, int inputIndex)
                throws ContractException {
    for (int i = 0; i < script.length; i++) {
      BitcoinScriptWords word = word(script, i);
      i = run(word, i, script, stack, transaction, inputIndex);
    }
  }

  private BitcoinScriptWords word(byte[] script, int index) {
    BitcoinScriptWords word = BitcoinScriptWords.valueOf(script[index]);
    return word;
  }

  private BitcoinTransactionOutput getPreviousOutput(BitcoinTransaction transaction,
                                                     BitcoinTransactionInput input)
                                               throws ContractException {
    try {
      BitcoinTransaction previousTransaction = input.getPreviousTransaction();
      if (!previousTransaction.isResolved()) {
        transaction.getResolver().resolve(previousTransaction);
      }
      if (input.getOutputIndex() < previousTransaction.getOutputs().size()) {
        return previousTransaction.getOutputs().get(input.getOutputIndex());
      } else {
        throw new ContractException(
            "Invalid output index "
                                + input.getOutputIndex()
                                + ":"
                                + previousTransaction.getOutputs().size());
      }
    } catch (ResolveException e) {
      throw new ContractException(e);
    }
  }

  private int run(BitcoinScriptWords word, int index,
                  byte[] script, Deque<byte[]> stack,
                  BitcoinTransaction transaction, int inputIndex)
              throws ContractException {
    switch (word) {
      case OP_0:
        op0(stack);
        break;
      case OP_1:
      case OP_2:
      case OP_3:
      case OP_4:
      case OP_5:
      case OP_6:
      case OP_7:
      case OP_8:
      case OP_9:
      case OP_10:
      case OP_11:
      case OP_12:
      case OP_13:
      case OP_14:
      case OP_15:
      case OP_16:
        opN(word, stack);
        break;
      case CONSTANT:
        return constant(index, script, stack);
      case OP_DUP:
        opDup(stack);
        break;
      case OP_HASH160:
        opHash160(stack);
        break;
      case OP_EQUALVERIFY:
        opEqualVerify(stack);
        break;
      case OP_EQUAL:
        opEqual(stack);
        break;
      case OP_VERIFY:
        opVerify(stack);
        break;
      case OP_CHECKSIG:
        return opCheckSig(index, script, stack, transaction, inputIndex);
      default:
        throw new UnsupportedOperationException(word.name());
    }
    return index;
  }

  private int constant(int index, byte[] script, Deque<byte[]> stack) throws ContractException {
    int size = script[index++] & 0xFF;
    if (size <= 0 || size > script.length - index) {
      throw new ContractException("Invalid constant size " + size + ":" + (script.length - index));
    }
    byte[] constant = new byte[size];
    System.arraycopy(script, index, constant, 0, size); // FIXME Implement with one array
    stack.push(constant);
    return index + size - 1;
  }

  private void op0(Deque<byte[]> stack) {
    stack.push(new byte[0]);
  }

  private void opN(BitcoinScriptWords word, Deque<byte[]> stack) {
    stack.push(new byte[] {(byte) ((word.getCode() - 0x50) & 0xFF)});
  }

  private void opDup(Deque<byte[]> stack) {
    stack.push(stack.peek());
  }

  private void opHash160(Deque<byte[]> stack) throws ContractException {
    checkStackMinSize(stack, 1);
    byte[] data = stack.pop();
    data = hash(data, new SHA256Digest());
    data = hash(data, new RIPEMD160Digest());
    stack.push(data);
  }

  private void opEqualVerify(Deque<byte[]> stack)
            throws ContractException {
    opEqual(stack);
    opVerify(stack);
  }

  private void opVerify(Deque<byte[]> stack) throws ContractException {
    checkStackMinSize(stack, 1);
    byte[] results = stack.peek();
    for (int i = results.length - 1; i >= 0; i--) {
      if ((results[i] & 0xFF) == 0) {
        throw new ContractException("SCRIPT_ERR_VERIFY");
      }
    }
    stack.pop();
  }

  private void opEqual(Deque<byte[]> stack) throws ContractException {
    checkStackMinSize(stack, 2);
    byte[] a1 = stack.pop();
    byte[] a2 = stack.pop();
    stack.push(new byte[] {(byte) (Arrays.equals(a1, a2) ? 1 : 0)});
  }

  private int opCheckSig(int index, byte[] script,
                         Deque<byte[]> stack,
                         BitcoinTransaction txNew,
                         int inputIndex)
            throws ContractException {
    // Step 1 - the public key and the signature are popped from the stack, in that order
    checkStackMinSize(stack, 2);
    final byte[] pub = stack.pop();
    final byte[] signatureAndHashType = stack.pop();

    // Step 2 - A new subscript is created from the instruction
    // from the most recently parsed OP_CODESEPARATOR
    // (last one in script) to the end of the script.
    // If there is no OP_CODESEPARATOR the entire script becomes the subscript
    // (hereby referred to as subScript)
    final byte[] subscript = new byte[index + 1 == script.length
                                          ? script.length : script.length - 1];
    System.arraycopy(script, 0, subscript, 0, index + 1);
    if (index + 1 < subscript.length) {
      System.arraycopy(script, index + 1, subscript, index, subscript.length - index);
    }

    // Step 3 - The sig is deleted from subScript.
    // Not standard to have a sig in the subscript
    // FIXME Step 3 ignored

    // Step 4 - All OP_CODESEPARATORS are removed from subScript
    // FIXME Step 4 ignored

    // Step 5 - Extract hash type from signature
    // Signature format is [<DER signature> <1 byte hash-type>].
    // Hashtype value is last byte of the sig.    
    final SignatureHashType hashType = SignatureHashType
              .from(signatureAndHashType[signatureAndHashType.length - 1]);
    
    
    final byte[] signature = new byte[signatureAndHashType.length - 1];
    System.arraycopy(signatureAndHashType, 0, signature, 0, signature.length);
    
    // Step 6 - Copy TxNew to TxCopy
    // A copy is made of the current transaction (hereby referred to txCopy)
    BitcoinTransaction txCopy = (BitcoinTransaction) txNew.clone();
    
    // SIGHASH_NONE 1 - The output of txCopy is set to a vector of zero size.
    if (hashType == SignatureHashType.SIGHASH_NONE) {
      txCopy.setOutputs(Collections.emptyList());
    }

    // Step 7 - Set all TxIn scripts in TxCopy to empty strings
    // The scripts for all transaction inputs in txCopy are set to empty scripts
    // (exactly 1 byte 0x00)
    final byte[] empty = new byte[] {};
    for (BitcoinTransactionInput input : txCopy.getInputs()) {
      input.getContract().setPayload(empty);

      // SIGHASH_NONE 2 - All other inputs aside from the current input
      // in txCopy have their nSequence index set to zero
      if (hashType == SignatureHashType.SIGHASH_NONE) {
        input.setSequence(0);
      }
    }
    
    

    // Step 8 - Copy Subscript into the TxIn script you are checking
    txCopy.getInputs().get(inputIndex).getContract().setPayload(subscript);

    // Step 9 - Serialize TxCopy and append 4-byte hash type code
    final byte[] payload = txCopy.dump();
    final byte[] payloadAndHashType = new byte[payload.length + 4];
    System.arraycopy(payload, 0, payloadAndHashType, 0, payload.length);
    payloadAndHashType[payloadAndHashType.length - 4] = hashType.code;

    // Step 10 - Verify Signature
    byte[] hash = hash(hash(payloadAndHashType));
    final boolean result = ecdsaCheckSignature(pub, signature, hash);

    // Push to stack verification result
    if (result) {
      stack.push(TRUE);
    } else {
      stack.push(FALSE);
    }
    return index;
  }

  private void checkStackMinSize(Deque<byte[]> stack, int size) throws ContractException {
    if (stack.size() < size) {
      throw new ContractException("SCRIPT_ERR_INVALID_STACK_OPERATION");
    }
  }
  
  /**
   * Display this contract as string.
   *
   * @param contract Script
   * @return Human readable form
   */
  public String toString(byte[] contract) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < contract.length; i++) {
      if (builder.length() > 0) {
        builder.append(" ");
      }
      BitcoinScriptWords word = word(contract, i);
      switch (word) {
        case CONSTANT:
          int size = contract[i++] & 0xFF;
          if (size <= 0 || size > contract.length - i) {
            throw new Chain4jRuntimeException("Invalid constant size");
          }
          byte[] constant = new byte[size];
          System.arraycopy(contract, i, constant, 0, size); 
          builder.append(toHex(constant));
          i += size - 1;
          break;
        default:
          builder.append(word.name());
          break;
      }
    }
    return builder.toString();
  }
  
  @Override
  public String toString() {
    return toString(getPayload());
  }
}
