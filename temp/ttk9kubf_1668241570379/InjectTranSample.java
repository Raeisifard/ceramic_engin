import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.vx6.master.MasterVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.List;

public class InjectTranSample extends MasterVerticle {
    private Configuration configuration;

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        configuration = Configuration.builder().options(Option.AS_PATH_LIST, Option.SUPPRESS_EXCEPTIONS, Option.ALWAYS_RETURN_LIST).build();
        initPromise.complete();
    }

    @Override
    public <T> void noCmd(Message<T> msg, String cmd) {
        String jsonString = setting.toString();
        String sampleName = "";
        JsonObject body = (JsonObject) msg.body();
        if (body.containsKey("TransId"))
            sampleName = body.getString("TransId").toUpperCase().trim();
        List<String> allPaths;
        if (sampleName.contains("$"))
            allPaths = JsonPath.using(configuration).parse(jsonString).read(sampleName);
        else {
            if (sampleName.length() > 0)
                sampleName = "." + sampleName;
            allPaths = JsonPath.using(configuration).parse(jsonString).read("$" + sampleName + "..*");
            List<String> exludedPaths = JsonPath.using(configuration).parse(jsonString).read("$" + sampleName + "..*[?(@.length != 0)]");
            allPaths.removeAll(exludedPaths);
        }
        for (String p : allPaths) {
            String sampleTrans = JsonPath.parse(jsonString).read(p);
            if (sampleTrans.trim().length() > 0) {
                eb.publish(addressBook.getResult(), sampleTrans, addressBook.getDeliveryOptions(msg));
                this.resultOutboundCount++;
            }
        }
    }
}