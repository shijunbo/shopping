package com.guogod.shopping.dao;

import java.util.List;
import java.util.Map;

/**
 * Created by didi on 2015/7/26.
 */
public interface IMobileDao {
    List<Map<String, Object>> selectMobileByPrice(int minPirce, int maxPrice, int limits);
}
