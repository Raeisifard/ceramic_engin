import com.vx6.master.MasterVerticle;
import freemarker.cache.NullCacheStorage;
import freemarker.template.*;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class BmiPaidBill extends MasterVerticle {
    private Configuration config;
    private Template template;
    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {

        this.config = new Configuration(Configuration.VERSION_2_3_29);
        this.config.setDefaultEncoding("UTF-8");
        this.config.setIncompatibleImprovements(new Version(2, 3, 20));
        this.config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        this.config.setLogTemplateExceptions(false);
        this.config.setWrapUncheckedExceptions(true);
        this.config.setFallbackOnNullLoopVariable(false);
        /* ------------------------------------------------------------------------ */
        this.config.setCacheStorage(new NullCacheStorage());
        this.template = new Template(this.getClass().getSimpleName(), new StringReader(this.setting.getString("query")), this.config);
        super.initialize(initPromise);
    }

    @Override
    public void process(Message msg) {
        StringWriter stringWriter = new StringWriter();
        JsonObject body = (JsonObject) msg.body();
        //body.put("ID", body.getString("UniqueId"));
        try {
            template.process(body.getMap(), stringWriter);
            eb.publish(addressBook.getResult(), new JsonObject()
            .put("query", stringWriter.toString())
            .put("cmd", "executupdate"), addressBook.getDeliveryOptions(msg));
        } catch (TemplateException | IOException e) {
            eb.publish(addressBook.getError(), body, addressBook.getDeliveryOptions(msg).addHeader("cause", e.getMessage()));
            //e.printStackTrace();
        }
        //eb.publish(addressBook.getResult(), body, addressBook.getDeliveryOptions(msg));
    }
}