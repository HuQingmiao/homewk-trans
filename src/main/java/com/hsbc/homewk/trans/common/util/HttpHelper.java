package com.hsbc.homewk.trans.common.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * http请求工具
 */
public class HttpHelper {

    /**
     * 发起GET请求
     *
     * @param uri        请求的url地址
     * @param headerMap  请求头参数map
     * @param paramerMap 请求参数map
     * @return 服务端返回的对象
     */
    public static String get(String uri, Map<String, String> headerMap, Map<String, Object> paramerMap) {
        StringBuilder urlParameters = addGetParameters(paramerMap);
        HttpGet httpGet = new HttpGet(uri + urlParameters);
        setHeaders(httpGet, headerMap);

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        return execute(httpClient, httpGet);
    }


    /**
     * 发起POST请求
     *
     * @param uri        请求的url地址
     * @param headerMap  请求头参数map
     * @param paramerMap 请求参数map
     * @return 请求的结果字符串
     */
    public static String post(String uri, Map<String, String> headerMap, Map<String, String> paramerMap, Object request)
            throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(uri);
        addParameters(httpPost, paramerMap, request);
        setHeaders(httpPost, headerMap);

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        return execute(httpClient, httpPost);
    }

    /**
     * 发起PUT请求
     *
     * @param uri        请求的url地址
     * @param headerMap  请求头参数map
     * @param paramerMap 请求参数map
     * @return 服务端返回的对象
     */
    public static String put(String uri, Map<String, String> headerMap, Map<String, String> paramerMap, Object request)
            throws UnsupportedEncodingException {
        HttpPut httpPut = new HttpPut(uri);
        addParameters(httpPut, paramerMap, request);
        setHeaders(httpPut, headerMap);

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        return execute(httpClient, httpPut);
    }


    /**
     * 发起DELETE请求
     *
     * @param uri        请求的url地址
     * @param headerMap  请求头参数map
     * @param paramerMap 请求参数map
     * @return 请求的结果字符串
     */
    public static String delete(String uri, Map<String, String> headerMap, Map<String, Object> paramerMap) {
        StringBuilder urlParameters = addGetParameters(paramerMap);
        HttpDelete httpDelete = new HttpDelete(uri + urlParameters);
        setHeaders(httpDelete, headerMap);

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        return execute(httpClient, httpDelete);
    }


    /**
     * 设置请求头参数
     *
     * @param headerMap   请求头map集合
     * @param httpRequest http请求对象
     */
    private static void setHeaders(HttpRequestBase httpRequest, Map<String, String> headerMap) {
        if (headerMap != null && !headerMap.isEmpty()) {
            for (String key : headerMap.keySet()) {
                String value = headerMap.get(key);
                httpRequest.setHeader(key, value);
            }
        }
    }

    /**
     * GET,DELETE请求添加参数
     *
     * @param parameterMap 请求参数的map
     * @return StringBuilder
     */
    private static StringBuilder addGetParameters(Map<String, Object> parameterMap) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("?");
        for (String key : parameterMap.keySet()) {
            Object value = parameterMap.get(key);
            urlBuilder.append(key).append("=").append(value).append("&");
        }
        return urlBuilder;
    }

    /**
     * 对POST、PUT 请求添加参数
     *
     * @param parameterMap   需要添加到请求参数的map集合
     * @param postPutRequest post或者get请求对象
     * @param request        请求入参对象
     * @throws UnsupportedEncodingException 传递参数引发的异常
     */
    private static void addParameters(HttpEntityEnclosingRequestBase postPutRequest, Map<String, String> parameterMap, Object request)
            throws UnsupportedEncodingException {
        if (request != null) {
            postPutRequest.setHeader("Content-Type", "application/json;charset=UTF-8");
            postPutRequest.setEntity(new StringEntity(JSONObject.toJSONString(request), StandardCharsets.UTF_8));
        } else {
            // 1、构造list集合，往里面存请求的数据
            List<NameValuePair> list = new ArrayList<>();
            if (parameterMap != null) {
                for (String key : parameterMap.keySet()) {
                    String value = parameterMap.get(key);
                    BasicNameValuePair basicNameValuePair = new BasicNameValuePair(key, value);
                    list.add(basicNameValuePair);
                }
            }
            //2 我们发现Entity是一个接口，所以只能找实现类，发现实现类又需要一个集合，集合的泛型是NameValuePair类型
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(list);
            //3 通过setEntity 将我们的entity对象传递过去
            postPutRequest.setEntity(formEntity);
        }
    }

    /**
     * 发起请求、调用服务。
     *
     * @param httpClient  htt请求对象
     * @param httpRequest 请求的http方式对象
     * @return 请求结果字符串
     */
    private static String execute(CloseableHttpClient httpClient, HttpRequestBase httpRequest) {
        try {
            CloseableHttpResponse response = httpClient.execute(httpRequest);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                String errMsg = MessageFormat.format("请求失败, statusCode = {0}", response.getStatusLine().getStatusCode());
                throw new RuntimeException(errMsg);
            }

            //获取返回的数据
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

