package com.guogod.shopping.user;

import com.google.inject.Inject;
import com.guogod.shopping.dao.IMobileDao;
import com.guogod.shopping.utils.Constant;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by didi on 2015/7/23.
 */
public class AgentServer implements IAgentServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentServer.class);
    private ConcurrentHashMap<String, IAgent> agents = new ConcurrentHashMap<String, IAgent>();
    private Configuration configuration;
    private IMobileDao mobileDao;

    @Inject
    public AgentServer(Configuration configuration, IMobileDao mobileDao){
        this.configuration = configuration;
        this.mobileDao = mobileDao;
    }

    @Override
    public void start() {

    }

    @Override
    public void close() {

    }

    @Override
    public IAgent newAgent(String id, String params) {
        if ( !agents.containsKey(id) ){
            if ( Constant.MOBILE_TOKEN.equalsIgnoreCase(params) ){
                AgentBase base = new AgentBase();
                base.setStatus(AgentStatus.ONIDLE);
                base.setType(AgentConsumeType.MOBILE);
                base.setUserId(id);
                agents.put(id, new MobileAgent(base, mobileDao));
            }
        }
        return agents.get(id);
    }
}
