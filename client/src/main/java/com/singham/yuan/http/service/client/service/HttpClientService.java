package com.singham.yuan.http.service.client.service;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientService.class);

    private static final int TIMEOUT = 120 * 1000;

    private static final Charset UTF_8 = Consts.UTF_8;

    public static String post(String url, Map<String, String[]> parameters, CloseableHttpClient httpclient) {
        return getContent(httpclient, httpPost(url, parameters));
    }

    public static String post(String url, String request, CloseableHttpClient httpclient) {
        return getContent(httpclient, httpPost(url, request));
    }

    private static String getContent(CloseableHttpClient httpclient, HttpPost httpPost) {
        try {
            HttpResponse httpResponse = httpclient.execute(httpPost);
            return EntityUtils.toString(httpResponse.getEntity(), UTF_8);
        } catch (Exception e) {
            LOGGER.error("HttpClientUtils getResult error", e);
            return "";
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                LOGGER.error("httpclient.close() error", e);
            }
        }
    }

    private static HttpPost httpPost(String url, String request) {
        return httpPost(url, new StringEntity(request, contentType()));
    }

    private static ContentType contentType() {
        return ContentType.create("text/xml", UTF_8);
    }

    private static HttpPost httpPost(String url, Map<String, String[]> parameters) {
        return httpPost(url, createParams(parameters));
    }

    private static HttpEntity createParams(Map<String, String[]> parameterMap) {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            parameters.add(new BasicNameValuePair(entry.getKey(), ArrayUtils.isEmpty(entry.getValue()) ? "" : entry.getValue()[0]));
        }
        return new UrlEncodedFormEntity(parameters, UTF_8);
    }

    private static HttpPost httpPost(String url, HttpEntity httpEntity) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(httpEntity);
        httpPost.setConfig(requestConfig());
        return httpPost;
    }

    private static RequestConfig requestConfig() {
        return RequestConfig.custom().setSocketTimeout(TIMEOUT).setConnectTimeout(TIMEOUT).setConnectionRequestTimeout(TIMEOUT).build();
    }

}
