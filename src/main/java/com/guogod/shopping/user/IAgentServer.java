package com.guogod.shopping.user;

/**
 * Created by didi on 2015/7/23.
 */
public interface IAgentServer {
    void start();
    void close();
    IAgent newAgent(String id, String params);
}
