import com.vx6.master.MasterVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class AtiehSqlAggregation extends MasterVerticle {
    private StringBuilder sqlAggregation = new StringBuilder();
    private int sqlLoadSize = 1, counter = 0;

    @Override
    public void process(Message msg) {
        JsonObject body = (JsonObject) msg.body();
        this.counter++;
        sqlAggregation.append(body.getString("sql"));
        this.resultOutboundCount--;
        if(counter >= sqlLoadSize)
            sendSqlLoad2Db();
    }

    @Override
    public <T> void noCmd(Message<T> tMessage, String cmd) {
        this.sqlLoadSize = ((JsonObject) tMessage.body()).getInteger("sqlLoadSize", 1);
        sendSqlLoad2Db();
    }

    private void sendSqlLoad2Db() {
        String sqlLoad = this.sqlAggregation.toString();
        this.sqlAggregation.setLength(0);
        if (!sqlLoad.isEmpty()) {
            eb.publish(addressBook.getResult(), new JsonObject().put("query", sqlLoad)
                    .put("cmd", "executeupdate"), addressBook.getDeliveryOptions()
                    .addHeader("name", "atieh").addHeader("count", this.counter + ""));
            resultOutboundCount++;
        }
        this.counter = 0;
    }

}