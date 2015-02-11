/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soaptest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.predic8.schema.ComplexType;
import com.predic8.schema.Element;
import com.predic8.schema.Schema;
import com.predic8.schema.Sequence;
import com.predic8.schema.TypeDefinition;
import com.predic8.soamodel.Consts;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author jagadeesh.t
 */
public class ParseSoapWithSchema {

    public String resString = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
            + "   <S:Body>\n"
            + "      <ns2:getPersonalObjResponse xmlns:ns2=\"http://ws.imi.com/\">\n"
            + "         <getPersonalObj>\n"
            + "            <person>\n"
            + "               <addrs>\n"
            + "                  <PIN>def</PIN>\n"
            + "                  <street>abc</street>\n"
            + "               </addrs>\n"
            + "               <addrs>\n"
            + "                  <PIN>2</PIN>\n"
            + "                  <street>1</street>\n"
            + "               </addrs>\n"
            + "               <age>12.3</age>\n"
            + "               <name>jag</name>\n"
            + "            </person>\n"
            + "            <personalDetails>\n"
            + "               <address>\n"
            + "                  <PIN>502325</PIN>\n"
            + "                  <street>lax</street>\n"
            + "               </address>\n"
            + "               <wage>12.6</wage>\n"
            + "               <wname>wwwname</wname>\n"
            + "            </personalDetails>\n"
            + "            <personalType>sampletype</personalType>\n"
            + "         </getPersonalObj>\n"
            + "      </ns2:getPersonalObjResponse>\n"
            + "   </S:Body>\n"
            + "</S:Envelope>";

//    static String wsdlURL = "http://www.thomas-bayer.com/axis2/services/BLZService?wsdl";
//    static String wsdlURL = "http://wsf.cdyne.com/WeatherWS/Weather.asmx?WSDL";
    String wsdlURL = "http://localhost:3535/WSTest/sample?wsdl";

    JsonObject jsonSoap = new JsonObject();
    SOAPBody soapBody = null;
    Definitions defs = null;
    Deque<Schema> schemaStack = new ArrayDeque<>();
    Schema schema = null;

