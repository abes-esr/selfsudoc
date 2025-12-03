package fr.abes.derives.xsltransform;

import fr.abes.utils.BufferedRW;
import fr.abes.utils.TestUtils;
import org.custommonkey.xmlunit.*;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TransformWrapperXPathBasedTest extends XMLTestCase {

    Reader brStylesheet = null;
    int outPutMethod = 0;
    Class XpathFactoryClass = null;
    DocumentBuilderFactory factory = null;
    DocumentBuilder builder = null;
    XpathEngine engine = null;
    private TransformWrapper wrapper = null;
    private Reader brXML = null;
    private Writer writer = null;
    private File tmpOut = null;

    public void setUp() throws Exception {

        //Hardcode running configuation
        System.setProperty("javax.xml.parsers.SAXParserFactory", "org.apache.xerces.jaxp.SAXParserFactoryImpl");
        System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");

        //Used only to speed up Xpath tests
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "net.sf.saxon.dom.DocumentBuilderFactoryImpl");
        System.setProperty("javax.xml.xpath.XPathFactory:http://java.sun.com/jaxp/xpath/dom", "net.sf.saxon.xpath.XPathFactoryImpl");


        XpathFactoryClass = XPathFactory.newInstance().getClass();

        System.out.println(TestUtils.getJaxpImplementationInfo("DocumentBuilderFactory", DocumentBuilderFactory.newInstance().getClass()));
        System.out.println(TestUtils.getJaxpImplementationInfo("SAXParserFactory", SAXParserFactory.newInstance().getClass()));
        System.out.println(TestUtils.getJaxpImplementationInfo("XPathFactory", XpathFactoryClass));
        System.out.println(TestUtils.getJaxpImplementationInfo("TransformerFactory", TransformerFactory.newInstance().getClass()));

        //factory = XMLUnit.getControlDocumentBuilderFactory();
        factory = DocumentBuilderFactory.newInstance();
        System.out.println("factory = " + factory.getClass().getName());
        factory.setValidating(false);
        builder = factory.newDocumentBuilder();

        XMLUnit.setXPathFactory(XpathFactoryClass.getName());
        System.out.println("xpathfactory = " + XMLUnit.getXPathFactory());
        engine = XMLUnit.newXpathEngine();


        // Use temp outFile
        tmpOut = File.createTempFile("dat", null);
        writer = BufferedRW.getBufferedWriter(tmpOut, TestUtils.UTF8);
        wrapper = new TransformWrapper();
        brStylesheet = null;
        outPutMethod = 0;

    }

    public void tearDown() throws Exception {

        if (writer != null) {
            writer.close();
        }
        // free resources
        brXML.close();
        brStylesheet.close();

        if (tmpOut != null) {
            tmpOut.deleteOnExit();
        }

        wrapper.resetTransformer();

    }

    /*
    // FIXME: le fichier test/testRCR751032301.xml n'est pas présent dans les sources
    public void testTransformRCREmptyParameters() {

        String xmlSourceFileName = "test/testRCR751032301.xml";
        // xmlSource XML read from file
        brXML = TestUtils.UTF8Reader(xmlSourceFileName);
        NodeList l = null;
        try {
            //check Input XML must contains datagroup[(@tag='loc')]
            InputSource inXML = new InputSource(brXML);
            Document inDocument = builder.parse(inXML);
            l = engine.getMatchingNodes("//datagroup[(@tag='loc')]", inDocument);
            assertEquals(6217, l.getLength());

        } catch (XpathException e) {
            fail(e.getMessage());
        } catch (SAXException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        }

        brXML = TestUtils.UTF8Reader(xmlSourceFileName);


        String styleSheetFileName = "filterRCR.xsl"; // XSLTStylesheet
        if (!wrapper.isXSLTCTranslets()) {
            brStylesheet = TestUtils.UTF8Reader(styleSheetFileName);
        }
        outPutMethod = TransformWrapper.OUTPUT_XML;

        try {
            wrapper.transform(brXML, brStylesheet,
                    writer, TransformWrapper.outPutProperties(outPutMethod));

        } catch (TransformerException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        }

        assertTrue(tmpOut.length() > 0);

        //XPath for detailed assertions
        Reader brOut = null;
        try {
            System.out.print(tmpOut.getPath());
            brOut = TestUtils.UTF8Reader(tmpOut.getPath());
            InputSource outXML = new InputSource(brOut);

            Document outDocument = builder.parse(outXML);
            //Must not exist datagroup[(@tag='loc')]
            l = engine.getMatchingNodes("//datagroup[(@tag='loc')]", outDocument);
            assertEquals(0, l.getLength());

        } catch (XpathException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        } catch (SAXException e) {
            fail(e.getMessage());
        } finally {
            try {
                brOut.close();
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }


    }

    public void testTransformRCRParameters() {

        String xmlSourceFileName = "test/testRCR751032301.xml";
        // xmlSource XML read from file
        brXML = TestUtils.UTF8Reader(xmlSourceFileName);

        String styleSheetFileName = "filterRCR.xsl"; // XSLTStylesheet
        if (!wrapper.isXSLTCTranslets()) {
            brStylesheet = TestUtils.UTF8Reader(styleSheetFileName);
        }
        outPutMethod = TransformWrapper.OUTPUT_XML;

        try {
            wrapper.setTransformer(brStylesheet, TransformWrapper.outPutProperties(outPutMethod));
            wrapper.setParameter("rcrselected", "751032301");
            wrapper.transform(brXML, writer);

        } catch (TransformerException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        }

        assertTrue(tmpOut.length() > 0);

        //XPath for detailed assertions
        Reader brOut = null;
        try {
            System.out.print(tmpOut.getPath());
            brOut = TestUtils.UTF8Reader(tmpOut.getPath());
            InputSource outXML = new InputSource(brOut);

            //Document outDocument=XMLUnit.buildControlDocument(outXML);
            Document outDocument = builder.parse(outXML);


            //Must exist datafield 930 subfield b = RCR tags
            NodeList l = engine.getMatchingNodes("//datagroup[(@tag='loc')]/datafield[(@tag='930')]/subfield[(@code='b' and text()='751032301')]", outDocument);
            assertEquals(4776, l.getLength());

            //Must no exist datafield 930 subfield b != RCR tags
            l = engine.getMatchingNodes("//datagroup[(@tag='loc')]/datafield[(@tag='930')]/subfield[(@code='b' and text()!='751032301')]", outDocument);
            assertEquals(0, l.getLength());

        } catch (XpathException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        } catch (SAXException e) {
            fail(e.getMessage());
        } finally {
            try {
                brOut.close();
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }

    }
    */


    public void testTransformToDataGroup() {

        String xmlSourceFileName = "test/testbeforedatagroup.xml";
        // xmlSource XML read from file
        brXML = TestUtils.UTF8Reader(xmlSourceFileName);

        String styleSheetFileName = "datagrouploc.xsl"; // XSLTStylesheet
        if (!wrapper.isXSLTCTranslets()) {
            brStylesheet = TestUtils.UTF8Reader(styleSheetFileName);
        }
        outPutMethod = TransformWrapper.OUTPUT_XML;


        try {
            wrapper.transform(brXML, brStylesheet,
                    writer, TransformWrapper.outPutProperties(outPutMethod));

        } catch (TransformerException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        }

        assertTrue(tmpOut.length() > 0);

        //XPath for detailed assertions
        Reader brOut = null;
        try {
            System.out.print(tmpOut.getPath());
            brOut = TestUtils.UTF8Reader(tmpOut.getPath());
            InputSource outXML = new InputSource(brOut);
            Document outDocument = builder.parse(outXML);
            HashMap<String, String> m = new HashMap<String, String>();
            m.put("marc", "http://www.loc.gov/MARC21/slim");
            NamespaceContext ctx = new SimpleNamespaceContext(m);
            engine.setNamespaceContext(ctx);

            //No PPN lost
            NodeList l = engine.getMatchingNodes("//controlfield[(@tag='001')]", outDocument);
            brXML.close();
            brXML = TestUtils.UTF8Reader(xmlSourceFileName);
            InputSource inputXML = new InputSource(brXML);
            //Document inputDocument=XMLUnit.buildControlDocument(inputXML);
            Document inputDocument = builder.parse(inputXML);
            NodeList input = engine.getMatchingNodes("//controlfield[(@tag='001')]", inputDocument);
            assertEquals(input.getLength(), l.getLength());


            //Must exist <datagroup tag="loc"> tags
            l = engine.getMatchingNodes("//datagroup[(@tag='loc')]|//marc:datagroup[(@tag='loc')]", outDocument);
            assertEquals(34, l.getLength());

            //Must not exist datafield 930,917,915,955,919,931,991,992,916 outside of datagroup tag
            l = engine.getMatchingNodes("//record/datafield[(@tag='930' or @tag='917' or @tag='915' or @tag='955' or @tag='919' or @tag='931' or @tag='991' or @tag='992' or @tag='916' or (@tag='856' and subfield[@code='5'] != '') or @tag='316' )]", outDocument);
            assertEquals(0, l.getLength());


            NodeList datagroups = engine.getMatchingNodes("//datagroup[(@tag='loc')]", outDocument);
            int nbloc = datagroups.getLength();

            Map<String, String> df930sub5 = new HashMap<String, String>();

            //Tests sur les exemplaires
            for (int i = 0; i < nbloc; i++) {
                Node datagroup = datagroups.item(i);
                if (datagroup.getNodeType() != Node.ELEMENT_NODE) {
                    fail("NOT AN ELEMENT");
                }

                NodeList datafields = datagroup.getChildNodes();
                int nbdfield = datafields.getLength();
                Set<String> codes5 = new HashSet<String>(); //Stocke sans doublons les <subfields code="5"> de tous les datafield de la localisation

                // Datafields
                for (int j = 0; j < nbdfield; j++) {
                    Node datafield = datafields.item(j);
                    if (datafield.getNodeType() != Node.ELEMENT_NODE) {
                        fail("NOT AN ELEMENT");
                    }
                    if (datafield.getNodeName() != "datafield") {
                        fail("NOT A <datafield>");
                    }
                    NamedNodeMap attributes = datafield.getAttributes();
                    String datafieldtag = attributes.getNamedItem("tag").getNodeValue();
                    assertTrue(datafieldtag, "930".equals(datafieldtag) || "915".equals(datafieldtag)
                            || "917".equals(datafieldtag) || "992".equals(datafieldtag)
                            || "991".equals(datafieldtag) || "919".equals(datafieldtag)
                            || "931".equals(datafieldtag) || "955".equals(datafieldtag)
                            || "916".equals(datafieldtag) || "316".equals(datafieldtag));

                    boolean iscurrentdf930 = "930".equals(datafieldtag);

                    NodeList subfields = datafield.getChildNodes();
                    int nbsubs = subfields.getLength();

                    // Subfields
                    for (int k = 0; k < nbsubs; k++) {
                        Node subfield = subfields.item(k);
                        if (subfield.getNodeType() != Node.ELEMENT_NODE) {
                            fail("NOT AN ELEMENT");
                        }
                        if (subfield.getNodeName() != "subfield") {
                            fail("NOT A <subfield>");
                        }

                        attributes = subfield.getAttributes();
                        String code = attributes.getNamedItem("code").getNodeValue();

                        if (code.equals("5")) { //<subfield code="5">

                            String code5 = subfield.getTextContent();
                            if (code5 == null || "".equals(code5.trim())) {
                                fail("NO CODE=5 FOUND");
                            }


                            codes5.add(code5); // test same <subfield code="5"> for all <datafields> of this <datagroup>

                            if (iscurrentdf930) {
                                // test a unique 930/5 per <datagroup tag="loc"> and test different <datagroup tag="loc"> can't share same 930/5
                                String PPN = null;

                                boolean found = false;
                                int n = 0;
                                Node c = null;
                                NodeList pcn = datagroup.getParentNode()
                                        .getChildNodes();
                                do {
                                    // Move back to controlfied tag="001" = PPN
                                    n++;
                                    c = pcn.item(n);

                                    if (c.getNodeName().equals("controlfield")) {
                                        attributes = c.getAttributes();
                                        String controlfieldtag = attributes.getNamedItem("tag")
                                                .getNodeValue();

                                        if (controlfieldtag.equals("001")) {
                                            found = true;
                                            PPN = c.getTextContent();
                                        }

                                    }

                                } while (!found);

                                if (PPN == null || "".equals(PPN.trim())) {
                                    fail("NO PPN FOUND");
                                }

                                String v = df930sub5.get(code5);
                                if (v == null) {
                                    df930sub5.put(code5, PPN);
                                } else {
                                    //un PPN utilise deja cette cle : il ne peut y en avoir un deuxieme !
                                    fail("key=" + code5 + " found in PPN=" + PPN + " was already in PPN=" + v);
                                }

                            }


                        }

                    }

                }
                assertEquals(1, codes5.size());
            }


        } catch (XpathException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        } catch (SAXException e) {
            fail(e.getMessage());
        } finally {
            try {
                brOut.close();
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }


    }

    public void testLevel1TransformToDataGroup() {

        String xmlSourceFileName = "test/testlevel1_039618625.xml";
        // xmlSource XML read from file
        brXML = TestUtils.UTF8Reader(xmlSourceFileName);

        String styleSheetFileName = "datagrouploc.xsl"; // XSLTStylesheet
        if (!wrapper.isXSLTCTranslets()) {
            brStylesheet = TestUtils.UTF8Reader(styleSheetFileName);
        }
        outPutMethod = TransformWrapper.OUTPUT_XML;


        try {
            wrapper.transform(brXML, brStylesheet,
                    writer, TransformWrapper.outPutProperties(outPutMethod));

        } catch (TransformerException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        }

        assertTrue(tmpOut.length() > 0);

        //XPath for detailed assertions
        Reader brOut = null;
        try {
            System.out.print(tmpOut.getPath());
            brOut = TestUtils.UTF8Reader(tmpOut.getPath());
            InputSource outXML = new InputSource(brOut);
            Document outDocument = builder.parse(outXML);
            HashMap<String, String> m = new HashMap<String, String>();
            m.put("marc", "http://www.loc.gov/MARC21/slim");
            NamespaceContext ctx = new SimpleNamespaceContext(m);
            engine.setNamespaceContext(ctx);

            //No PPN lost
            NodeList l = engine.getMatchingNodes("//controlfield[(@tag='001')]", outDocument);
            brXML.close();
            brXML = TestUtils.UTF8Reader(xmlSourceFileName);
            InputSource inputXML = new InputSource(brXML);
            //Document inputDocument=XMLUnit.buildControlDocument(inputXML);
            Document inputDocument = builder.parse(inputXML);
            NodeList input = engine.getMatchingNodes("//controlfield[(@tag='001')]", inputDocument);
            assertEquals(input.getLength(), l.getLength());


            //Must exist <datagroup tag="loc"> tags
            l = engine.getMatchingNodes("//datagroup[(@tag='loc')]|//marc:datagroup[(@tag='loc')]", outDocument);
            assertEquals(6, l.getLength());

            //Must not exist datafield 930,917,915,955,919,931,991,992,916 outside of datagroup tag
            l = engine.getMatchingNodes("//record/datafield[(@tag='930' or @tag='917' or @tag='915' or @tag='955' or @tag='919' or @tag='931' or @tag='991' or @tag='992' or @tag='916' or (@tag='856' and subfield[@code='5'] != '') or @tag='316' )]", outDocument);
            assertEquals(0, l.getLength());


            NodeList datagroups = engine.getMatchingNodes("//datagroup[(@tag='loc')]", outDocument);
            int nbloc = datagroups.getLength();

            Map<String, String> df930sub5 = new HashMap<String, String>();

            //Tests sur les exemplaires
            for (int i = 0; i < nbloc; i++) {
                Node datagroup = datagroups.item(i);
                if (datagroup.getNodeType() != Node.ELEMENT_NODE) {
                    fail("NOT AN ELEMENT");
                }

                NodeList datafields = datagroup.getChildNodes();
                int nbdfield = datafields.getLength();
                Set<String> codes5 = new HashSet<String>(); //Stocke sans doublons les <subfields code="5"> de tous les datafield de la localisation

                // Datafields
                for (int j = 0; j < nbdfield; j++) {
                    Node datafield = datafields.item(j);
                    if (datafield.getNodeType() != Node.ELEMENT_NODE) {
                        fail("NOT AN ELEMENT");
                    }
                    if (datafield.getNodeName() != "datafield") {
                        fail("NOT A <datafield>");
                    }
                    NamedNodeMap attributes = datafield.getAttributes();
                    String datafieldtag = attributes.getNamedItem("tag").getNodeValue();
                    //System.out.println(attributes.getNamedItem("ind1").getNodeValue());
                    //System.out.println(attributes.getNamedItem("ind2").getNodeValue());
                    assertTrue(datafieldtag, "930".equals(datafieldtag) || "915".equals(datafieldtag)
                            || "917".equals(datafieldtag) || "992".equals(datafieldtag)
                            || "991".equals(datafieldtag) || "919".equals(datafieldtag)
                            || "931".equals(datafieldtag) || "955".equals(datafieldtag)
                            || "916".equals(datafieldtag) || "316".equals(datafieldtag) || "856".equals(datafieldtag));

                    boolean iscurrentdf930 = "930".equals(datafieldtag);

                    NodeList subfields = datafield.getChildNodes();
//				System.out.println(datafield.getNamespaceURI());
//				System.out.println(datafield.getNodeName());
//				System.out.println(datafield.getNodeValue());
//				System.out.println(datafield.getTextContent());
                    int nbsubs = subfields.getLength();

                    // Subfields
                    for (int k = 0; k < nbsubs; k++) {
                        Node subfield = subfields.item(k);

                        if (subfield.getNodeType() == Node.TEXT_NODE) {
//							System.out.println(subfield.getNamespaceURI());
//							System.out.println(subfield.getNodeName());
//							System.out.println(subfield.getNodeValue());
//							System.out.println(subfield.getTextContent());
                        } else {

                            if (subfield.getNodeType() != Node.ELEMENT_NODE) {
                                fail("NOT AN ELEMENT");
                            }
                            if (subfield.getNodeName() != "subfield") {
                                fail("NOT A <subfield>");
                            }

                            //System.out.println(subfield.getNamespaceURI());


                            attributes = subfield.getAttributes();
                            String code = attributes.getNamedItem("code")
                                    .getNodeValue();

                            System.out.println(subfield.getNodeName() + " code=" + code + " " + subfield.getTextContent());
                            //System.out.println(subfield.getNodeValue());


                            if (code.equals("5")) { // <subfield code="5">

                                String code5 = subfield.getTextContent();
                                if (code5 == null || "".equals(code5.trim())) {
                                    fail("NO CODE=5 FOUND");
                                }

                                //System.out.println(code5);

                                codes5.add(code5.substring(0, 19)); // test same <subfield
                                // code="5"> for all
                                // <datafields> of this
                                // <datagroup>

                                if (iscurrentdf930) {
                                    // test a unique 930/5 per <datagroup
                                    // tag="loc"> and test different <datagroup
                                    // tag="loc"> can't share same 930/5
                                    String PPN = null;

                                    boolean found = false;
                                    int n = 0;
                                    Node c = null;
                                    NodeList pcn = datagroup.getParentNode()
                                            .getChildNodes();
                                    do {
                                        // Move back to controlfied tag="001" =
                                        // PPN
                                        n++;
                                        c = pcn.item(n);

                                        if (c.getNodeName().equals(
                                                "controlfield")) {
                                            attributes = c.getAttributes();
                                            String controlfieldtag = attributes
                                                    .getNamedItem("tag")
                                                    .getNodeValue();

                                            if (controlfieldtag.equals("001")) {
                                                found = true;
                                                PPN = c.getTextContent();
                                            }

                                        }

                                    } while (!found);

                                    if (PPN == null || "".equals(PPN.trim())) {
                                        fail("NO PPN FOUND");
                                    }

                                    String v = df930sub5.get(code5);
                                    if (v == null) {
                                        df930sub5.put(code5, PPN);
                                    } else {
                                        // un PPN utilise deja cette cle : il ne
                                        // peut y en avoir un deuxieme !
                                        fail("key=" + code5 + " found in PPN="
                                                + PPN + " was already in PPN="
                                                + v);
                                    }

                                }

                            }

                        }

                    }

                }
                assertEquals(1, codes5.size());
            }


        } catch (XpathException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        } catch (SAXException e) {
            fail(e.getMessage());
        } finally {
            try {
                brOut.close();
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }


    }

}
