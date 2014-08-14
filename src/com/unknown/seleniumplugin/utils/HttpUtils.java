package com.unknown.seleniumplugin.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by mike-sid on 11.02.14.
 * Operations with http protocol
 */
public class HttpUtils {
    private static HttpClient client;
    static {
        client = HttpClientBuilder.create().disableRedirectHandling().build();
    }

    /**
     * sends http GET request
     *
     * @param url url for request
     * @return {@link org.apache.http.HttpResponse} instance  or null if error occured
     */
    public static HttpResponse sendGetRequest(String url) {
        HttpGet getRequest = new HttpGet(url);
        HttpResponse response = null;
        try {
            response = client.execute(getRequest);
            getRequest.abort();
        } catch (IOException e) {
            //do nothing
//            e.printStackTrace();
        }
        return response;
    }

    /**
     * sends http GET request with redirect
     *
     * @param url url for request
     * @return {@link org.apache.http.HttpResponse} instance  or null if error occured
     */
    public static HttpResponse sendGetRequestWithRedirect(String url) {
        client = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(url);
        HttpResponse response = null;
        try {
            response = client.execute(getRequest);
            getRequest.abort();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String getStringFromResponse(HttpResponse response) {
        try {
            return EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
        } catch (IOException e) {
            return null;
        }
    }

    public static String getStringResponseFromUrl(String url) {
        try {
            HttpGet getRequest = new HttpGet(url);
            HttpResponse response = client.execute(getRequest);
            String entityString = getStringFromResponse(response);
            getRequest.abort();
            return entityString;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * checks if response has no errors(returns codes:200, not returns:400)
     *
     * @param response {@link org.apache.http.HttpResponse} instance
     * @return true if response is good
     */
    public static boolean isResponseGood(HttpResponse response) {
        if (response == null) {
            return false;
        }
        int code = response.getStatusLine().getStatusCode();
        return isCodeGood(code);
    }

    public static boolean isCodeGood(int code) {
        return code != 404;
    }

    public static HttpResponse sendDeleteRequest(String url) {
        HttpDelete getRequest = new HttpDelete(url);
        HttpResponse response = null;
        try {
            response = client.execute(getRequest);
            getRequest.abort();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static HttpResponse sendPostRequest(String url, String body, Map<String, String> headers, ContentType contentType) {
        HttpPost post = new HttpPost(url);
        if(headers != null) {
            for(String header : headers.keySet()){
                post.setHeader(header, headers.get(header));
            }
        }
        post.setEntity(new StringEntity(body, contentType));
        HttpResponse response = null;
        try {
            response = client.execute(post);
            post.abort();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;

    }

    public static HttpResponse sendPutRequest(String url, String body, Map<String, String> headers, ContentType contentType) {
        HttpPut post = new HttpPut(url);
        if(headers != null) {
            for(String header : headers.keySet()){
                post.setHeader(header, headers.get(header));
            }
        }
        post.setEntity(new StringEntity(body, contentType));
        HttpResponse response = null;
        try {
            response = client.execute(post);
            post.abort();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;

    }


    public static int getResponseCode(HttpResponse response) {
        if (response == null) {
            return -1;
        } else {
            return response.getStatusLine().getStatusCode();
        }
    }
}
