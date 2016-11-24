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

import java.util.List;

public interface PayloadResolverRegistery {
  <P extends Payload & Resolvable<P>> List<PayloadResolver<P>> resolvers(Class<P> instanceClass);
  
  <P extends Payload & Resolvable<P>> List<PayloadResolver<P>> resolvers(P instance);

  <P extends Payload & Resolvable<P>> void addResolver(PayloadResolver<P> validator);
  
  <P extends Payload & Resolvable<P>> void removeResolver(PayloadResolver<P> validator);

  <P extends Payload & Resolvable<P>> P resolve(Class<P> payloadType, Hash hash)
            throws ResolveException;

  <P extends Payload & Resolvable<P>> P resolve(Class<P> payloadType, long index)
            throws ResolveException;

  <P extends Payload & Resolvable<P>> void resolve(P instance)
            throws ResolveException;
}
