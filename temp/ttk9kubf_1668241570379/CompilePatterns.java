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

public class CompilePatterns extends MasterVerticle {
    private static final String patternName = "template_patterns";
    private int count = 0;
    private Configuration config;
    //private LocalMap<String, TemplateHolder<Template>> cache;
    private LocalMap<String, TemplateHolder<Template>> cache;
    private static String label = "<h3 style=\"margin: 0;\">CompilePatterns</h3>" +
            "<h3 style=\"display: inline-block; margin: 0;\">Patterns Size:&nbsp;</h3>" +
            "<h2 style=\"display: inline-block; margin: 0;color: %s\">%s</h2>";
    private static ArrayList<String> transIdList = new ArrayList<>();

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        SharedData sharedData = vertx.sharedData();
        cache = sharedData.getLocalMap(patternName);
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
        this.config.setNumberFormat(",##0");
        this.config.setClassForTemplateLoading(this.getClass(), "/");
        this.config.addAutoImport("decorate", "/templates/decorate.ftl");
        /* ------------------------------------------------------------------------ */
        this.config.setCacheStorage(new NullCacheStorage());

        super.initialize(initPromise);
    }

    @Override
    public void process(Message msg) {
        if (msg.body() instanceof String && msg.body().toString().equalsIgnoreCase("finished")) {
            Set keySet = this.cache.keySet();
            keySet.forEach(key -> {
                if (!transIdList.contains(key))
                    cache.remove(key);
            });
            //this.cache.entrySet().removeIf(entry -> true);
            sendLabel(String.format(label, this.cache.size() == 0 ? "red" : "#0043ff", this.cache.size()), "18ff96", "50", "efff18");
            /*eb.publish(addressBook.getGraph_id(), patternName + "registry patterns completed", addressBook.getDeliveryOptions()
                    .addHeader("patternName", patternName).addHeader("cmd", "init"));*/
            eb.publish(addressBook.getResult(), "Patterns loaded",
                    addressBook.getDeliveryOptions().addHeader("cmd", "status").addHeader("name", "patterns"));
            transIdList.clear();
        } else {
            TemplateHolder<Template> template;
            JsonObject pat = (JsonObject) msg.body();
            String transId = pat.getString("TransId");
            String text = pat.getString("text");
            try {
                if (pat.getString("root_tag_name").equalsIgnoreCase("globals"))
                    this.config.setSharedVariable(transId, text.trim());
                else {
                    template = new TemplateHolder(new Template(transId, new StringReader(text.trim()), this.config));
                    this.cache.put(transId, template);
                }
                transIdList.add(transId);
                count++;
                resultOutboundCount--;
                errorOutboundCount++;
                eb.publish(addressBook.getError(), pat, addressBook.getDeliveryOptions(msg));
            } catch (IOException | TemplateModelException e) {
                resultOutboundCount--;
                errorOutboundCount++;
                eb.publish(addressBook.getError(), e.getMessage(), addressBook.getDeliveryOptions(msg));
                e.printStackTrace();
            }
        }
    }
}