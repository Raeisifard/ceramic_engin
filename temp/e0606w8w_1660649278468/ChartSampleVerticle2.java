import com.vx6.master.MasterVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Random;

public class ChartSampleVerticle2 extends MasterVerticle {
    private int count = 0;
    private static Random rand = new Random();

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        sendChart(this.setting, "Throughput Statistic");
        initPromise.complete();
    }

    @Override
    public void process(Message msg) {
        resultOutboundCount--;
        char randCh = (char) (rand.nextInt(26) + 'a');
        JsonArray ja = new JsonArray();
        int randInt = rand.nextInt(101);
        ja.add(randInt);
        JsonObject jo = new JsonObject().put("data", ja);
        ja = new JsonArray();
        ja.add(jo);
        JsonArray labels = new JsonArray().add(Character.toUpperCase(randCh) + (randInt + ""));
        jo = new JsonObject();
        jo.put("datasets", ja);
        jo.put("labels", labels);
        JsonObject data = new JsonObject().put("data", jo);
        //System.out.println(data.toString());
        sendChart(data);
    }
}


