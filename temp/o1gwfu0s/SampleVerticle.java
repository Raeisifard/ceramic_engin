import com.vx6.master.MasterVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static java.lang.Integer.parseInt;

import java.util.Random;

public class SampleVerticle extends MasterVerticle {
  private static Random rand = new Random();

  @Override
  public void process(Message msg) {
    JsonObject body = (JsonObject) msg.body();
    body.put("duration", parseInt(msg.headers().get("duration")));
    System.out.println(msg.headers().get("duration"));
    try {
      eb.publish(addressBook.getResult(), body, addressBook.getDeliveryOptions(msg));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}