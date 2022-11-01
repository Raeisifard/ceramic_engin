import com.vx6.master.MasterVerticle;
import com.vx6.utils.PersianCalendarBean;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.Date;


public class WSScheduler extends MasterVerticle {
    private static final String address_base = "cron.scheduler";
    private String schId;

    @Override
    public <T> void start(Message<T> msg) {
        JsonObject body = (JsonObject) msg.body();
        eb.consumer(body.getString("address"), this::control);
        eb.request(address_base + ".schedule", body, ar -> {
            if (ar.succeeded()) {
                this.schId = ar.result().body().toString();
                //System.out.println("Received reply: " + ar.result().body());
            }
        });
    }

    @Override
    public void stop() throws Exception {
        eb.publish(address_base + ".cancel", schId);
        eb.publish(addressBook.getError(), "Scheduler \"" + schId + "\" has been canceled!");
        errorOutboundCount++;
        System.out.println("Scheduler has been canceled!");
    }

    @Override
    protected  <T> void control(Message<T> msg) {
        if (msg.body() instanceof JsonObject) {
            JsonObject body = (JsonObject) msg.body();
            if (body.containsKey("cmd") && body.getString("cmd").equalsIgnoreCase("browse")) {
                publish(msg);
            }
        }
    }

    private <T> void publish(Message<T> tMessage) {
        Date date = new Date();
        eb.publish(addressBook.getResult(), ((JsonObject) tMessage.body())
                .put("SolarDate", PersianCalendarBean.getPersianDate(date))
                .put("GregorianDate", date.toString()), addressBook.getDeliveryOptions(tMessage)
                .addHeader("SolarDate", PersianCalendarBean.getPersianDate(date))
                .addHeader("GregorianDate", date.toString()));
        resultOutboundCount++;
    }
}