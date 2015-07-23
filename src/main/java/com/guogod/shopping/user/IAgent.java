package com.guogod.shopping.user;

/**
 * Created by didi on 2015/7/23.
 */
public interface IAgent {
    boolean isOnline();
    AgentBase getAgentBase();
    AgentStatus getStatus();
    void setStatus(AgentStatus status);

    String doWork(String input);
}
