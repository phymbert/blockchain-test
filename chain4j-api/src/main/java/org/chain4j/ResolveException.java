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

public class ResolveException extends Chain4jException {
  private static final long serialVersionUID = -3250172764666797952L;

  public ResolveException() {
    super();
  }

  public ResolveException(String message, Throwable cause) {
    super(message, cause);
  }

  public ResolveException(String message) {
    super(message);
  }

  public ResolveException(Throwable cause) {
    super(cause);
  }

}
