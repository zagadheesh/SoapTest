/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soaptest;

import com.predic8.schema.Import;
import com.predic8.schema.Schema;
import com.predic8.wsdl.Binding;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Input;
import com.predic8.wsdl.Message;
import com.predic8.wsdl.Operation;
import com.predic8.wsdl.Output;
import com.predic8.wsdl.Port;
import com.predic8.wsdl.PortType;
import com.predic8.wsdl.Service;
import com.predic8.wsdl.WSDLParser;
import com.predic8.wstool.creator.RequestCreator;
import com.predic8.wstool.creator.SOARequestCreator;
import groovy.xml.MarkupBuilder;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static soaptest.ParseResponse.wsdlURL;

/**
 *
 * @author jagadeesh.t
 */
public class ReadWSDL {

    public static String wsdlXML = "";
    public static File f = new File("C:\\Users\\jagadeesh.t\\Documents\\NetBeansProjects\\SoapTest\\src\\wsdl1.xml");
    public static WSDLParser parser = new WSDLParser();
    public static Definitions defs = null;
    public static String wsdlURL = "http://localhost:3535/WSTest/sample?wsdl";
//    public static String wsdlURL = "http://www.reportingsales.com/DeFactoSF1.asmx?WSDL";

    public static void loadWSDLFromFile() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = null;
            while ((line = br.readLine()) != null) {
                wsdlXML += line;
            }
            defs = parser.parse(wsdlXML);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReadWSDL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReadWSDL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void loadWSDLFromURL() {
        defs = parser.parse(wsdlURL);
    }

    public static void listSchemas() {

        List<Schema> schemasList = defs.getSchemas();
        int i = 1;
        for (Schema schema : schemasList) {

            System.out.println("schema " + i + schema);
            i++;
            if (schema != null) {
                String targetNamespace = schema.getTargetNamespace();
                System.out.println("target ns :: " + targetNamespace);

                if (targetNamespace != null) {
                    try {
                        String namespaceUri = schema.getNamespaceUri();
                        System.out.println("namespaceuri " + i + " :: " + namespaceUri);
                    } catch (Exception e) {
                        System.out.println("exxx");
                    }
                }
            }
        }

        Schema sc = defs.getSchema("http://jaxb.dev.java.net/array");
        System.out.println("schema :::: " + sc);

    }

    public static void testHeirarchy() {
        int tabs = 0;
        List<Service> services = defs.getServices();
        for (Service service : services) {
            out("service name :: " + service.getName(), tabs++);
            List<Port> ports = service.getPorts();
            for (Port port : ports) {
                out("port name :: " + port.getName(), tabs);
                Binding binding = port.getBinding();
                out("binding name :: " + binding.getName(), tabs);
                PortType portType = binding.getPortType();
                out("portType name :: " + portType.getName(), tabs);
                List<Operation> operations = portType.getOperations();
                out("operations size :: " + operations.size(), tabs++);
                for (Operation operation : operations) {
                    out("operation name :: " + operation.getName(), tabs++);
                    Input input = operation.getInput();
//                    out("input name :: "+input.getName(), tabs);
                    Message inMsg = input.getMessage();
                    out("input msg name :: " + inMsg.getName(), tabs);
                    Output output = operation.getOutput();
                    Message outMsg = output.getMessage();
                    out("output msg name :: " + outMsg.getName(), tabs);
                    tabs--;
                }
                tabs--;
            }

        }
    }

    public static void getInputJsons() {
        int tabs = 0;
        List<Service> services = defs.getServices();
        for (Service service : services) {
            List<Port> ports = service.getPorts();
            for (Port port : ports) {
                Binding binding = port.getBinding();
                PortType portType = binding.getPortType();
                List<Operation> operations = portType.getOperations();
                for (Operation operation : operations) {
                    Input input = operation.getInput();
                    Message inMsg = input.getMessage();
//                    Output output = operation.getOutput();
//                    Message outMsg = output.getMessage();
                }
            }

        }
    }

    public static String createRequestEnvelope() {
        StringWriter writer = new StringWriter();
        SOARequestCreator reqCreator = new SOARequestCreator(defs, new RequestCreator(), new MarkupBuilder(writer));
        reqCreator.createRequest("HelloWorld", "sayHello", "HelloWorldImplPortBinding");

        String soapRequest = writer.toString();

        return soapRequest;
    }

    public static void out(String s, int tabs) {
        System.out.println(getTabs(tabs) + " " + s);
    }

    public static String getTabs(int i) {
        String s = "";
        while (i > 0) {
            s += "\t";
            i--;
        }
        return s;

    }

    public static void main(String[] args) {
        loadWSDLFromURL();
//        listSchemas();
//        testHeirarchy();
        out(createRequestEnvelope(), 0);
    }

}
