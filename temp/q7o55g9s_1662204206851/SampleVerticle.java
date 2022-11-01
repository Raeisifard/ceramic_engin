import com.vx6.master.MasterVerticle;
import com.vx6.tools.template.freemarker.TemplateHolder;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import freemarker.template.*;
import freemarker.cache.NullCacheStorage;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;


public class SampleVerticle extends MasterVerticle {
    private Configuration config;

    @Override
    public void process(Message msg) {
        resultOutboundCount--;
        JsonObject body = (JsonObject) msg.body();
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
        try {
            this.config.setSetting("boolean_format", "c");
            TemplateHolder<Template> template;
            template = new TemplateHolder(new Template("due", new StringReader(setting.getString("text")), this.config));
            StringWriter stringWriter = new StringWriter();
            try {
                template.template().process(body.getMap(), stringWriter);
                String str = stringWriter.toString();
                eb.publish(addressBook.getResult(), str);
                resultOutboundCount++;
            } catch (TemplateException | IOException e) {
                eb.publish(addressBook.getError(), body, addressBook.getDeliveryOptions(msg).addHeader("cause", e.getMessage()));
                errorOutboundCount++;
                //e.printStackTrace();
            }
        } catch (TemplateException | IOException e) {
            e.printStackTrace();
        }
        /* ------------------------------------------------------------------------ */
        this.config.setCacheStorage(new NullCacheStorage());
    }
}