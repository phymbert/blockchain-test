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

public class ValidatorRegisteryBase implements ValidatorRegistery {

  private static final Logger logger = LogManager.getLogger(ValidatorRegisteryBase.class);
  private List<Validator<?>> validators = new ArrayList<>();

  @SuppressWarnings("unchecked")
  @Override
  public <E> List<Validator<E>> validators(Class<E> instanceClass) {
    List<Validator<E>> supportedValidators = new ArrayList<>();
    for (Validator<?> validator : validators) {
      if (validator.supportsType(instanceClass)) {
        supportedValidators.add((Validator<E>) validator);
      }
    }
    return supportedValidators;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <E> List<Validator<E>> validators(E instance) {
    List<Validator<E>> supportedValidators = new ArrayList<>();
    for (Validator<?> validator : validators) {
      if (validator.supportsInstance(instance)) {
        supportedValidators.add((Validator<E>) validator);
      }
    }
    return supportedValidators;
  }

  @Override
  public <E> void addValidator(Validator<E> validator) {
    logger.info("Add validator {}", validator);
    validators.add(validator);
  }

  @Override
  public <E> void removeValidator(Validator<E> validator) {
    logger.info("Remove validator {}", validator);
    validators.remove(validator);
  }
  
  @Override
  public <E> ValidationResult<E> validate(E instance) throws ValidationException {
    ValidationResult<E> result = null;
    for (Validator<?> validator : validators(instance)) {
      @SuppressWarnings("unchecked")
      ValidationResult<E> result2 = ((Validator<E>) validator).validate(instance);
      if (result == null) {
        result = result2;
      } else {
        result = result.and(result2);
      }
    }
    return result;
  }
  
  @Override
  public <E> ValidationResult<E> validateAndThrow(E instance) throws ValidationException {
    ValidationResult<E> result = validate(instance);
    if (!result.isOk()) {
      throw new ValidationException(result);
    }
    return result;
  }

}
