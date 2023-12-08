package com.lq.im.service.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import javax.annotation.Resource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;

@Slf4j
@Component
public class HttpRequestUtils {
    @Resource
    private CloseableHttpClient httpClient;

    @Resource
    private RequestConfig requestConfig;

    public String doGet(String url) throws URISyntaxException {
        return doGet(url, null);
    }

    public String doGet(String url, Map<String, Object> params) throws URISyntaxException {
        return doGet(url, params, null);
    }

    public String doGet(String url, Map<String, Object> params, String charset) throws URISyntaxException {
        return doGet(url, params, null, charset);
    }

    public String doGet(String url, Map<String, Object> params, Map<String, Object> headers, String charset)
            throws URISyntaxException {
        if (StringUtils.isBlank(charset)) {
            charset = StandardCharsets.UTF_8.displayName();
        }
        URIBuilder uriBuilder = new URIBuilder(url);
        if (!CollectionUtils.isEmpty(params)) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                uriBuilder.addParameter(entry.getKey(), entry.getValue().toString());
            }
        }
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        httpGet.setConfig(requestConfig);

        if (!CollectionUtils.isEmpty(headers)) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                httpGet.addHeader(entry.getKey(), entry.getValue().toString());
            }
        }
        String result = "";
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), charset);
            }
        } catch (IOException e) {
            log.error("An error occurred while doing HTTP GET with url {}", url);
            throw new RuntimeException(e);
        }
        return result;
    }

    public <T> T doGet(String url, Class<T> aClass, Map<String, Object> params,
                       String charset) throws URISyntaxException {
        String result = doGet(url, params, charset);
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, aClass);
        }
        return null;
    }

    public <T> T doGet(String url, Class<T> aClass, Map<String, Object> params, Map<String, Object> headers,
                       String charset) throws URISyntaxException {
        String result = doGet(url, params, headers, charset);
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, aClass);
        }
        return null;
    }

    public String doPost(String url) throws URISyntaxException {
        return doPost(url, null, null, null);
    }

    public String doPost(String url, Map<String, Object> params, String jsonBody,
                         String charset) throws URISyntaxException {
        return doPost(url, params, null, jsonBody, charset);
    }

    public String doPost(String url, Map<String, Object> params, Map<String, Object> headers, String jsonBody,
                         String charset) throws URISyntaxException {
        if (StringUtils.isBlank(charset)) {
            charset = StandardCharsets.UTF_8.displayName();
        }
        URIBuilder uriBuilder = new URIBuilder(url);
        if (!CollectionUtils.isEmpty(params)) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                uriBuilder.addParameter(entry.getKey(), entry.getValue().toString());
            }
        }
        HttpPost httpPost = new HttpPost(uriBuilder.build());
        httpPost.setConfig(requestConfig);
        if (StringUtils.isNotBlank(jsonBody)) {
            StringEntity stringEntity = new StringEntity(jsonBody, charset);
            stringEntity.setContentEncoding(charset);
            stringEntity.setContentType(APPLICATION_JSON.getMimeType());
            httpPost.setEntity(stringEntity);
        }
        if (!CollectionUtils.isEmpty(headers)) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue().toString());
            }
        }
        String result = "";
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), charset);
            }
        } catch (IOException e) {
            log.error("An error occurred while doing HTTP POST with url {}", url);
            throw new RuntimeException(e);
        }
        return result;
    }

    public <T> T doPost(String url, Class<T> aClass, Map<String, Object> params, String jsonBody,
                        String charset) throws URISyntaxException {
        String result = doPost(url, params, jsonBody, charset);
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, aClass);
        }
        return null;
    }

    public <T> T doPost(String url, Class<T> aClass, Map<String, Object> params, Map<String, Object> headers,
                        String jsonBody, String charset) throws URISyntaxException {
        String result = doPost(url, params, headers, jsonBody, charset);
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, aClass);
        }
        return null;
    }
}
