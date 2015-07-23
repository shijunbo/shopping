package com.guogod.shopping.model.response;

/**
 * Created by shijunbo on 2015/7/16.
 */
public interface Message {
    public void setToUserName(String toUserName);
    public void setFromUserName(String fromUserName);
    public void setCreateTime(long createTime);
    public void setMsgType(String msgType);

}
