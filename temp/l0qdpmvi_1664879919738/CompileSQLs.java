import com.vx6.master.MasterVerticle;
import freemarker.template.*;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import freemarker.cache.NullCacheStorage;
import io.vertx.ext.web.common.template.impl.TemplateHolder;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Set;

public class CompileSQLs extends MasterVerticle {
    private static final String Cache_Name = "sql_template_patterns";
    private int count = 0;
    private Configuration config;
    //private LocalMap<String, TemplateHolder<Template>> cache;
    private LocalMap<String, TemplateHolder<Template>> cache;
    private static String label = "<h3 style=\"margin: 0;\">CompileSQLs</h3>" +
            "<h3 style=\"display: inline-block; margin: 0;\">Patterns Size:&nbsp;</h3>" +
            "<h2 style=\"display: inline-block; margin: 0;color: %s\">%s</h2>";
    private static ArrayList<String> nameList = new ArrayList<>();

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        SharedData sharedData = vertx.sharedData();
        cache = sharedData.getLocalMap(Cache_Name);
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
        this.config.setSetting("boolean_format", "c");
        /* ------------------------------------------------------------------------ */
        this.config.setCacheStorage(new NullCacheStorage());

        super.initialize(initPromise);
    }

    @Override
    public void process(Message msg) {
        resultOutboundCount--;
        if (msg.body() instanceof String && msg.body().toString().equalsIgnoreCase("finished")) {
            Set keySet = this.cache.keySet();
            keySet.forEach(key -> {
                if (!nameList.contains(key))
                    cache.remove(key);
            });
            //this.cache.entrySet().removeIf(entry -> true);
            sendLabel(String.format(label, this.cache.size() == 0 ? "red" : "#0043ff", this.cache.size()), "18ff96", "50", "efff18");
            /*eb.publish(addressBook.getGraph_id(), patternName + "registry patterns completed", addressBook.getDeliveryOptions()
                    .addHeader("patternName", patternName).addHeader("cmd", "init"));*/
            eb.publish(addressBook.getResult(), "SQL template loaded",
                    addressBook.getDeliveryOptions().addHeader("cmd", "status").addHeader("name", "sql"));
            resultOutboundCount++;
            nameList.clear();
        } else {
            TemplateHolder<Template> template;
            JsonObject pat = (JsonObject) msg.body();
            if (!pat.getString("group").equalsIgnoreCase("registry"))
                return;
            String name = pat.getString("name");
            String text = pat.getString("text");
            try {
                template = new TemplateHolder(new Template(name, new StringReader(text), this.config));
                this.cache.put(name, template);
                nameList.add(name);
                count++;
                errorOutboundCount++;
                eb.publish(addressBook.getError(), pat, addressBook.getDeliveryOptions(msg));
            } catch (IOException e) {
                errorOutboundCount++;
                eb.publish(addressBook.getError(), e.getMessage(), addressBook.getDeliveryOptions(msg));
                e.printStackTrace();
            }
        }
    }
}