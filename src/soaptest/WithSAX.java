/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soaptest;

import java.io.ByteArrayInputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author jagadeesh.t
 */
public class WithSAX {

    public static String resString = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" >\n"
            + "   <S:Body xmlns:ns2=\"http://ws.imi.com/\" >\n"
            + "      <ns2:getArrayObjResponse >\n"
            + "         <getArrayObj>\n"
            + "            <age>10.0</age>\n"
            + "            <name>name 1</name>\n"
            + "         </getArrayObj>\n"
            + "         <getArrayObj>\n"
            + "            <age>11.0</age>\n"
            + "            <name>name 2</name>\n"
            + "         </getArrayObj>\n"
            + "         <getArrayObj>\n"
            + "            <age>12.0</age>\n"
            + "            <name>name 3</name>\n"
            + "         </getArrayObj>\n"
            + "      </ns2:getArrayObjResponse>\n"
            + "   </S:Body>\n"
            + "</S:Envelope>";

    static String currEle = null;
    static Deque<String> stack = new ArrayDeque<>();

    public static class SAXHandler extends DefaultHandler {

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

            currEle = qName;
            stack.push(qName);
            System.out.println("start element \t" + qName + "\t uri=" + uri);
            System.out.println("local name "+localName);
            for(int i=0;i<attributes.getLength();i++){
                String qName1 = attributes.getQName(i);
                System.out.println("att \t"+qName1);
            }
            
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            System.out.println("data in " + currEle + "\t" + new String(ch, start, length));
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            System.out.println("end element \t" + qName);
        }

    }

    public static void main(String[] args) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        SAXHandler handler = new SAXHandler();
        saxParser.parse(new ByteArrayInputStream(resString.getBytes()), handler);
    }

}
