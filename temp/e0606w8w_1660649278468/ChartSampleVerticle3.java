import com.vx6.master.MasterVerticle;
import io.vertx.core.Promise;

public class ChartSampleVerticle3 extends MasterVerticle {

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        sendChart(this.setting, "Throughput Statistic");
        initPromise.complete();
    }
}


