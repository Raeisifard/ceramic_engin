import com.vx6.master.MasterVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.SharedData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Kickstart extends MasterVerticle {
    private boolean initiated = false;
    private SharedData sharedData;
    private HashMap<Integer, JsonObject> bodies = new HashMap<>();
    private HashMap<Integer, MultiMap> headers = new HashMap();
    private List<Future> futuresList;
    private Promise<Void> accountPro = Promise.promise();
    private Promise<Void> maxSendRefNo2Pro = Promise.promise();
    private Promise<Void> patternsPro = Promise.promise();
    private Promise<Void> banksPro = Promise.promise();
    private Promise<Void> branchesPro = Promise.promise();
    private Promise<Void> currencyPro = Promise.promise();
    private Promise<Void> devicesPro = Promise.promise();
    private Promise<Void> stmtPro = Promise.promise();
    private Promise<Void> sqlPro = Promise.promise();
    private static final String green = "<div style=\" width: 50px; height: 50px; margin: 10px auto; background-color: #ABFF00; border-radius: 50%; box-shadow: rgba(0, 0, 0, 0.2) 0 -1px 7px 1px, inset #441313 0 -1px 9px, rgba(255, 0, 0, 0.5) 0 2px 12px;\"></div>";
    private static final String red = "<div style=\" width: 50px; height: 50px; margin: 10px auto; background-color: #F00; border-radius: 50%; box-shadow: rgba(0, 0, 0, 0.2) 0 -1px 7px 1px, inset #304701 0 -1px 9px, #89FF00 0 2px 12px;\"></div>";

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        futuresList = Arrays.asList(accountPro.future(), maxSendRefNo2Pro.future(), patternsPro.future(), sqlPro.future()
                , banksPro.future(), branchesPro.future(), currencyPro.future(), devicesPro.future(), stmtPro.future());
        CompositeFuture.all(futuresList).onComplete(ar -> {
            if (ar.succeeded()) {
                initiated = true;
                sendLabel("<h1 style=\"margin:0;\">Kickstart</h1>" + green);
                eb.publish(addressBook.getGraph_id(), "State initiated", addressBook.getDeliveryOptions()
                        .addHeader("cmd", "status").addHeader("name", "init"));
                eb.publish(addressBook.getResult(), "State initiated", addressBook.getDeliveryOptions()
                        .addHeader("cmd", "status").addHeader("name", "init"));
                //Ready to turn on inputs
                for (Integer i : bodies.keySet()) {
                    if (bodies.get(i) != null)
                        publishOut(i, bodies.get(i), addressBook.getDeliveryOptions().setHeaders(headers.get(i)));
                }
                bodies.clear();
                headers.clear();
            } else {
                //Nothing by now
                initiated = false;
                sendLabel("<h1 style=\"margin:0;\">Kickstart</h1>" + red);
            }
        });
        sendLabel("<h1 style=\"margin:0;\">Kickstart</h1>" + (initiated ? green : red));
        initPromise.complete();
        sharedData = vertx.sharedData();
    }

    @Override
    public void process(Message msg) {
        if (msg.headers().contains("cmd") && msg.headers().get("cmd").equals("status") && msg.headers().contains("name")) {
            var name = msg.headers().get("name");
            switch (name) {
                case "account":
                    if (!accountPro.future().isComplete())
                        accountPro.complete();
                    break;
                case "MaxSendRefNo2":
                    if (!maxSendRefNo2Pro.future().isComplete())
                        maxSendRefNo2Pro.complete();
                    break;
                case "patterns":
                    if (!patternsPro.future().isComplete())
                        patternsPro.complete();
                    break;
                case "banks":
                    if (!banksPro.future().isComplete())
                        banksPro.complete();
                    break;
                case "branches":
                    if (!branchesPro.future().isComplete())
                        branchesPro.complete();
                    break;
                case "currency":
                    if (!currencyPro.future().isComplete())
                        currencyPro.complete();
                    break;
                case "devices":
                    if (!devicesPro.future().isComplete())
                        devicesPro.complete();
                    break;
                case "stmt":
                    if (!stmtPro.future().isComplete())
                        stmtPro.complete();
                    break;
                case "sql":
                    if (!sqlPro.future().isComplete())
                        sqlPro.complete();
                    break;
            }
        }
    }

    @Override
    public <T> void noCmd(Message<T> msg, String cmd) {
        try {
            JsonObject body = (JsonObject) msg.body();
            if (initiated) {
                publishOut(body.getInteger("no"), msg.body(), addressBook.getDeliveryOptions(msg));
            } else {
                bodies.put(body.getInteger("no"), body);
                headers.put(body.getInteger("no"), msg.headers());
            }
        } catch (Exception ignored) {
        }
    }
    
    @Override
    public <T> void ready(Message<T> tMessage) {
        if (!(sharedData.getLocalMap("SAPTA_ACCOUNTS_3").isEmpty() || accountPro.future().isComplete()))
            accountPro.complete();
        if (!(sharedData.getLocalMap("SAPTA_STMT").isEmpty() || stmtPro.future().isComplete()))
            stmtPro.complete();
        if (!(sharedData.getLocalMap("SAPTA_BRANCHES").isEmpty() || branchesPro.future().isComplete()))
            branchesPro.complete();
        if (!(sharedData.getLocalMap("SAPTA_CURRENCY").isEmpty() || currencyPro.future().isComplete()))
            currencyPro.complete();
        if (!(sharedData.getLocalMap("SAPTA_BANKS").isEmpty() || banksPro.future().isComplete()))
            banksPro.complete();
        if (!(sharedData.getLocalMap("SAPTA_DEVICES").isEmpty() || devicesPro.future().isComplete()))
            devicesPro.complete();
        if (!(sharedData.getLocalMap("template_patterns").isEmpty() || patternsPro.future().isComplete()))
            patternsPro.complete();
    }

    @Override
    public void stop() throws Exception {
        initiated = false;
        sendLabel("<h1 style=\"margin:0;\">Kickstart</h1>" + red);
    }
}