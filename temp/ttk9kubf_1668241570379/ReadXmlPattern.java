import com.vx6.master.MasterVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.StringReader;
import javax.xml.parsers.*;
import java.io.*;

public class ReadXmlPattern extends MasterVerticle {
    private DocumentBuilder builder;

    @Override
    public void initialize(Promise<Void> initPromise) {
        try {
            //Parser that produces DOM object trees from XML content
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            //API to obtain DOM Document instance
            //DocumentBuilder builder = null;

            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();

            initPromise.complete();
        } catch (Exception e) {
            initPromise.fail("xml builder not initialized!");
        }
    }

    @Override
    public void process(Message msg) {
        resultOutboundCount--;
        //System.out.println("msg: " + msg.body().toString());
        //Use method to convert XML string content to XML Document object
        Document doc = convertStringToXMLDocument("<xml>" + msg.body().toString() + "</xml>");
        doc.getDocumentElement().normalize();
        Element root = doc.getDocumentElement();
        NodeList groups = root.getChildNodes();
        for (int i = 0; i < groups.getLength(); i++) {
            JsonObject joRoot = new JsonObject();
            if (!(groups.item(i) instanceof Element))
                continue;
            joRoot.put("root_tag_name", groups.item(i).getNodeName());
            NamedNodeMap attrs = groups.item(i).getAttributes();
            for (int j = 0; j < attrs.getLength(); ++j) {
                Node attr = attrs.item(j);
                joRoot.put(attr.getNodeName(), attr.getNodeValue());
            }
            Node group = groups.item(i);
            if (group instanceof Element) {
                Element groupElement = (Element) group;
                NodeList patterns = groupElement.getElementsByTagName("Pattern");
                for (int j = 0; j < patterns.getLength(); j++) {
                    JsonObject joPattern = new JsonObject();
                    Node patElement = patterns.item(j);
                    joPattern.put("pattern_tag_name", patElement.getNodeName());
                    NamedNodeMap atts = patElement.getAttributes();
                    for (int k = 0; k < atts.getLength(); k++) {
                        Node att = atts.item(k);
                        joPattern.put(att.getNodeName(), att.getNodeValue());
                    }
                    joPattern.put("text", patElement.getTextContent());
                    joPattern.mergeIn(joRoot.copy());
                    eb.publish(addressBook.getResult(), joPattern, addressBook.getDeliveryOptions(msg));
                    resultOutboundCount++;
                    //System.out.println("Pattern#" + i + ": " + joPattern.toString());
                }
            }
        }

        //System.out.println("joRoot: " + joRoot.toString());

        resultOutboundCount++;
        eb.publish(addressBook.getResult(), "finished");
    }

    private void sendError(Message msg, Exception e) {
        eb.publish(addressBook.getError(), msg.body(), addressBook.getDeliveryOptions(msg).addHeader("error", e.getMessage()));
    }

    private Document convertStringToXMLDocument(String xmlString) {

        try {
            //Parse the content to Document object
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}