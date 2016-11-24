/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the LICENSE.txt file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.chain4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class PayloadResolverRegisteryBase implements PayloadResolverRegistery {

  private static final Logger logger = LogManager.getLogger(PayloadResolverRegisteryBase.class);
  private List<PayloadResolver<?>> resolvers = new ArrayList<>();

  @SuppressWarnings("unchecked")
  @Override
  public <P extends Payload & Resolvable<P>> List<PayloadResolver<P>> resolvers(
            Class<P> instanceClass) {
    List<PayloadResolver<P>> supportedResolvers = new ArrayList<>();
    for (PayloadResolver<?> resolver : resolvers) {
      if (resolver.supportsType(instanceClass)) {
        supportedResolvers.add((PayloadResolver<P>) resolver);
      }
    }
    return supportedResolvers;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <P extends Payload & Resolvable<P>> List<PayloadResolver<P>> resolvers(P instance) {
    List<PayloadResolver<P>> supportedResolvers = new ArrayList<>();
    for (PayloadResolver<?> resolver : resolvers) {
      if (resolver.supportsInstance(instance)) {
        supportedResolvers.add((PayloadResolver<P>) resolver);
      }
    }
    return supportedResolvers;
  }

  @Override
  public <P extends Payload & Resolvable<P>> void addResolver(PayloadResolver<P> resolver) {
    logger.info("Add resolver {}", resolver);
    resolvers.add(resolver);
  }

  @Override
  public <P extends Payload & Resolvable<P>> void removeResolver(PayloadResolver<P> resolver) {
    logger.info("Remove resolver {}", resolver);
    resolvers.remove(resolver);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <P extends Payload & Resolvable<P>> void resolve(P instance)
            throws ResolveException {
    for (PayloadResolver<?> resolver : resolvers(instance)) {
      try {
        ((PayloadResolver<P>) resolver).resolve(instance);
        return;
      } catch (ResolveException e) {
        logger.debug("Unable to resolve {} from {} got {}",
              instance, resolver, e.getMessage(), e);
      }
    }
  }

  @Override
  public <P extends Payload & Resolvable<P>> P resolve(Class<P> payloadType, Hash hash)
            throws ResolveException {
    for (PayloadResolver<?> resolver : resolvers(payloadType)) {
      try {
        @SuppressWarnings("unchecked")
        P resolved = ((PayloadResolver<P>) resolver).resolve(hash);
        if (resolved != null) {
          return resolved;
        }
      } catch (ResolveException e) {
        if (logger.isDebugEnabled()) {
          logger.debug("Unable to resolve {} {} from {} got {}",
              payloadType, hash, resolver, e.getMessage(), e);
        }
      }
    }
    throw new ResolveException(); // FIXME add details
  }

  @Override
  public <P extends Payload & Resolvable<P>> P resolve(Class<P> payloadType, long index)
            throws ResolveException {
    for (PayloadResolver<?> resolver : resolvers(payloadType)) {
      try {
        @SuppressWarnings("unchecked")
        P resolved = ((PayloadResolver<P>) resolver).resolve(index);
        if (resolved != null) {
          return resolved;
        }
      } catch (ResolveException e) {
        if (logger.isDebugEnabled()) {
          logger.debug("Unable to resolve {} {} from {} got {}",
              payloadType, index, resolver, e.getMessage(), e);
        }
      }
    }
    throw new ResolveException(); // FIXME add details
  }
}
