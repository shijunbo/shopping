package com.guogod.shopping.service;

import com.google.inject.Inject;
import com.guogod.shopping.http.DXJSONResponseHandler;
import com.guogod.shopping.utils.JSONUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Map;
import java.util.Set;

/**
 * Created by shijunbo on 2015/7/17.
 */
public class ApiServiceImpl implements ApiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiServiceImpl.class);
    private HttpClient httpClient;
    private Configuration configuration;

    @Inject
    public ApiServiceImpl(Configuration configuration, HttpClient httpClient){
        this.configuration = configuration;
        this.httpClient = httpClient;
    }

    @Override
    public Map<String, Object> doAccessTokens(Map<String, String> queryParams){
        String api = configuration.getString("api.access_token");
        if (StringUtils.isBlank(api)) {
            LOGGER.error("Miss config: api.access_token");
            return null;
        }

        Map<String, Object> result = null;
        URI apiUri = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(api);
            Set<String> keys = queryParams.keySet();
            for ( String key : keys ){
                uriBuilder.addParameter(key, queryParams.get(key));
            }
            apiUri = uriBuilder.build();
            HttpGet httpGet = new HttpGet(apiUri);
            JsonNode jsonObject = httpClient.execute(httpGet, new DXJSONResponseHandler());
            if ( null != jsonObject ) {
                result = JSONUtils.toObject(jsonObject, JSONUtils.MAP_TYPE_REF);
            }
        } catch (HttpResponseException e) {
            if (404 != e.getStatusCode()) {
                LOGGER.warn("Don't request api.access_token {}  detail {}", apiUri.toString(), e.getMessage());
            }
        } catch (Exception e) {
            LOGGER.warn("Don't request api.access_token  detail {}", e.getStackTrace());
        }
        return result;
    }

    @Override
    public Map<String, Object> createMenu(Map<String, String> queryParams, String menu){
        String api = configuration.getString("api.menu_create");
        if (StringUtils.isBlank(api)) {
            LOGGER.error("Miss config: api.menu_create");
            return null;
        }

        Map<String, Object> result = null;
        URI apiUri = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(api);
            Set<String> keys = queryParams.keySet();
            for ( String key : keys ){
                uriBuilder.addParameter(key, queryParams.get(key));
            }
            apiUri = uriBuilder.build();
            HttpPost httpPost = new HttpPost(apiUri);
            httpPost.setEntity(new StringEntity(menu, ContentType.APPLICATION_JSON));
            JsonNode jsonObject = httpClient.execute(httpPost, new DXJSONResponseHandler());
            if ( null != jsonObject ) {
                result = JSONUtils.toObject(jsonObject, JSONUtils.MAP_TYPE_REF);
            }
        } catch (HttpResponseException e) {
            if (404 != e.getStatusCode()) {
                LOGGER.warn("Don't request api.menu_create {}  detail {}", apiUri.toString(), e.getMessage());
            }
        } catch (Exception e) {
            LOGGER.warn("Don't request api.menu_create  detail {}", e.getMessage());
        }
        return result;
    }
}
