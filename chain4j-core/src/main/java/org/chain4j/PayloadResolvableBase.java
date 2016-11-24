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

public class PayloadResolvableBase<P extends PayloadResolvableBase<P>>
          extends PayloadBase implements Resolvable<P> {

  private static final long serialVersionUID = -4652159918499265723L;
  private transient boolean resolved = false;
  private transient PayloadResolver<P> resolver;
  
  public PayloadResolvableBase() {
  }

  public PayloadResolvableBase(byte[] hash) {
    super(hash);
  }

  public PayloadResolvableBase(byte[] hash, byte[] payload) {
    super(hash, payload);
  }

  public PayloadResolvableBase(String hash) throws Chain4jException {
    super(hash);
  }

  public PayloadResolvableBase(String hash, String payload) throws Chain4jException {
    super(hash, payload);
  }

  @Override
  public boolean isResolved() {
    return resolved;
  }

  public void setResolved(boolean resolved) {
    this.resolved = resolved;
  }

  public PayloadResolver<P> getResolver() {
    return resolver;
  }

  public void setResolver(PayloadResolver<P> resolver) {
    this.resolver = resolver;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  protected Object clone() throws CloneNotSupportedException {
    PayloadResolvableBase payloadResolvableBase = (PayloadResolvableBase) super.clone();
    payloadResolvableBase.resolved = resolved;
    payloadResolvableBase.resolver = resolver;
    return payloadResolvableBase;
  }
}
