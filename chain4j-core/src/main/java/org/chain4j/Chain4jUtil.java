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

import org.bouncycastle.crypto.Digest;

public class Chain4jUtil extends Chain4jBase {

  @Override
  public String toHex256(byte[] hash) {
    return super.toHex256(hash);
  }

  @Override
  public String toHex(byte[] hash) {
    return super.toHex(hash);
  }
  
  @Override
  public byte[] fromHex(String hex) throws Chain4jException {
    return super.fromHex(hex);
  }
  
  @Override
  public byte[] fromHexBigEndian(String hex) throws Chain4jException {
    return super.fromHexBigEndian(hex);
  }
  
  @Override
  public long leToLong(byte[] bytes) {
    return super.leToLong(bytes);
  }
  
  @Override
  public String toHexBigEndian(byte[] hash) {
    return super.toHexBigEndian(hash);
  }
  
  @Override
  public byte[] hash(byte[] input) {
    return super.hash(input);
  }

  @Override
  public byte[] hash(byte[] input, Digest digest) {
    return super.hash(input, digest);
  }
}
