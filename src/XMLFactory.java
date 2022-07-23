package Project1;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.*;
import java.util.ArrayList;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * This class provides the ability to create, read XML file.
 * @author Goheung Choi
 */
public class XMLFactory {
    /**
     * Read XML file and create Parcel Objects into the ArrayList.
     * @param fileName
     * @return ArrayList
     */
    public static ArrayList<Point> fileReader(String fileName) {
        ArrayList<Point> points = new ArrayList<>();
        try {
            Point tempPoint;
            
            File inputFile = new File(fileName);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            Document doc = builder.parse(inputFile);
            doc.getDocumentElement().normalize();
            
            XPath xPath = XPathFactory.newInstance().newXPath();
            
            String expression = "/points/point";
            NodeList nodeList = (NodeList) xPath.compile(expression)
                    .evaluate(doc, XPathConstants.NODESET);
            
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node nNode = nodeList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    // get parcel
                    tempPoint =  new Point(Float.parseFloat(eElement.getElementsByTagName("xvalue").item(0).getTextContent()),
                            Float.parseFloat(eElement.getElementsByTagName("yvalue").item(0).getTextContent())
                    );
                    points.add(tempPoint);
                }
            }
            
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
        }
        return points;
    }
    /**
     * Builds an XML file based on parcel ArrayList.
     * @param users
     * @param fileName
     */
    public static void fileBuilder(ArrayList<Point> points, String fileName) {
        try {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();
        
        // root element
        Element rootElement = doc.createElement("points");
        doc.appendChild(rootElement);
        
        for(int i = 0; i < points.size(); i++) {
            // user element
            Element point = doc.createElement("point");
            rootElement.appendChild(point);

            // timestamp element
            Element xvalue = doc.createElement("xvalue");
            xvalue.appendChild(doc.createTextNode(String.valueOf(points.get(i).getX())));
            point.appendChild(xvalue);

            Element yvalue = doc.createElement("yvalue");
            yvalue.appendChild(doc.createTextNode(String.valueOf(points.get(i).getY())));
            point.appendChild(yvalue);
            
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(fileName));
            transformer.transform(source, result);

            // Output to console for testing
            StreamResult consoleResult = new StreamResult(System.out);
            transformer.transform(source, consoleResult);
        }
        } catch (ParserConfigurationException | TransformerException | DOMException e) {
        }
    }
}
