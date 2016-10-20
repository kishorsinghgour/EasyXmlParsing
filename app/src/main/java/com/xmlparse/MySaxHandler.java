package com.xmlparse;

import com.xmlparse.handler.HtmlParseHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by gour.kishor on 17-Oct-16.
 */
public class MySaxHandler extends DefaultHandler {
    private StringBuilder content = new StringBuilder();
    private HtmlParseHandler parseHandler;

    public MySaxHandler(HtmlParseHandler parseHandler) {
        this.parseHandler = parseHandler;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        content.setLength(0);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        content.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (parseHandler != null) {
            parseHandler.onHtmlParser(content.toString());
        }
    }
}
