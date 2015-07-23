package com.guogod.shopping.user;

/**
 * Created by didi on 2015/7/23.
 */
public abstract class Agent implements IAgent {
    private AgentBase baseinfo;
    private volatile AgentStatus status = AgentStatus.ONEND;

    public Agent(AgentBase baseinfo){
        this.baseinfo = baseinfo;
        this.setStatus(this.baseinfo.getStatus());;
    }

    @Override
    public boolean isOnline() {
        return status != AgentStatus.ONEND;
    }

    @Override
    public AgentBase getAgentBase() {
        return baseinfo;
    }

    @Override
    public AgentStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(AgentStatus status) {
        status = status;
    }
}
