import com.vx6.master.MasterVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.Promise;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import java.io.StringReader;
import javax.xml.parsers.*;

public class Xml2Dom extends MasterVerticle {
    private DocumentBuilder builder;

    @Override
    public void initialize(Promise<Void> initPromise) {
        try {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
            initPromise.complete();
        } catch (Exception e) {
            initPromise.fail("xml builder not initialized!");
        }
    }

    @Override
    public void process(Message msg) {
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
        eb.publish(addressBook.getResult(), joRoot, addressBook.getDeliveryOptions(msg));
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