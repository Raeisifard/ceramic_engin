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
        //System.out.println("msg: " + msg.body().toString());
        //Use method to convert XML string content to XML Document object
        Document doc = convertStringToXMLDocument(msg.body().toString());
        doc.getDocumentElement().normalize();
        Element root = doc.getDocumentElement();
        JsonObject joRoot = new JsonObject();
        joRoot.put("root_tag_name", root.getTagName());
        NamedNodeMap attrs = root.getAttributes();
        for (int i = 0; i < attrs.getLength(); ++i) {
            Node attr = attrs.item(i);
            joRoot.put(attr.getNodeName(), attr.getNodeValue());
        }
        //System.out.println("joRoot: " + joRoot.toString());
        NodeList pattern = root.getElementsByTagName("Pattern");
        for (int i = 0; i < pattern.getLength(); i++) {
            JsonObject joPattern = new JsonObject();
            Node patElement = pattern.item(i);
            joPattern.put("pattern_tag_name", patElement.getNodeName());
            NamedNodeMap atts = patElement.getAttributes();
            for (int j = 0; j < atts.getLength(); j++) {
                Node att = atts.item(j);
                joPattern.put(att.getNodeName(), att.getNodeValue());
            }
            joPattern.put("text", patElement.getTextContent());
            joPattern.mergeIn(joRoot.copy());
            eb.publish(addressBook.getResult(), joPattern, addressBook.getDeliveryOptions(msg));
            //System.out.println("Pattern#" + i + ": " + joPattern.toString());
        }
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