/**
 * Copyright (c) 2016 Chain4j developers
 * Distributed under the MIT software license, see the accompanying
 * file COPYING or http://www.opensource.org/licenses/mit-license.php.
 */

package org.blockchain.info;

import java.io.IOException;
import java.util.Map;

public interface HttpClient {

  /**
   * Perform a GET request on a Blockchain.info API resource.
   *
   * @param resource
   *          Resource path after https://blockchain.info/api/
   * @param params
   *          Map containing request parameters
   * @return String response
   * @throws BlochainInfoException
   *           If the server returns an error
   */
  String get(String resource, Map<String, String> params)
      throws BlochainInfoException, IOException;

  String get(String baseURL, String resource, Map<String, String> params)
      throws BlochainInfoException, IOException;

  /**
   * Perform a POST request on a Blockchain.info API resource.
   *
   * @param resource
   *          Resource path after https://blockchain.info/api/
   * @param params
   *          Map containing request parameters
   * @return String response
   * @throws BlochainInfoException
   *           If the server returns an error
   * @throws IOException
   *           If the server is not reachable
   */
  String post(String resource, Map<String, String> params)
      throws BlochainInfoException, IOException;

  String post(String baseURL, String resource, Map<String, String> params)
      throws BlochainInfoException, IOException;

}