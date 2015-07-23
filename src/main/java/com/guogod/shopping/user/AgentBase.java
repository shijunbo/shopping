package com.guogod.shopping.user;

/**
 * Created by didi on 2015/7/23.
 */
public class AgentBase {
    public String userId;
    public AgentConsumeType type;
    public AgentStatus status;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public AgentConsumeType getType() {
        return type;
    }

    public void setType(AgentConsumeType type) {
        this.type = type;
    }

    public AgentStatus getStatus() {
        return status;
    }

    public void setStatus(AgentStatus status) {
        this.status = status;
    }
}
