import com.vx6.master.MasterVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.dropwizard.MetricsService;


public class HeaderAndBodyProbe extends MasterVerticle {
    private String className = this.getClass().getName();
    private static MetricsService metricsService;

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        metricsService = MetricsService.create(vertx);
        initPromise.complete();
    }

    @Override
    public void process(Message msg) {
        System.out.println(className + ".Input.body: " + msg.body());
        System.out.println(className + ".Input.header: " + msg.headers());
    }

    @Override
    public void trigger(Message msg) {
       /* System.out.println(className + ".Trigger.body: " + msg.body());
        System.out.println(className + ".Trigger.header: " + msg.headers());*/
        JsonObject metrics = metricsService.getMetricsSnapshot("vertx.eventbus.messages.published");
        health.put("metrics", metrics);
        //System.out.println(metrics);
    }
}