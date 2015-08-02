package com.guogod.shopping.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;

/**
 * Created by shijunbo on 2015/7/15.
 */
public class DXXMLResponseHandler implements ResponseHandler<JsonNode> {
    public static final XmlMapper mapper = new XmlMapper();

    static {
//        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
//        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public JsonNode handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() >= 300) {
            EntityUtils.consume(entity);
            throw new HttpResponseException(statusLine.getStatusCode(),
                    statusLine.getReasonPhrase());
        }

        JsonNode contentNode = null;
        int errcode = 400;
        try {
            if (null != entity) {
                String respContent = EntityUtils.toString(entity);
                if (null != respContent) {
                    JsonNode root = mapper.readTree(respContent);
                    if (null != root && !root.isNull()) {
                        JsonNode statusObj = root.path("responseHeader");
                        if (!statusObj.isNull()) {
                            errcode = statusObj.get("errcode").asInt();
                            if (errcode >= 300) {
                                throw new HttpResponseException(errcode, String.format("response: %s", respContent));
                            }
                            contentNode = root.get("response");
                            if (contentNode.isNull() || contentNode.isMissingNode() || contentNode.toString().equals("{}")) {
                                return null;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new HttpResponseException(errcode, e.getMessage());
        } finally {
            EntityUtils.consume(entity);
        }
        return contentNode;
    }
}
