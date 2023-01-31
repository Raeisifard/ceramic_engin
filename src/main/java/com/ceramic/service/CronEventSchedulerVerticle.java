package com.ceramic.service;

import com.ceramic.api.BarVerticle;
import com.diabolicallabs.vertx.cron.CronObservable;
import io.reactivex.Scheduler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.shareddata.LocalMap;
import io.vertx.reactivex.core.shareddata.SharedData;

import java.util.Arrays;
import java.util.TimeZone;
import java.util.UUID;

public class CronEventSchedulerVerticle extends AbstractVerticle {
    private static final Logger log = LoggerFactory.getLogger(CronEventSchedulerVerticle.class);
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public CronEventSchedulerVerticle() {
    }

    public void start(Promise<Void> startPromise) throws Exception {
        EventBus eb = this.vertx.eventBus();
        String addressBase = this.config().getString("address_base", "cron");
        String create_address = addressBase + ".schedule";
        String cancel_address = addressBase + ".cancel";
        eb.consumer(cancel_address, (handler) -> {
            String id = (String) handler.body();
            if (id != null) {
                SharedData sd = this.vertx.sharedData();
                sd.getLocalMap(addressBase + "cron.ids").remove(id);
            }
            handler.reply(null);
        });
        eb.consumer(create_address, (handler) -> {
            if (!(handler.body() instanceof JsonObject)) {
                throw new IllegalArgumentException("Message must be a JSON object");
            } else {
                JsonObject message = (JsonObject) handler.body();

                String action;
                try {
                    if (!message.containsKey("cron_expression")) {
                        throw new IllegalArgumentException("Message must contain cron_expression");
                    }

                    if (!message.containsKey("address")) {
                        throw new IllegalArgumentException("Message must contain the address to schedule");
                    }

                    if (message.containsKey("timezone_name")) {
                        Boolean noneMatch = Arrays.stream(TimeZone.getAvailableIDs()).noneMatch((available) -> {
                            return available.equals(message.getString("timezone_name"));
                        });
                        if (noneMatch) {
                            throw new IllegalArgumentException("timezone_name " + message.getString("timezone_name") + " is invalid");
                        }
                    }

                    if (message.containsKey("action")) {
                        action = message.getString("action");
                        if (!action.equals("send") && !action.equals("publish")) {
                            throw new IllegalArgumentException("action must be 'send' or 'publish'");
                        }
                    }
                } catch (IllegalArgumentException var15) {
                    handler.fail(-1, var15.getMessage());
                    return;
                }

                action = message.getString("cron_expression");
                String timezoneName = message.getString("timezone_name");
                String scheduledAddress = message.getString("address");
                Object scheduledMessage = message.getValue("message");
                String actionx = message.getString("action", "send");
                String resultAddress = message.getString("result_address");
                SharedData sd = this.vertx.sharedData();
                String id = UUID.randomUUID().toString();
                LocalMap<String, JsonObject> map = sd.getLocalMap(addressBase + "cron.ids");
                map.put(id, message);
                Scheduler scheduler = RxHelper.scheduler(this.vertx);
                CronObservable.cronspec(scheduler, action, timezoneName).takeWhile((timestamped) -> {
                    return map.get(id) != null;
                }).subscribe((timestamped) -> {
                    if (actionx.equals("send")) {
                        eb.request(scheduledAddress, scheduledMessage, (scheduledAddressHandler) -> {
                            if (resultAddress != null) {
                                if (scheduledAddressHandler.succeeded()) {
                                    eb.request(resultAddress, scheduledAddressHandler.result().body());
                                } else if (scheduledAddressHandler.failed()) {
                                    this.logger.error("Message to " + resultAddress + " failed.", scheduledAddressHandler.cause());
                                }
                            }

                        });
                    } else {
                        eb.publish(scheduledAddress, scheduledMessage);
                    }

                }, (fault) -> {
                    //this.logger.error("Unable to process cronspec " + action + " for address " + scheduledAddress, fault);
                    handler.fail(-1, fault.getMessage());
                });
                handler.reply(id);
            }
        });
        log.info("Starting verticle {" + this + "}");
        startPromise.complete();
    }
}
