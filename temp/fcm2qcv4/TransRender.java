
import com.vx6.master.MasterVerticle;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.common.template.impl.TemplateHolder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

public class TransRender extends MasterVerticle {
    private LocalMap<String, TemplateHolder<Template>> cache;

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        SharedData sharedData = vertx.sharedData();
        cache = sharedData.getLocalMap("template_patterns");
        super.initialize(initPromise);
    }

    @Override
    public void process(Message msg) {
        JsonObject trans = (JsonObject) msg.body();
        TemplateHolder<Template> th = cache.get(trans.getString("module"));
        if (th != null) {
            StringWriter stringWriter = new StringWriter();
            try {
                (th.template()).process(trans.getMap(), stringWriter);
                eb.publish(addressBook.getResult(), stringWriter.toString(), addressBook.getDeliveryOptions(msg));
            } catch (TemplateException | IOException e) {
                eb.publish(addressBook.getError(), trans, addressBook.getDeliveryOptions(msg).addHeader("cause", e.getMessage()));
                //e.printStackTrace();
            }
        } else {
            eb.publish(addressBook.getError(), trans, addressBook.getDeliveryOptions(msg).addHeader("cause", "template_not_found"));
        }
    }
}