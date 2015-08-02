package com.guogod.shopping.rest.api;

import com.google.inject.Inject;
import com.guogod.shopping.model.response.Message;
import com.guogod.shopping.model.response.MessageUtils;
import com.guogod.shopping.model.response.TextMessage;
import com.guogod.shopping.user.IAgent;
import com.guogod.shopping.user.IAgentServer;
import com.guogod.shopping.utils.Constant;
import com.guogod.shopping.utils.XMLUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.restlet.data.CharacterSet;
import org.restlet.data.Encoding;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.util.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import com.guogod.shopping.utils.SignUtils;

/**
 * Created by shijunbo on 2015/7/15.
 */
public class RecommandMobileServerResource extends AbstractServerResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecommandMobileServerResource.class);
    private IAgentServer agentServer;

    @Inject
    public RecommandMobileServerResource(Configuration config, IAgentServer agentServer) {
        this.agentServer = agentServer;
        this.configuration = config;
    }

    @Override
    public Representation processGetRequest(Form form) {
        Map<String, String> values = form.getValuesMap();
        // 微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
        String signature = values.get("signature");
        // 时间戳
        String timestamp = values.get("timestamp");
        // 随机数
        String nonce = values.get("nonce");
        // 随机字符串
        String echostr = values.get("echostr");
        String response = null;
        try {
            response = echostr;
//            // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，否则接入失败
//            if ( SignUtils.checkSignature(signature, timestamp, nonce, configuration.getString("api.token", "shop")) ) {
//                response = echostr;
//            }
        } catch (Exception e) {
            setStatus(Status.SERVER_ERROR_INTERNAL);
            LOGGER.warn("processGetRequest unknown error: {}", e);
        }
        return this.retrievalResponse.buildStringResponse(response);
    }

    @Override
    public Representation processPostRequest(Form form, Representation representation) {
        Message message = null;
        try {
            if (null == representation) {
                throw new IllegalArgumentException("invalid request.");
            }

            InputStream inputStream = representation.getStream();
            if (!(inputStream instanceof GZIPInputStream)) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream((int) representation.getSize());
                IOUtils.copy(inputStream, byteArrayOutputStream);
                byte[] bytes = byteArrayOutputStream.toByteArray();
                if (byteArrayOutputStream.size() > 1 && bytes[0] == 31 && bytes[1] == -117) {
                    inputStream = new GZIPInputStream(new ByteArrayInputStream(bytes));
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("missing gzip header,manual ungzip");
                    }
                } else {
                    inputStream = new ByteArrayInputStream(bytes);
                }
            }

            String body = IOUtils.toString(inputStream, Charset.forName("UTF-8"));
            if ( LOGGER.isDebugEnabled() ){
                LOGGER.debug("{}", body);
            }
            Map<String, Object> bodyMap = XMLUtils.parseXml(body);
            checkPostBodyParams(bodyMap);
            if ( Constant.REQ_MESSAGE_TYPE_TEXT.equalsIgnoreCase((String) bodyMap.get( Constant.REQ_MESSAGE_TYPE )) ){
                message = new TextMessage();
                MessageUtils.fillMessage(message, bodyMap);
                message.setMsgType(Constant.REQ_MESSAGE_TYPE_TEXT);
                String userId = (String)bodyMap.get(Constant.REQ_FROM_USER_NAME);
                String content = (String) bodyMap.get(Constant.REQ_CONTENT);
                String result = null;
                IAgent agent = this.agentServer.newAgent(userId, content);
                if ( null != agent ){
                    result = agent.doWork(content);
                }
                if ( null !=  result ) {
                    content = result;
                }
                ((TextMessage) message).setContent(content);
            }
        } catch (IllegalArgumentException e) {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return this.retrievalResponse.buildStringResponse("post body miss params");
        } catch (Exception e) {
            setStatus(Status.SERVER_ERROR_INTERNAL);
            LOGGER.warn("processPostRequest unknown error: {}", e);
        }
        return this.retrievalResponse.buildXMLResponse(message);
    }
}
