package com.guogod.shopping.model.request;

/**
 * Created by shijunbo on 2015/7/16.
 */
public class TextMessage extends BaseMessage {
    /**
    * 回复的消息内容
    */
    private String Content;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
