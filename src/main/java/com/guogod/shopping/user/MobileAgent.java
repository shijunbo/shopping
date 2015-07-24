package com.guogod.shopping.user;

import com.guogod.shopping.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by didi on 2015/7/23.
 */
public class MobileAgent extends Agent {
    private static final Logger LOGGER = LoggerFactory.getLogger(Agent.class);

    private IAgentServer agentServer;

    public MobileAgent(AgentBase baseinfo) {
        super(baseinfo);
    }

    @Override
    public String doWork(String params) {
        String result = null;
        if ( this.status == AgentStatus.ONCOMSUMER ){
            setStatus(AgentStatus.ONEND);
            StringBuffer buffer = new StringBuffer();
            buffer.append("小白购物为你推荐价格在").append(params).append("畅销手机:").append("\n\n");
            buffer.append("    ").append("手机品牌").append(" ").append("型号").append(" ").append("畅销指数").append("\n\n");
            buffer.append("    ").append("小米Note").append(" ").append("note2").append(" ").append("五星（******）").append("\n\n");
                    result = buffer.toString();
        }else if ( this.status == AgentStatus.ONIDLE  || ( Constant.MOBILE_TOKEN.equalsIgnoreCase(params) && this.status == AgentStatus.ONEND) ) {
            setStatus(AgentStatus.ONCOMSUMER);
            result = "请输出手机价格范围，列如 1000-1500";
        }
        return result;
    }
}
