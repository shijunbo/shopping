package com.guogod.shopping.user;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by didi on 2015/7/23.
 */
public class AgentServer implements IAgentServer {
    private ConcurrentHashMap<String, IAgent> agents = new ConcurrentHashMap<String, IAgent>();
    private static AgentServer agentServer = new AgentServer();
    private AgentServer() {}
    public static AgentServer getIns() { return agentServer; }


    @Override
    public void start() {

    }

    @Override
    public void close() {

    }

    @Override
    public IAgent newAgent(String id, String params) {
        if ( !agents.contains(id) ){
            if ( params == "@手机" ){
                AgentBase base = new AgentBase();
                base.setStatus(AgentStatus.ONIDLE);
                base.setType(AgentConsumeType.MOBILE);
                base.setUserId(id);
                agents.put(id, new MobileAgent(base));
            }
        }
        return agents.get(id);
    }
}