    public ParseSoapWithSchema() {

        try {
            MessageFactory factory = MessageFactory.newInstance();
            SOAPMessage message1 = factory.createMessage(new MimeHeaders(), new ByteArrayInputStream(resString.getBytes(Charset.forName("UTF-8"))));
            soapBody = message1.getSOAPBody();

            WSDLParser parser = new WSDLParser();

            defs = parser.parse(wsdlURL);
        } catch (Exception ex) {
            Logger.getLogger(ParseSoapWithSchema.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Object getChildNodes(Node node) {
        NodeList childNodes = node.getChildNodes();
        StringBuilder content = new StringBuilder();
        boolean isContentNode = true;
        List<Node> list = new ArrayList<>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node n = childNodes.item(i);
            short nodeType = n.getNodeType();
            if (nodeType == Node.TEXT_NODE) {

                content.append(n.getTextContent());
            } else if (nodeType == Node.ELEMENT_NODE) {
                isContentNode = false;
                list.add(n);
            }
        }

        return isContentNode ? content.toString() : list;
    }

    public String getNodeContent(Node node) {
        String content = null;
        try {
            content = (String) this.getChildNodes(node);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return content;
    }

    public List<Node> listNodes(Node node) {
        List<Node> content = null;
        try {
            content = (List<Node>) this.getChildNodes(node);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return content;
    }

    private void traverse(ComplexType ct, Node node, JsonElement jsonEle) {

        JsonObject ctJson = new JsonObject();
        String name = ct.getName();
        System.out.println(ct.getName());

        if (ct.getModel() instanceof Sequence) {
            Sequence seq = (Sequence) ct.getModel();

            for (Element em : (List<Element>) seq.getElements()) {
                JsonElement finalContent = null;
                List<Node> nodes = this.listNodes(node);
                if (em.getEmbeddedType() != null) {
                    dump(em.getEmbeddedType());

                } else {

                    if (em.getType().getNamespaceURI().equals(Consts.SCHEMA_NS)) {
                        String eleName = em.getName();
                        String maxOccurs = em.getMaxOccurs();

                        if (maxOccurs.equals("unbounded")) {
                            JsonArray primArray = new JsonArray();

                            for (Node eNode : nodes) {
                                String nodeName = eNode.getNodeName();
                                String[] split = nodeName.split(":");
                                if (split.length > 1) {
                                    nodeName = split[1];
                                }
                                if (nodeName.equals(eleName)) {
                                    String nodeContent = this.getNodeContent(eNode);
                                    if (nodeContent != null) {
                                        JsonPrimitive jPrimitive = new JsonPrimitive((String) nodeContent);
                                        primArray.add(jPrimitive);
                                    }
                                }
                            }
                            finalContent = primArray;
                        } else {

                            for (Node eNode : nodes) {
                                String nodeName = eNode.getNodeName();
                                String[] split = nodeName.split(":");
                                if (split.length > 1) {
                                    nodeName = split[1];
                                }
                                if (nodeName.equals(eleName)) {
                                    String nodeContent = this.getNodeContent(eNode);
                                    if (nodeContent != null) {
                                        JsonPrimitive jPrimitive = new JsonPrimitive((String) nodeContent);
                                        finalContent = jPrimitive;
                                        break;
                                    }
                                }
                            }

                        }
                        jsonEle.getAsJsonObject().add(eleName, finalContent);
                        System.out.println(", type= 'xsd:" + em.getType().getLocalPart() + "'");

                    } else {
                        String eleName = em.getName();
                        String maxOccurs = em.getMaxOccurs();
                        String eleLocalPart = em.getType().getLocalPart();

                        String ns = em.getNamespaceUri();
                        ComplexType compType = null;

                        if (ns != null) {
                            schema = defs.getSchema(ns);
                            if (schema != null) {
//                                if (!schemaStack.isEmpty()) {
//                                    schemaStack.pop();
//                                }
//                                schemaStack.push(schema);
                            }
                        } else {
//                            schema = schemaStack.peek();
                        }

                        if (schema != null) {
                            compType = schema.getComplexType(eleLocalPart);
                        }
                        if (compType != null) {
                            if (maxOccurs.equals("unbounded")) {
                                JsonArray jsonCompArray = new JsonArray();

                                for (Node eNode : nodes) {
                                    String nodeName = eNode.getNodeName();
                                    String[] split = nodeName.split(":");
                                    if (split.length > 1) {
                                        nodeName = split[1];
                                    }
                                    if (nodeName.equals(eleName)) {
                                        JsonObject jsonComplexObj = new JsonObject();
                                        traverse(compType, eNode, jsonComplexObj);
                                        jsonCompArray.add(jsonComplexObj);
                                    }
                                }
                                finalContent = jsonCompArray;
                            } else {
                                JsonObject jsonComplexObj = new JsonObject();
                                for (Node eNode : nodes) {
                                    String nodeName = eNode.getNodeName();
                                    String[] split = nodeName.split(":");
                                    if (split.length > 1) {
                                        nodeName = split[1];
                                    }
                                    if (nodeName.equals(eleName)) {
                                        traverse(compType, eNode, jsonComplexObj);
                                    }
                                }
                                finalContent = jsonComplexObj;
                            }
                            jsonEle.getAsJsonObject().add(eleName, finalContent);
                        }
                        System.out.println(", type= 'tns:" + em.getType().getLocalPart() + "'");
                    }
                }//else part for embedded type

            }
        }

    }

    void dump(TypeDefinition td) {
        System.out.println(td.getName());
    }

    public void parseSoapResponse() throws Exception {

        JsonObject bodyJson = new JsonObject();

        NodeList bodyChildren = soapBody.getChildNodes();
        JsonObject jsonObj = new JsonObject();
        JsonObject bodyEleJson = new JsonObject();
        if (bodyChildren != null && bodyChildren.getLength() > 0) {

            for (int i = 0; i < bodyChildren.getLength(); i++) {
                Node bodyChild = bodyChildren.item(i);
                short bodyChildType = bodyChild.getNodeType();
                if (bodyChildType == Node.ELEMENT_NODE) {

                    String bodyChildName = bodyChild.getNodeName();
                    String bodyChildNS = bodyChild.getNamespaceURI();
                    if (bodyChildName.contains(":")) {
                        bodyChildName = bodyChildName.split(":")[1];
                    }
                    if (bodyChildNS != null) {
                        schema = defs.getSchema(bodyChildNS);
                    }

                    if (schema != null) {
                        ComplexType ct = schema.getComplexType(bodyChildName);

                        JsonObject soapBodyEleJson = new JsonObject();
                        traverse(ct, bodyChild, soapBodyEleJson);

                        jsonObj.add(bodyChildName, soapBodyEleJson);
                    }

                }
            }
            bodyEleJson.add("body", jsonObj);
            JsonObject envelopeJson = new JsonObject();
            envelopeJson.add("envelope", bodyEleJson);
            System.out.println(envelopeJson);
        }

    }

    public static void main(String[] args) throws Exception {
        ParseSoapWithSchema p = new ParseSoapWithSchema();
        p.parseSoapResponse();
    }

}
