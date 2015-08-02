package com.guogod.shopping.XmlorJson; /**
 * Created by shijunbo on 2015/7/16.
 */
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

public class JSONorXMLtoMap {

//    public static final String xml = "<A><B>b</B><C>c</C></A>";
    public static final String xml = "<xml><ToUserName><![CDATA[toUser]]></ToUserName><FromUserName><![CDATA[fromUser]]></FromUserName><CreateTime>1348831860</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[this is a test]]></Content><MsgId>1234567890123456</MsgId>";
    public static final String json = "{\"B\":\"b\",\"C\":\"c\"}";

    @JacksonXmlRootElement(localName = "xml")
    public static class POJO{
        private Map<String,String> map = new TreeMap<String,String>();

        @JsonAnyGetter
        public Map<String, String> get() {
            return map;
        }

        @JsonAnySetter
        public void set(String name, String value) {
            map.put(name, value);
        }

    }

    @Test
    public final void test() throws JsonProcessingException, IOException {
        ObjectMapper xmlmapper = new XmlMapper();
        POJO p2 = xmlmapper.readValue(xml,POJO.class);
        for ( String key : p2.get().keySet() ){
            System.out.print(key);
        }
        assertEquals(xmlmapper.writeValueAsString(p2),xml);

        ObjectMapper jsonmapper = new ObjectMapper();
        POJO p3 = jsonmapper.readValue(json, POJO.class);
        assertEquals(jsonmapper.writeValueAsString(p3),json);
    }

}