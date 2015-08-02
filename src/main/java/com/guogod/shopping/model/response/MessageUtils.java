package com.guogod.shopping.model.response;

import com.guogod.shopping.utils.Constant;

import java.util.Date;
import java.util.Map;

/**
 * Created by shijunbo on 2015/7/16.
 */
public class MessageUtils {
    public static void fillMessage(Message message, Map<String, Object> params){
        message.setFromUserName((String) params.get(Constant.REQ_TO_USER_NAME));
        message.setToUserName((String) params.get(Constant.REQ_FROM_USER_NAME));
        message.setCreateTime(new Date().getTime());
    }
}
