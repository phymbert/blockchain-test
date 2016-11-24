/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the LICENSE.txt file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.chain4j;

import org.bouncycastle.util.Arrays;

public class PayloadBase extends HashBase implements Payload {

  private static final long serialVersionUID = 3952890350622386852L;

  private byte[] payload;

  public PayloadBase() {
  }

  public PayloadBase(byte[] hash) {
    super(hash);
  }

  public PayloadBase(byte[] hash, byte[] payload) {
    super(hash);
    this.payload = readPayload(payload);
  }

  public PayloadBase(String hash) throws Chain4jException {
    super(hash);
  }

  public PayloadBase(String hash, String payload) throws Chain4jException {
    super(hash);
    this.payload = readPayload(fromHexBigEndian(payload));
  }

  protected byte[] readPayload(byte[] payload) {
    return payload;
  }

  @Override
  public final byte[] getPayload() {
    checkPayload();
    return payload;
  }
  
  public void setPayload(byte[] payload) {
    setHash(null);
    this.payload = payload;
  }

  @Override
  public int size() {
    return getPayload().length;
  }

  @Override
  public byte[] getHash() {
    checkHash();
    return super.getHash();
  }

  protected void checkHash() {
    if (super.getHash() == null
            && (payload != null || canBuildPayload())) {
      setHash(customHash(getPayload()));
    }
  }

  @Override
  public byte[] dump() {
    return buildPayload();
  }

  protected void checkPayload() {
    if (payload == null && canBuildPayload()) {
      payload = buildPayload();
    }
  }
  
  protected boolean canBuildPayload() {
    return true;
  }

  protected byte[] buildPayload() {
    throw new UnsupportedOperationException();
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    PayloadBase payloadBase = (PayloadBase) super.clone();
    if (payload != null) {
      payloadBase.payload = Arrays.copyOf(payload, payload.length);
    }
    return payloadBase;
  }
}
