import com.vx6.master.MasterVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import freemarker.cache.NullCacheStorage;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import io.vertx.ext.web.common.template.impl.TemplateHolder;

import java.io.IOException;
import java.io.StringReader;

public class Template2LocalMap extends MasterVerticle {
    private Configuration config;
    private LocalMap<String, TemplateHolder<Template>> cache;
    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        SharedData sharedData = vertx.sharedData();
        cache = sharedData.getLocalMap("template_patterns");
        /* ------------------------------------------------------------------------ */
        /* You should do this ONLY ONCE in the whole application life-cycle:        */

        /* Create and adjust the configuration singleton */
        this.config = new Configuration(Configuration.VERSION_2_3_29);
        //this.config.setDirectoryForTemplateLoading(new File("/templates"));
        // Recommended settings for new projects:
        this.config.setDefaultEncoding("UTF-8");
        this.config.setIncompatibleImprovements(new Version(2, 3, 20));
        this.config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        this.config.setLogTemplateExceptions(false);
        this.config.setWrapUncheckedExceptions(true);
        this.config.setFallbackOnNullLoopVariable(false);
        /* ------------------------------------------------------------------------ */
        this.config.setCacheStorage(new NullCacheStorage());

        super.initialize(initPromise);
    }
    @Override
    public void process(Message msg) {
        TemplateHolder<Template> template;
        JsonObject pat = (JsonObject) msg.body();
        String transId = pat.getString("TransId");
        String text = pat.getString("text");
        try {
            template = new TemplateHolder(new Template(transId, new StringReader(text), this.config));
            this.cache.put(transId, template);
            eb.publish(addressBook.getResult(), pat, addressBook.getDeliveryOptions(msg));
        } catch (IOException e) {
            eb.publish(addressBook.getError(), e.getMessage(), addressBook.getDeliveryOptions(msg));
            e.printStackTrace();
        }
    }
}