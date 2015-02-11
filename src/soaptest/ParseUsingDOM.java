/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soaptest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author jagadeesh.t
 */
public class ParseUsingDOM {

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

//    static String wsdlURL = "http://www.thomas-bayer.com/axis2/services/BLZService?wsdl";
//    static String wsdlURL = "http://wsf.cdyne.com/WeatherWS/Weather.asmx?WSDL";
    static String wsdlURL = "http://localhost:3535/WSTest/sample?wsdl";

    public static void main(String[] args) throws Exception {
        parse();
    }

    public static void parse() throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document doc = docBuilder.parse(new ByteArrayInputStream(resString.getBytes()));

//        NodeList bodyEle = doc.getElementsByTagName("S:Body");
//        System.out.println("bodyEle \t" + bodyEle.getLength());
        Element documentElement = doc.getDocumentElement();
        NodeList childNodes = documentElement.getChildNodes();
        System.out.println(childNodes.getLength());

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            System.out.println(item.getNodeName());

        }

    }
}
