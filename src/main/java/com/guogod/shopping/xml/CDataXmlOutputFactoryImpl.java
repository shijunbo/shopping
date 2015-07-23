package com.guogod.shopping.xml;

/**
 * Created by shijunbo on 2015/7/19.
 */
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.stream.*;
import javax.xml.transform.Result;
import com.fasterxml.aalto.stax.OutputFactoryImpl;

public class CDataXmlOutputFactoryImpl extends XMLOutputFactory {
    OutputFactoryImpl f = new OutputFactoryImpl();
    public XMLEventWriter createXMLEventWriter(OutputStream out) throws XMLStreamException {
        return f.createXMLEventWriter(out);
    }
    public XMLEventWriter createXMLEventWriter(OutputStream out, String enc) throws XMLStreamException {
        return f.createXMLEventWriter(out, enc);
    }
    public XMLEventWriter createXMLEventWriter(Result result) throws XMLStreamException {
        return f.createXMLEventWriter(result);
    }
    public XMLEventWriter createXMLEventWriter(Writer w) throws XMLStreamException {
        return f.createXMLEventWriter(w);
    }
    public XMLStreamWriter createXMLStreamWriter(OutputStream out) throws XMLStreamException {
        return new CDataXmlStreamWriter(f.createXMLStreamWriter(out));
    }
    public XMLStreamWriter createXMLStreamWriter(OutputStream out, String enc) throws XMLStreamException {
        return new CDataXmlStreamWriter(f.createXMLStreamWriter(out, enc));
    }
    public XMLStreamWriter createXMLStreamWriter(Result result) throws XMLStreamException {
        return new CDataXmlStreamWriter(f.createXMLStreamWriter(result));
    }
    public XMLStreamWriter createXMLStreamWriter(Writer w) throws XMLStreamException {
        return new CDataXmlStreamWriter(f.createXMLStreamWriter(w));
    }
    public Object getProperty(String name) {
        return f.getProperty(name);
    }
    public boolean isPropertySupported(String name) {
        return f.isPropertySupported(name);
    }
    public void setProperty(String name, Object value) {
        f.setProperty(name, value);
    }

}
