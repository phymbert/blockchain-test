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

public interface ValidatorRegistery {
  <E> List<Validator<E>> validators(Class<E> instanceClass);
  
  <E> List<Validator<E>> validators(E instance);

  <E> void addValidator(Validator<E> validator);
  
  <E> void removeValidator(Validator<E> validator);

  <E> ValidationResult<E> validate(E instance) throws ValidationException;

  <E> ValidationResult<E> validateAndThrow(E instance) throws ValidationException;
}
