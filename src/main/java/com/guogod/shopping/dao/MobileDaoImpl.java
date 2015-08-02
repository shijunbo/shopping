package com.guogod.shopping.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.guogod.shopping.dao.mapper.IMobileMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by didi on 2015/7/26.
 */
@Singleton
public class MobileDaoImpl implements IMobileDao {

    private static final Logger LOGGER = LoggerFactory.getLogger("com.guogod.shopping.mobile");
    private SqlSessionFactory factory;

    @Inject
    public MobileDaoImpl(SqlSessionFactory factory) {
        super();
        this.factory = factory;
    }

    @Override
    public List<Map<String, Object>> selectMobileByPrice(int minPirce, int maxPrice, int limits) {
        SqlSession sqlSession = factory.openSession();
        try {
            Map<String, Integer> params = new HashMap<String, Integer>();
            params.put("minprice", minPirce);
            params.put("maxprice", maxPrice);
            params.put("size", limits);
            IMobileMapper mapper = sqlSession.getMapper(IMobileMapper.class);
            return mapper.getMobileByPrice(params);
//            sqlSession.selectList("shopping.ibatis.mapper.mobile.getMobileByPrice", params);
        }catch (Exception e){
            LOGGER.warn("selectMobileByPrice fail :" + e.getMessage());
        }finally {
            sqlSession.close();
        }
        return null;
    }
}
