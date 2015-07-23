package com.guogod.shopping.rest.model;

/**
 * Created by shijunbo on 2015/7/15.
 */
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.aalto.stax.InputFactoryImpl;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.guogod.shopping.xml.CDataXmlOutputFactoryImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.data.Tag;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

public class RetrievalResponse {
    private static final XmlMapper RESPONSE_OBJECT_XMLMAPPTER = new XmlMapper(new XmlFactory(new InputFactoryImpl(),
            new CDataXmlOutputFactoryImpl()));
    private static final ObjectMapper RESPONSE_OBJECT_MAPPTER = new ObjectMapper();
    private static final ObjectWriter RESPONSE_OBJECT_WRITER = RESPONSE_OBJECT_MAPPTER.writerWithDefaultPrettyPrinter();

    static {
        RESPONSE_OBJECT_MAPPTER.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        RESPONSE_OBJECT_MAPPTER.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    static {
    }

    private Map<String, Object> response = new HashMap<String, Object>();
    private Date lastModifyDate;
    private String etag;

    public void setEtag(String etag){
        this.etag = etag;
    }

    public Date getLastModifyDate() {
        if (null == lastModifyDate) return null;
        return (Date) lastModifyDate.clone();
    }

    public void setLastModifyDate(Date lastModifyDate) {
        if (null != lastModifyDate) {
            this.lastModifyDate = new Date(lastModifyDate.getTime());
        } else {
            this.lastModifyDate = null;
        }
    }

    public Map<String, Object> getResponse() {
        return response;
    }

    public void setResponse(Map<String, Object> response) {
        this.response = response;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public Representation buildStringResponse(String _response) {
        if ( null != _response ) {
            Representation representation = new StringRepresentation(_response);
            String etag = DigestUtils.md5Hex(_response);
            if (null == this.etag || !this.etag.contains(etag)) {
                lastModifyDate = new Date();
            }

            if (null == this.lastModifyDate) {
                lastModifyDate = new Date();
            }
            representation.setTag(new Tag(etag));
            representation.setModificationDate(lastModifyDate);
            return representation;
        }else{
            return new StringRepresentation("server error");
        }
    }

    private String flatXml(String xml){
        xml = xml.substring(xml.indexOf("<xml"), xml.lastIndexOf("xml>")+4);
        int len  = xml.length();
        StringBuffer  sb = new StringBuffer(len+1);
        sb.append("<xml");
        int index = 4;
        while( index != -1 && index <= len - 8 ){
            Character begin = xml.charAt(index);
            Character next = xml.charAt(index + 1);
            if ( begin == '<' && Character.isLetter(next) ){
                sb.append(begin);
                sb.append(Character.toUpperCase(next));
                index += 2;
            }else if ( begin == '/' && Character.isLetter(next) ){
                sb.append(begin);
                sb.append(Character.toUpperCase(next));
                index += 2;
            }else{
                sb.append(begin);
                index += 1;
                if ( next != '<' && next != '/'){
                    sb.append(next);
                    index += 1;
                }else{
                    continue;
                }
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }

    public Representation buildXMLResponse(Object _response) {
        if ( null != _response ) {
            try {
                Map<String, Object> result = new HashMap<String, Object>();
                String xml = null;
                if (null != _response) {
                    result.put("xml", _response);
                    xml = RESPONSE_OBJECT_XMLMAPPTER.writeValueAsString(result);
                    xml = flatXml(xml);
                }

                Representation representation = new StringRepresentation(xml, MediaType.APPLICATION_XML);
                String etag = DigestUtils.md5Hex(xml);
                if (null == this.etag || !this.etag.contains(etag)) {
                    lastModifyDate = new Date();
                }

                if (null == this.lastModifyDate) {
                    lastModifyDate = new Date();
                }
                representation.setTag(new Tag(etag));
                representation.setModificationDate(lastModifyDate);
                return representation;
            } catch (IOException e) {
                return new StringRepresentation(e.toString(), MediaType.TEXT_HTML);
            }
        }else{
            return new StringRepresentation("server error");
        }
    }

    public Representation buildJsonResponse(Object _response) {
        try {
            Map<String, Object> result = new HashMap<String, Object>();
            String data = null;
            if (_response != null){
                result.put("response", _response);
                data = RESPONSE_OBJECT_MAPPTER.writeValueAsString(_response);
            }else{
                result.put("response", this.response);
                data = RESPONSE_OBJECT_MAPPTER.writeValueAsString(this.response);
            }

            String json = RESPONSE_OBJECT_WRITER.withDefaultPrettyPrinter().writeValueAsString(result);
            Representation representation = new StringRepresentation(json, MediaType.APPLICATION_JSON);
            String etag = DigestUtils.md5Hex(data);
            if ( null == this.etag  ||  !this.etag.contains(etag) ){
                lastModifyDate = new Date();
            }

            if ( null == this.lastModifyDate ){
                lastModifyDate = new Date();
            }
            representation.setCharacterSet(CharacterSet.ALL.UTF_8);
            representation.setTag(new Tag(etag));
            representation.setModificationDate(lastModifyDate);
            return representation;
        } catch (IOException e) {
            return new StringRepresentation(e.toString(), MediaType.TEXT_HTML);
        }
    }

}
