package com.guogod.shopping.utils;

import com.fasterxml.jackson.core.JsonParseException;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.xml.XMLSerializer;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.MissingNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
/**
 * Created by shijunbo on 2015/7/16.
 */
public class XMLUtils {
    private static final Logger logger = LoggerFactory.getLogger(XMLUtils.class);
    public static final ObjectMapper xmlmapper = new XmlMapper();

    @JacksonXmlRootElement(localName = "xml")
    public static class POJO{
        private Map<String,Object> map = new TreeMap<String,Object>();
        @JsonAnyGetter
        public Map<String, Object> get() {
            return map;
        }
        @JsonAnySetter
        public void set(String name, Object value) {
            map.put(name, value);
        }
    }

    public static JsonNode toObject(String xml){
        JsonNode jsonNode = MissingNode.getInstance();

        try {
            jsonNode = xmlmapper.readValue(xml, JsonNode.class);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return jsonNode;
    }

    public static Map<String, Object> toMap(String xml) throws JsonParseException,IOException{
        POJO p2 = xmlmapper.readValue(xml, POJO.class);
        return p2.get();
    }

    public static Map<String, Object> parseXml(String xml) {
        xml = xml.replaceAll(">\\s+<", "><");
        JSON json = (new XMLSerializer()).read(xml);
        Map<String, Object> map = null;
        if ( json.isArray() ) {
            JSONArray jsonarray = (JSONArray) json;
            map = (Map<String, Object>) jsonarray.get(0);
        } else {
            map = (Map<String, Object>) json;
        }
        return map;
    }

}
