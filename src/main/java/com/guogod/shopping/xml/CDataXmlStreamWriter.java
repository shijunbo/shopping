package com.guogod.shopping.xml;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Created by shijunbo on 2015/7/19.
 */
public class CDataXmlStreamWriter implements XMLStreamWriter {
    private XMLStreamWriter w;

    public CDataXmlStreamWriter(XMLStreamWriter w) {
        this.w = w;
    }
    public void close() throws XMLStreamException {
        w.close();
    }
    public void flush() throws XMLStreamException {
        w.flush();
    }
    public NamespaceContext getNamespaceContext() {
        return w.getNamespaceContext();
    }
    public String getPrefix(String uri) throws XMLStreamException {
        return w.getPrefix(uri);
    }
    public Object getProperty(String name) throws IllegalArgumentException {
        return w.getProperty(name);
    }
    public void setDefaultNamespace(String uri) throws XMLStreamException {
        w.setDefaultNamespace(uri);
    }
    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        w.setNamespaceContext(context);
    }
    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        w.setPrefix(prefix, uri);
    }
    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        w.writeAttribute(prefix, namespaceURI, localName, value);
    }
    public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
        w.writeAttribute(namespaceURI, localName, value);
    }
    public void writeAttribute(String localName, String value) throws XMLStreamException {
        w.writeAttribute(localName, value);
    }
    public void writeCData(String data) throws XMLStreamException {
        w.writeCData(data);
    }
    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        w.writeCharacters(text, start, len);
    }
    // All this code just to override this method
    public void writeCharacters(String text) throws XMLStreamException {
        w.writeCData(text);
    }
    public void writeComment(String data) throws XMLStreamException {
        w.writeComment(data);
    }
    public void writeDTD(String dtd) throws XMLStreamException {
        w.writeDTD(dtd);
    }
    public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
        w.writeDefaultNamespace(namespaceURI);
    }
    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        w.writeEmptyElement(prefix, localName, namespaceURI);
    }
    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        w.writeEmptyElement(namespaceURI, localName);
    }
    public void writeEmptyElement(String localName) throws XMLStreamException {
        w.writeEmptyElement(localName);
    }
    public void writeEndDocument() throws XMLStreamException {
        w.writeEndDocument();
    }
    public void writeEndElement() throws XMLStreamException {
        w.writeEndElement();
    }
    public void writeEntityRef(String name) throws XMLStreamException {
        w.writeEntityRef(name);
    }
    public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
        w.writeNamespace(prefix, namespaceURI);
    }
    public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        w.writeProcessingInstruction(target, data);
    }
    public void writeProcessingInstruction(String target) throws XMLStreamException {
        w.writeProcessingInstruction(target);
    }
    public void writeStartDocument() throws XMLStreamException {
        w.writeStartDocument();
    }
    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        w.writeStartDocument(encoding, version);
    }
    public void writeStartDocument(String version) throws XMLStreamException {
        w.writeStartDocument(version);
    }
    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        w.writeStartElement(prefix, localName, namespaceURI);
    }
    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        w.writeStartElement(namespaceURI, localName);
    }
    public void writeStartElement(String localName) throws XMLStreamException {
        w.writeStartElement(localName);
    }
}
