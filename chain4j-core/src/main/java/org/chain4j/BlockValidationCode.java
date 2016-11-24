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

public enum BlockValidationCode {
  NOT_MAIN_CHAIN,
  OUTDATED,
  NOT_RESOLVED;

  public static BlockValidationResultStatusBased result(BlockValidationCode code) {
    return code.new BlockValidationResultStatusBased();
  }

  private class BlockValidationResultStatusBased
          implements ValidationResult<Block<?, ?, ?, ?>> {

    @Override
    public ValidationStatus getStatus() {
      return ValidationStatus.NOT_PASSED;
    }

    @Override
    public boolean isOk() {
      return false;
    }

    @Override
    public ValidationResult<Block<?, ?, ?, ?>> and(ValidationResult<Block<?, ?, ?, ?>> and) {
      throw new UnsupportedOperationException();
    }

    @Override
    public String getDescription() {
      return name();
    }
    
  }
}
