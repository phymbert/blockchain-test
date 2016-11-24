package org.chain4j.bitcoin;

import static org.junit.Assert.*;

import org.chain4j.Chain4jException;
import org.chain4j.Chain4jUtil;
import org.junit.Test;

import java.nio.BufferOverflowException;

public class VarUIntTest {
  
  Chain4jUtil chain4j = new Chain4jUtil();

  @Test
  public void testVarUIntByteArray() throws Chain4jException {
    assertEquals("FC", new VarUInt(chain4j.fromHex("FC")).toString());
    assertEquals("FD-FFFF", new VarUInt(chain4j.fromHex("FDFFFF")).toString());
    assertEquals("FE-FFFFFFFF", new VarUInt(chain4j.fromHex("FEFFFFFFFF")).toString());
    assertEquals("FF-FFFFFFFFFF", new VarUInt(chain4j.fromHex("FFFFFFFFFFFF")).toString());
  }

  @Test
  public void testVarUIntLong() {
    assertEquals("FC", new VarUInt(252).toString());
    assertEquals("FD-FFFF", new VarUInt(65535).toString());
    assertEquals("FE-FFFFFFFF", new VarUInt(4294967295L).toString());
    assertEquals("FF-000000FFFFFFFFFF", new VarUInt(1099511627775L).toString());
  }

  @Test
  public void testGet() {
    assertEquals("FF-7FFFFFFFFFFFFFFF", new VarUInt(9223372036854775807L).toString());
    assertEquals(9223372036854775807L, new VarUInt(9223372036854775807L).get());
    long interval = Chain4jUtil.MAX_UINT / 1_000_000;
    for (long i = 0; i <= Chain4jUtil.MAX_UINT; i += interval) {
      assertEquals(i, new VarUInt(i).get());
    }
  }

  @Test(expected = BufferOverflowException.class)
  public void testGetOverflow() throws Chain4jException {
    VarUInt varUInt = new VarUInt(chain4j.fromHex("FFFFFFFFFFFFFFFFFF"));
    assertEquals(9, varUInt.size());
    long longValue = varUInt.get();
    fail("Overflow not raised " + varUInt + ": " + longValue);
  }
}
