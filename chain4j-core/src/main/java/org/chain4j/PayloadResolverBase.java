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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

public class PayloadResolverBase<P extends PayloadResolvableBase<P>>
              extends Chain4jBase
              implements PayloadResolver<P> {
  
  private final Class<P> clazz;
  
  @Inject
  private PayloadResolverRegistery provider;

  public PayloadResolverBase(Class<P> clazz) {
    super();
    this.clazz = clazz;
  }

  @PostConstruct
  public void construct() {
    provider.addResolver(this);
  }

  @PreDestroy
  public void destroy() {
    provider.removeResolver(this);
  }
  
  @Override
  public P resolve(Hash hash) throws ResolveException {
    P resolved = doResolve(hash);
    markAsResolved(resolved);
    return resolved;
  }

  @Override
  public P resolve(long index) throws ResolveException {
    P resolved = doResolve(index);
    markAsResolved(resolved);
    return resolved;
  }

  @Override
  public void resolve(P toResolve) throws ResolveException {
    P resolved = resolve((Hash) toResolve);
    map(resolved, toResolve);
    markAsResolved(resolved);
  }

  protected void markAsResolved(P resolved) {
    resolved.setResolver(this);
    resolved.setResolved(true);
  }

  protected void map(P resolved, P toResolve) {
  }
  

  @Override
  public boolean supportsType(Class<?> object) {
    return clazz.isAssignableFrom(object);
  }

  @Override
  public boolean supportsInstance(Object object) {
    if (object == null) {
      return supportsNull();
    }
    return supportsType(object.getClass());
  }

  protected boolean supportsNull() {
    return false;
  }
  
  protected P doResolve(Hash hash) throws ResolveException {
    throw new UnsupportedOperationException();
  }
  
  protected P doResolve(long index) throws ResolveException {
    throw new UnsupportedOperationException();
  }
}
