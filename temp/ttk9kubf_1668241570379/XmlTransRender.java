
import com.vx6.master.MasterVerticle;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.vertx.core.Promise;;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.common.template.impl.TemplateHolder;

import java.io.IOException;
import java.io.StringWriter;

public class XmlTransRender extends MasterVerticle {
    private LocalMap<String, TemplateHolder<Template>> cache;

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        SharedData sharedData = vertx.sharedData();
        cache = sharedData.getLocalMap("template_patterns");
        super.initialize(initPromise);
    }

    @Override
    public void process(Message msg) {
        resultOutboundCount--;
        JsonObject body = (JsonObject) msg.body();
        JsonObject tran = body.getJsonObject("tran");
        TemplateHolder<Template> th = cache.get(tran.getString("COMMAND"));
        if (th != null) {
            StringWriter stringWriter = new StringWriter();
            try {
                (th.template()).process(tran.getMap(), stringWriter);
                tran.put("msg", stringWriter.toString().trim());
                eb.publish(addressBook.getResult(), body, addressBook.getDeliveryOptions(msg));
                resultOutboundCount++;
            } catch (TemplateException | IOException e) {
                eb.publish(addressBook.getError(), body, addressBook.getDeliveryOptions(msg).addHeader("cause", e.getMessage()));
                errorOutboundCount++;
                //e.printStackTrace();
            }
        } else {
            eb.publish(addressBook.getError(), body, addressBook.getDeliveryOptions(msg).addHeader("cause", "template_not_found"));
            errorOutboundCount++;
        }
    }
}