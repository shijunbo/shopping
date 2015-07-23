package com.guogod.shopping.user;

/**
 * Created by didi on 2015/7/23.
 */
public enum  AgentConsumeType {
    UNKOWN {public String getName(){return "未知";}},
    /** 手机 */
    MOBILE {public String getName(){return "手机";}},
    /** 电脑 */
    COMPUTER {public String getName(){return "电脑";}};

    public abstract String getName();
}
