package com.guogod.shopping.service;

import java.util.Map;

/**
 * Created by shijunbo on 2015/7/17.
 */
public interface ApiService {
    public Map<String, Object> doAccessTokens(Map<String, String> queryParams);
    public Map<String, Object> createMenu(Map<String, String> queryParams, String menu);
}
