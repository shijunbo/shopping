package com.guogod.shopping.XmlorJson;

import com.fasterxml.aalto.stax.InputFactoryImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.guogod.shopping.model.response.TextMessage;
import com.guogod.shopping.xml.CDataXmlOutputFactoryImpl;
import org.junit.Test;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.XMLFormatter;

/**
 * Created by shijunbo on 2015/7/19.
 */
public class MaptoXML {

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

    @Test
    public final void test() throws Exception {
//          String xml = "<asdfasdf><xml><ToUserName><![CDATA[gh_7219505e0464]]></ToUserName><FromUserName><![CDATA[oLQ8iwuyOwW0xQ4MxmDMNcd4UIF8]]></FromUserName><CreateTime>1437311638</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[555]]></Content><MsgId>6173206479587138066</MsgId></xml><bbbbbbbb>";
//          System.out.println(xml.substring(xml.indexOf("<xml"), xml.lastIndexOf("xml>")+4));
        XmlFactory factory = new XmlFactory(new InputFactoryImpl(),
                new CDataXmlOutputFactoryImpl());
        TextMessage  tm = new TextMessage();
        tm.setContent("aa   aa");
        tm.setCreateTime(1212444);
        tm.setFromUserName("fromuser");
        tm.setToUserName("touser");
        tm.setMsgType("text");
        Map<String, Object> resutl = new HashMap<>();
        resutl.put("xml", tm);
        XmlMapper xml = new XmlMapper(factory);
//        xml.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES,true);
//        xml.configure(MapperFeature.USE_STD_BEAN_NAMING,true);
        xml.enable(MapperFeature.USE_STD_BEAN_NAMING);
        xml.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        System.out.print(flatXml(xml.writeValueAsString(resutl)));

//        XMLOutputFactory f = XMLOutputFactory.newFactory();
//        StringWriter out = new StringWriter();
//        XMLStreamWriter sw = f.createXMLStreamWriter(out);
//
//        // then Jackson components
//        XmlMapper mapper = new XmlMapper();
//        sw.writeStartDocument();
//        sw.writeStartElement("xml");
//
//        // Write whatever content POJOs...
//        Map<String, String> test = new HashMap<String,String>();
//        test.put("test","aaaa");
//        test.put("sencond","bbbb");
//        mapper.writeValue(sw,test);
//        sw.writeEndElement();
//        sw.writeEndDocument();
//        System.out.print(out.toString());
    }
}
