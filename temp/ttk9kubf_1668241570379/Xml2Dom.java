import com.vx6.master.MasterVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class Xml2Dom extends MasterVerticle {
    SAXBuilder saxBuilder = new SAXBuilder();
    @Override
    public void process(Message msg) {
        JsonObject joTran = new JsonObject();
        String transRaw = msg.body().toString();
        if (transRaw.trim().length() == 0)
            return;
        Document document = null;
        try {
            document = saxBuilder.build(new StringReader(transRaw));
            Element commandXMLConfigElement = document.getRootElement();
            String command = commandXMLConfigElement.getName().toUpperCase();
            joTran.put("Command".toUpperCase(), command);
            List<org.jdom2.Attribute> attributes = commandXMLConfigElement.getAttributes();
            for (org.jdom2.Attribute attribute : attributes) {
                joTran.put(attribute.getName().toUpperCase(), attribute.getValue());
            }
        } catch (JDOMException e) {
            sendException(e);
        } catch (IOException e) {
            sendException(e);
        }
        
        eb.publish(addressBook.getResult(), new JsonObject().put("tran", joTran)
            .put("rawXml", transRaw).put("channel", msg.headers().get("type")).put("tranType", "registery"));
    }
}