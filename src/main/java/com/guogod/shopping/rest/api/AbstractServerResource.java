package com.guogod.shopping.rest.api;

import com.guogod.shopping.utils.*;
import com.guogod.shopping.rest.model.RetrievalResponse;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.restlet.Request;
import org.restlet.data.CacheDirective;
import org.restlet.data.ClientInfo;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by shijunbo on 2015/7/15.
 */
public class AbstractServerResource extends ServerResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractServerResource.class);
    private static final String dx_key = "AYUEJ1AWWHKTYENYUG1RP1B52SY5R1OJ";

    private static final Random RANDOM_GENERATOR = new Random();
    @SuppressWarnings("serial")
    private static final HashSet<String> POST_BODY_XML_BASIC_PARAMS = new HashSet<String>() {{
        add(Constant.REQ_FROM_USER_NAME);
        add(Constant.REQ_TO_USER_NAME);
        add(Constant.REQ_MESSAGE_TYPE);
        add(Constant.REQ_CREATE_TIME);
        add(Constant.REQ_MSG_ID);
    }};
    protected Configuration configuration;

    protected boolean pretty;

    protected RetrievalResponse retrievalResponse = new RetrievalResponse();

    public AbstractServerResource() {
    }

    @Override
    public void doInit() {
        Form form = this.getQuery();
        pretty = "true".equalsIgnoreCase(form.getFirstValue("pretty", true));
        String logid = StringUtils.stripToNull(form.getFirstValue("logid", true));
        if ( null == logid ) {
            logid = String.valueOf(RANDOM_GENERATOR.nextLong());
        }
        MDC.put("logid", logid);
//        retrievalResponse.getResponseHeader().version = configuration.getString("server.version");
        retrievalResponse.setEtag(((Series) this.getRequestAttributes().get("org.restlet.http.headers")).getValues("If-None-Match"));
        String ifModifiedSince = ((Series) this.getRequestAttributes().get("org.restlet.http.headers")).getValues("If-Modified-Since");
        if ( null != ifModifiedSince ) {
            try {
                retrievalResponse.setLastModifyDate(new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.US).parse(ifModifiedSince));
            } catch (Exception ee) {
            }
        }
    }

    /**
     * should set this.retrievalResponse.getResponseHeader().status=Status.CLIENT_ERROR_FORBIDDEN.getCode();
     * when check failed
     */
    protected void check() {
    }

    protected void badRequest() {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    }

    protected void errorRequest() {
        setStatus(Status.SERVER_ERROR_INTERNAL);
    }

    protected String doCheck() {
        try {
            checkBasicParams();
            check();
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
        return null;
    }

    protected void checkPostBodyParams(Map<String, Object> body) throws Exception {
        if (!configuration.getBoolean("check.post.body.basic.params", true)) {
            return;
        }
        Set<String> basicParams = new HashSet<String>(POST_BODY_XML_BASIC_PARAMS);
        basicParams.removeAll(body.keySet());
        if (!basicParams.isEmpty()) {
            throw new IllegalArgumentException("body miss some of the params:" + basicParams);
        }
    }

    protected void checkBasicParams() {
//        if (!configuration.getBoolean("check.url.basic.params", false)) {
//            return;
//        }
//        Form form = getQuery();
//        Set<String> basicParams = new HashSet<String>(URL_BASIC_PARAMS);
//        basicParams.removeAll(form.getNames());
//        if (!basicParams.isEmpty()) {
//            throw new IllegalArgumentException("miss some of the params:" + basicParams);
//        }
    }

    @Get
    public Representation getResource() {
        Representation representation = processGetRequest(this.getQuery());
        Status status = getStatus();
        if (status != null && status.getCode() < 400) {
            try {
                Form form = getQuery();
                int maxAge = Integer.parseInt(form.getFirstValue("maxage", true, "60"));
                getResponse().getCacheDirectives().add(CacheDirective.maxAge(maxAge));
            } catch (Exception e) {
                getResponse().getCacheDirectives().add(CacheDirective.maxAge(60));
            }
        }
        return representation;
    }

    @Post
    public Representation postResource(Representation representation) {
        return processPostRequest(this.getQuery(), representation);
    }

    public Representation processGetRequest(Form form) {
        this.getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN);
        return null;
    }

    public Representation processPostRequest(Form form, Representation representation){
        this.getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN);
        return null;
    }

    public String generateMD5(String body) {
        int len = body.length();
        if (len > 50) body = body.substring(0, 50);
        String str = body + dx_key;
        return String.valueOf(Codec.hexMD5(str));
    }

    public Representation checkPostBody(String body) {
//        String checkStr = getQuery().getFirstValue("checkStr");
//        String myCheckStr = generateMD5(body);
//        if (StringUtils.isEmpty(checkStr)) {
//            retrievalResponse.getResponseHeader().status = Status.CLIENT_ERROR_BAD_REQUEST.getCode();
//            retrievalResponse.getResponseHeader().msg = "checkStr mustn't be null.";
//            badRequest();
//            return retrievalResponse.buildJsonResponse(pretty);
//        }
//        if (!myCheckStr.equals(checkStr)) {
//            if (LOGGER.isDebugEnabled()) {
//                LOGGER.debug("checkStr:{}, calcMd5:{}, body:{}", new Object[]{checkStr, myCheckStr, body});
//            }
//            badRequest();
//            retrievalResponse.getResponseHeader().status = Status.CLIENT_ERROR_BAD_REQUEST.getCode();
//            retrievalResponse.getResponseHeader().msg = "check post body : fail.";
//            return retrievalResponse.buildJsonResponse(pretty);
//        }
        return null;
    }

    public String getClientIP(Request request) {
        ClientInfo clientInfo = request.getClientInfo();
        List<String> forwardedAddresses = clientInfo.getForwardedAddresses();
        return forwardedAddresses.isEmpty() ? clientInfo.getAddress() : forwardedAddresses.get(forwardedAddresses.size() - 1);
    }
}
