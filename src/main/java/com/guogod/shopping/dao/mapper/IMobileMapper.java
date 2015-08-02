package com.guogod.shopping.dao.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2014/10/8.
 */
public interface IMobileMapper {
    List<Map<String, Object>> getMobileByPrice(Map<String, Integer> map);
}
