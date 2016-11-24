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

import java.util.Arrays;

public class HashBase extends Chain4jBase implements Hash {
  private static final long serialVersionUID = 2154674847843758745L;

  private byte[] hash;

  public HashBase() {
    super();
  }

  public HashBase(byte[] hash) {
    super();
    this.hash = hash;
  }

  public HashBase(String hex) throws Chain4jException {
    super();
    this.hash = fromHex(hex);
  }

  @Override
  public byte[] getHash() {
    return hash;
  }

  public void setHash(byte[] hash) {
    this.hash = hash;
  }

  @Override
  public String toHex() {
    return toHex(getHash());
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(getHash());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    HashBase other = (HashBase) obj;
    if (!Arrays.equals(hash, other.hash)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " [" + toHex();
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    HashBase hashBase = (HashBase) super.clone();
    if (hash != null) {
      hashBase.hash = Arrays.copyOf(hash, hash.length);
    }
    return hashBase;
  }
}
