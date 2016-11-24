/**
 * Copyright (c) 2016 Chain4j developers
 * Distributed under the MIT software license, see the accompanying
 * file COPYING or http://www.opensource.org/licenses/mit-license.php.
 */

package org.blockchain.info;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.chain4j.MinerBase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Tooks from https://github.com/blockchain/api-v1-client-java
 */
public class BlockchainInfoHttpClient implements HttpClient {
  private static final String        BASE_URL   = "https://chain.so/";

  public static volatile int TIMEOUT_MS = 10000;

  private static final Logger logger = LogManager.getLogger(MinerBase.class);

  @Override
  public String get(String resource, Map<String, String> params)
            throws BlochainInfoException, IOException {
    return openURL(BASE_URL, resource, params, "GET");
  }

  @Override
  public String get(String baseURL, String resource, Map<String, String> params)
      throws BlochainInfoException, IOException {
    return openURL(baseURL, resource, params, "GET");
  }

  @Override
  public String post(String resource, Map<String, String> params)
            throws BlochainInfoException, IOException {
    return openURL(BASE_URL, resource, params, "POST");
  }

  @Override
  public String post(String baseURL, String resource, Map<String, String> params)
      throws BlochainInfoException, IOException {
    return openURL(baseURL, resource, params, "POST");
  }

  private String openURL(String baseURL, String resource, Map<String, String> params,
      String requestMethod) throws BlochainInfoException, IOException {
    String encodedParams = urlEncodeParams(params);
    URL url = null;
    BlochainInfoException apiException = null;
    IOException ioException = null;

    String responseStr = null;

    if (requestMethod.equals("GET")) {
      if (encodedParams.isEmpty()) {
        url = new URL(baseURL + resource);
      } else {
        url = new URL(baseURL + resource + '?' + encodedParams);
      }
    } else if (requestMethod.equals("POST")) {
      url = new URL(baseURL + resource);
    }

    HttpURLConnection conn = null;

    try {
      conn = (HttpURLConnection)url.openConnection();
      conn.setRequestMethod(requestMethod);
      conn.setConnectTimeout(TIMEOUT_MS);

      if (requestMethod.equals("POST")) {
        byte[] postBytes = encodedParams.getBytes("UTF-8");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postBytes.length));
        conn.getOutputStream().write(postBytes);
        conn.getOutputStream().close();
      }

      if (conn.getResponseCode() != 200) {
        apiException = new BlochainInfoException(inputStreamToString(conn.getErrorStream()));
      } else {
        responseStr = inputStreamToString(conn.getInputStream());
      }
    } catch (IOException e) {
      ioException = e;
    } finally {
      try {
        if (apiException != null) {
          conn.getErrorStream().close();
        }
        conn.getInputStream().close();
      } catch (Exception ex) {
        logger.error(ex);
      }

      if (ioException != null) {
        throw ioException;
      }

      if (apiException != null) {
        throw apiException;
      }
    }

    return responseStr;
  }

  private String urlEncodeParams(Map<String, String> params) {
    String result = "";

    if (params != null && params.size() > 0) {
      try {
        StringBuilder data = new StringBuilder();
        for (Entry<String, String> kvp : params.entrySet()) {
          if (data.length() > 0) {
            data.append('&');
          }

          data.append(URLEncoder.encode(kvp.getKey(), "UTF-8"));
          data.append('=');
          data.append(URLEncoder.encode(kvp.getValue(), "UTF-8"));
        }
        result = data.toString();
      } catch (UnsupportedEncodingException e) {
        logger.error(e);
      }
    }

    return result;
  }

  private String inputStreamToString(InputStream is) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));

    StringBuilder responseStringBuilder = new StringBuilder();
    String line;

    while ((line = reader.readLine()) != null) {
      responseStringBuilder.append(line);
    }

    reader.close();

    return responseStringBuilder.toString();
  }
}
