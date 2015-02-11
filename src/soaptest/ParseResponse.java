/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soaptest;

import com.google.gson.JsonObject;
import com.predic8.schema.Appinfo;
import com.predic8.schema.Attribute;
import com.predic8.schema.ComplexContent;
import com.predic8.schema.ComplexType;
import com.predic8.schema.Derivation;
import com.predic8.schema.Documentation;
import com.predic8.schema.Element;
import com.predic8.schema.Import;
import com.predic8.schema.Include;
import com.predic8.schema.ModelGroup;
import com.predic8.schema.Schema;
import com.predic8.schema.SchemaComponent;
import com.predic8.schema.Sequence;
import com.predic8.schema.SimpleType;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;
import groovy.xml.QName;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class ParseResponse {

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
//        parse();
//        listAllElements();
//        parse1();
        parseSoapResponse();
//        testRPC();
//        testDOC();
    }

    public static void testRPC() {
        WSDLParser parser = new WSDLParser();

        Definitions defs = parser.parse(wsdlURL);
        Schema schema = defs.getSchema("http://ws.imi.com/");
//        ComplexType complexType = schema.getComplexType("getArrayObjResponse");
//        System.out.println(complexType);

//        schema.getE
    }

    public static void testDOC() {
        WSDLParser parser = new WSDLParser();

        Definitions defs = parser.parse(wsdlURL);
        Schema schema = defs.getSchema("http://ws.imi.com/");
        ComplexType complexType = schema.getComplexType("helloresp");
        System.out.println("type \t" + complexType);
        SimpleType simpleType = schema.getSimpleType("helloresp");
        System.out.println("simple type \t" + simpleType);
    }

    public static Object getChildNodes(Node node) {
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

    public static void parseSoapResponse() throws Exception {

        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message1 = factory.createMessage(new MimeHeaders(), new ByteArrayInputStream(resString.getBytes(Charset.forName("UTF-8"))));
        SOAPBody body1 = message1.getSOAPBody();

        WSDLParser parser = new WSDLParser();

        Definitions defs = parser.parse(wsdlURL);

        NodeList bodyChildren = body1.getChildNodes();
        JsonObject jsonObj = new JsonObject();
        if (bodyChildren != null && bodyChildren.getLength() > 0) {
            for (int i = 0; i < bodyChildren.getLength(); i++) {
                Node bodyChild = bodyChildren.item(i);
                short bodyChildType = bodyChild.getNodeType();
                if (bodyChildType == Node.ELEMENT_NODE) {
                    String bodyChildName = bodyChild.getNodeName();
                    String bodyChildNS = bodyChild.getNamespaceURI();
                    if (bodyChildName.contains(":")) {
                        bodyChildName = bodyChildName.split(":")[1];
//                        QName rootEleQName = new QName(bodyChildName, bodyChildName);
//                        Element rootEle = defs.getElement(rootEleQName);

                        Schema schema = defs.getSchema(bodyChildNS);
                        ComplexType ct = schema.getComplexType(bodyChildName);

                        out(" ComplexType Model: " + ct.getModel().getClass().getSimpleName());
                        if (ct.getModel() instanceof ModelGroup) {
                            out(" Model Particles: ");
                            for (SchemaComponent sc : ((ModelGroup) ct.getModel()).getParticles()) {
                                out(" Particle Kind: " + sc.getClass().getSimpleName());
                                out(" Particle Name: " + sc.getName() + "\n");
                            }
                        }

                        if (ct.getModel() instanceof ComplexContent) {
                            Derivation der = ((ComplexContent) ct.getModel()).getDerivation();
                            out(" ComplexConten Derivation: " + der.getClass().getSimpleName());
                            out(" Derivation Base: " + der.getBase());
                        }

                    }

                }
            }
        }

    }

    public static void listAllElements() {
        WSDLParser parser = new WSDLParser();

        Definitions defs = parser.parse(wsdlURL);
        List<Schema> wsdlSchemas = defs.getSchemas();
        out("schemas size :: " + wsdlSchemas.size());

        for (Schema schema : wsdlSchemas) {
            out("----------schema elements-----------------");
            List<Element> allElements = schema.getAllElements();
            out("elements size :: " + allElements.size());
            out("  TargetNamespace: \t" + schema.getTargetNamespace());
            List<Import> imports = schema.getImports();
            out("imports size :: " + imports.size());
//            parseSchema(schema);
            for (Import imprt : imports) {
                String importNS = imprt.getNamespace();
                out("imported namespace \t" + importNS);
                out("schemalocation \t" + imprt.getSchemaLocation());
            }

            QName qname = new QName("http://ws.imi.com/", "getArrayObjResponse");
            Element element = schema.getElement(qname);
            if (element != null) {
//                QName eleType = element.getType();
//                String maxOccurs = element.getMaxOccurs();
//
//                out("max occurs "+maxOccurs);
//                out("element type " + eleType);
//                out("element found ....");
            }

        }
    }

    public static void parseSchema(Schema schema) {

//        SchemaParser parser = new SchemaParser();
//        Schema schema = parser.parse("samples/xsd/human-resources.xsd");
        out("-------------- Schema Information --------------");
        out(" Schema TargetNamespace: " + schema.getTargetNamespace());
        out(" AttributeFormDefault: " + schema.getAttributeFormDefault());
        out(" ElementFormDefault: " + schema.getElementFormDefault());
        out("");

        if (schema.getImports().size() > 0) {
            out(" Schema Imports: ");
            for (Import imp : schema.getImports()) {
                out(" Import Namespace: " + imp.getNamespace());
                out(" Import Location: " + imp.getSchemaLocation());
            }
            out("");
        }

        if (schema.getIncludes().size() > 0) {
            out(" Schema Includes: ");
            for (Include inc : schema.getIncludes()) {
                out(" Include Location: " + inc.getSchemaLocation());
            }
            out("");
        }

        out(" Schema Elements: ");
        for (Element e : schema.getAllElements()) {
            out(" Element Name: " + e.getName());
            if (e.getType() != null) {
                /*
                 * schema.getType() delivers a TypeDefinition (SimpleType orComplexType)
                 * object.
                 */
                out(" Element Type Name: " + schema.getType(e.getType()).getName());
                out(" Element minoccurs: " + e.getMinOccurs());
                out(" Element maxoccurs: " + e.getMaxOccurs());
                if (e.getAnnotation() != null) {
                    annotationOut(e);
                }
            }
        }
        out("");

        out(" Schema ComplexTypes: ");
        for (ComplexType ct : schema.getComplexTypes()) {
            out(" ComplexType Name: " + ct.getName());
            if (ct.getAnnotation() != null) {
                annotationOut(ct);
            }
            if (ct.getAttributes().size() > 0) {
                out(" ComplexType Attributes: ");
                /*
                 * If available, attributeGroup could be read as same as attribute in
                 * the following.
                 */
                for (Attribute attr : ct.getAttributes()) {
                    out(" Attribute Name: " + attr.getName());
                    out(" Attribute Form: " + attr.getForm());
                    out(" Attribute ID: " + attr.getId());
                    out(" Attribute Use: " + attr.getUse());
                    out(" Attribute FixedValue: " + attr.getFixedValue());
                    out(" Attribute DefaultValue: " + attr.getDefaultValue());
                }
            }
            /*
             * ct.getModel() delivers the child element used in complexType. In case
             * of 'sequence' you can also use the getSequence() method.
             */

            out(" ComplexType Model: " + ct.getModel().getClass().getSimpleName());
            if (ct.getModel() instanceof ModelGroup) {
                out(" Model Particles: ");
                for (SchemaComponent sc : ((ModelGroup) ct.getModel()).getParticles()) {
                    out(" Particle Kind: " + sc.getClass().getSimpleName());
                    out(" Particle Name: " + sc.getName() + "\n");
                }
            }

            if (ct.getModel() instanceof ComplexContent) {
                Derivation der = ((ComplexContent) ct.getModel()).getDerivation();
                out(" ComplexConten Derivation: " + der.getClass().getSimpleName());
                out(" Derivation Base: " + der.getBase());
            }

            if (ct.getAbstractAttr() != null) {
                out(" ComplexType AbstractAttribute: " + ct.getAbstractAttr());
            }
            if (ct.getAnyAttribute() != null) {
                out(" ComplexType AnyAttribute: " + ct.getAnyAttribute());
            }

            out("");
        }

        if (schema.getSimpleTypes().size() > 0) {
            out(" Schema SimpleTypes: ");
            for (SimpleType st : schema.getSimpleTypes()) {
                out(" SimpleType Name: " + st.getName());
                out(" SimpleType Restriction: " + st.getRestriction());
                out(" SimpleType Union: " + st.getUnion());
                out(" SimpleType List: " + st.getList());
            }
        }
    }

    private static void annotationOut(SchemaComponent sc) {
        if (sc.getAnnotation().getAppinfos().size() > 0) {
            System.out
                    .print(" Annotation (appinfos) available with the content: ");
            for (Appinfo appinfo : sc.getAnnotation().getAppinfos()) {
                out(appinfo.getContent());
            }
        } else {
            System.out
                    .print(" Annotation (documentation) available with the content: ");
            for (Documentation doc : sc.getAnnotation().getDocumentations()) {
                out(doc.getContent());
            }
        }
    }

    public static void parse() throws Exception {
        HashMap<String, String> responseMsg = new HashMap();
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message1 = factory.createMessage(new MimeHeaders(), new ByteArrayInputStream(resString.getBytes(Charset.forName("UTF-8"))));
        SOAPBody body1 = message1.getSOAPBody();

        NodeList list1 = body1.getChildNodes();
        if (list1 != null && list1.getLength() > 0) {
            System.out.println(" Nodes Size:::" + list1.getLength());
            for (int i = 0; i < list1.getLength(); i++) {
                Node node = list1.item(i);

                System.out.println("child " + i + " :: " + node.getNodeName());
                short nodeType = node.getNodeType();
//                System.out.println("Node Type :: " + nodeType);

                NodeList innerList = list1.item(i).getChildNodes();
                System.out.println("innerList length :: " + innerList.getLength());
                if (innerList != null && innerList.getLength() > 0) {
                    System.out.println("------------------------------------");
                    for (int j = 0; j < innerList.getLength(); j++) {
                        System.out.println("######################################");
                        Node item = innerList.item(j);
                        String nodeName = item.getNodeName();
                        String textContent = item.getTextContent();
                        short nodeType1 = item.getNodeType();

                        System.out.println("Node Type :: " + nodeType1);
                        System.out.println("Name :: " + nodeName + " $$ content :: " + textContent + " ^^");

                        responseMsg.put(innerList.item(j).getNodeName(), innerList.item(j).getTextContent());
                        System.out.println("######################################");
                    }
                    System.out.println("------------------------------------");
                    System.out.println("map :: " + responseMsg);
                }
            }
        }
        System.out.println("map size :: " + responseMsg.size());
    }

    public static void iterateNode(Node node) {
        NodeList childNodes = node.getChildNodes();
        if (childNodes != null) {
            System.out.println("#### iterating " + node.getNodeName() + " child nodes size " + childNodes.getLength() + " parent " + node.getParentNode().getNodeName());

            for (int i = 0; i < childNodes.getLength(); i++) {
                Node item = childNodes.item(i);
                System.out.println("--------------------");

                System.out.println("node type \t" + item.getNodeType());

                if (item.getNodeType() == Node.TEXT_NODE) {
                    System.out.println("content \t" + item.getTextContent());
                }

//                System.out.println("node value \t**" + item.getNodeValue()+"**");
//                if (item.getNodeValue() != null) {
//                    System.out.println("node value length \t" + item.getNodeValue().length());
//                }
                System.out.println("node name \t" + item.getNodeName());
                System.out.println("node ns \t" + item.getNamespaceURI());

                iterateNode(item);
            }
        }
    }

    public static void iterateNode1(Node node) {

        System.out.println("#### iterating " + node.getNodeName() + " parent " + node.getParentNode().getNodeName());
        Object childNodes = getChildNodes(node);
        if (childNodes instanceof String) {
            System.out.println("content \t " + (String) childNodes);
        } else if (childNodes instanceof List) {
            List<Node> childList = (List<Node>) childNodes;
            System.out.println("child nodes size " + childList.size());
            for (int i = 0; i < childList.size(); i++) {
                Node get = childList.get(i);
                iterateNode1(get);

            }
        }

    }

    public static void parse1() throws Exception {
        HashMap<String, String> responseMsg = new HashMap();
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message1 = factory.createMessage(new MimeHeaders(), new ByteArrayInputStream(resString.getBytes(Charset.forName("UTF-8"))));
        SOAPBody body1 = message1.getSOAPBody();

        iterateNode1(body1);

//        NodeList childNodes = body1.getChildNodes();
//
//        for (int i = 0; i < childNodes.getLength(); i++) {
//            Node item = childNodes.item(i);
//            System.out.println("--------------------");
//            System.out.println("node type \t" + item.getNodeType());
//            System.out.println("node value \t" + item.getNodeValue());
//            System.out.println("node name" + item.getNodeName());
//            System.out.println("node ns \t" + item.getNamespaceURI());
//            System.out.println("--------------------");
//
//        }
//        NodeList list1 = body1.getChildNodes();
//        if (list1 != null && list1.getLength() > 0) {
//            System.out.println(" Nodes Size:::" + list1.getLength());
//            for (int i = 0; i < list1.getLength(); i++) {
//                Node node = list1.item(i);
//
//                System.out.println("child " + i + " :: " + node.getNodeName());
//                short nodeType = node.getNodeType();
////                System.out.println("Node Type :: " + nodeType);
//
//                NodeList innerList = list1.item(i).getChildNodes();
//                System.out.println("innerList length :: " + innerList.getLength());
//                if (innerList != null && innerList.getLength() > 0) {
//                    System.out.println("------------------------------------");
//                    for (int j = 0; j < innerList.getLength(); j++) {
//                        System.out.println("######################################");
//                        Node item = innerList.item(j);
//                        String nodeName = item.getNodeName();
//                        String textContent = item.getTextContent();
//                        short nodeType1 = item.getNodeType();
//
//                        System.out.println("Node Type :: " + nodeType1);
//                        System.out.println("Name :: " + nodeName + " $$ content :: " + textContent + " ^^");
//
//                        responseMsg.put(innerList.item(j).getNodeName(), innerList.item(j).getTextContent());
//                        System.out.println("######################################");
//                    }
//                    System.out.println("------------------------------------");
//                    System.out.println("map :: " + responseMsg);
//                }
//            }
//        }
//        System.out.println("map size :: " + responseMsg.size());
    }

    private static void out(String string) {
        System.out.println(string);
    }

}
