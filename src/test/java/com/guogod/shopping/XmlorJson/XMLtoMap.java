package com.guogod.shopping.XmlorJson; /**
 * Created by shijunbo on 2015/7/16.
 */
import com.fasterxml.jackson.core.JsonProcessingException;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.xml.XMLSerializer;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

public class XMLtoMap {

    @Test
    public final void test() throws JsonProcessingException, IOException {
         Map<String,Object> test = parseXml("<xml><ToUserName><![CDATA[toUser]]></ToUserName><FromUserName><![CDATA[fromUser]]></FromUserName><CreateTime>1348831860</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[this is a test]]></Content><MsgId>1234567890123456</MsgId></xml>");
         for (String key : test.keySet()){
             System.out.println("key" + key + " value" + test.get(key));
         }
    }

    public static Map<String, Object> parseXml(String xml) {
        try {
            xml = xml.replaceAll(">\\s+<", "><");
            JSON json1 = (new XMLSerializer()).read(xml);
            Map<String, Object> map = null;
            if (json1.isArray()) {
                JSONArray jsonarray = (JSONArray) json1;
                map = (Map<String, Object>) jsonarray.get(0);
            } else {
                map = (Map<String, Object>) json1;
            }
            return map;
        } catch (Exception e) {
        }
        return null;
    }
}
