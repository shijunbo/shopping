package com.guogod.shopping.service;

import com.google.inject.Inject;
import com.guogod.shopping.task.ISubTask;
import com.guogod.shopping.task.TaskError;
import com.guogod.shopping.task.ITaskSchedule;
import com.guogod.shopping.task.TickTaskSchedule;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.Lock;

/**
 * Created by shijunbo on 2015/7/16.
 */
public class AccessToken {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessToken.class);
    private ITaskSchedule<LazyUpdater> _updater;
    private Configuration configuration;
    private ApiService apiService;
    private ReadWriteLock rwLockCleanerAgent = new ReentrantReadWriteLock();
    private ConcurrentHashMap<String, Object> accessTokens = new ConcurrentHashMap<String, Object>();

    @Inject
    public AccessToken(Configuration configuration, ApiService apiService){
        this.configuration = configuration;
        this.apiService = apiService;
        this._updater = new TickTaskSchedule<AccessToken.LazyUpdater>(new LazyUpdater(), 180*1000);
        this._updater.start();
    }

    public String getAccessToken(){
        Lock ck = rwLockCleanerAgent.readLock();
        ck.lock();
        try{
            return (String)accessTokens.get("access_token");
        }finally {
            ck.unlock();
        }
    }

    public void setAccessTokenService(Map<String,Object> result){
        Lock wk = rwLockCleanerAgent.writeLock();
        wk.lock();
        try{
            accessTokens.put("access_token", (String) result.get("access_token"));
            accessTokens.put("expires_in", (Integer)result.get("expires_in"));
        }finally {
            wk.unlock();
        }
    }

    class LazyUpdater implements ISubTask {
        @Override
        public void onStart() {
        }

        @Override
        public void onEnd() {
        }

        @Override
        public void onTick(long elapse) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("grant_type", "client_credential");
            params.put("appid", configuration.getString("api.appkey",""));
            params.put("secret", configuration.getString("api.appsecret", ""));
            Map<String, Object> result = apiService.doAccessTokens(params);
            if ( result.containsKey("errcode") ){
                LOGGER.warn("[LazyUpdater] request access_token errcode {} errmsg {}", result.get("errcode"), result.get("errmsg"));
                _updater.setTickPollInterval(180*1000);
            }else{
                LOGGER.info("[LazyUpdater] request access_token response {}", result);
                setAccessTokenService(result);
                _updater.setTickPollInterval((int)accessTokens.get("expires_in")*1000);
            }
        }

        @Override
        public void onCancel() {
            LOGGER.warn("[LazyUpdater] cancel");
        }

        @Override
        public void onException(TaskError e) {
            LOGGER.error("[LazyUpdater] {}", e);
        }

        @Override
        public float getProgress() {
            return 0;
        }
    }
}
