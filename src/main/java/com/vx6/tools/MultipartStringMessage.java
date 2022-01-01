package com.vx6.tools;

import com.github.diogoduailibe.lzstring4j.LZString;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import io.vertx.core.*;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MultipartStringMessage {
    private Vertx vertx;
    private final JsonObject cnf;

    public MultipartStringMessage(Vertx vertx, JsonObject cnf) {
        this.vertx = vertx;
        this.cnf = cnf;
    }

    /*public Future<String> get(Message<String> msg) {
        Promise<String> promise = Promise.promise();
        vertx.executeBlocking(pro -> {
            StringBuilder stringBuilder = new StringBuilder();
            getAllChunks(msg, stringBuilder, pro);
        }, res -> {
            if (res.succeeded())
                promise.complete((String) res.result());
            else
                promise.fail(res.cause());
        });
        return promise.future();
    }*/

    public Future<String> get(Message<Object> msg) {
        Promise<String> promise = Promise.promise();
        vertx.executeBlocking(pro -> {
            StringBuilder stringBuilder = new StringBuilder();
            AtomicInteger partCount = new AtomicInteger(0);
            getAllChunks(msg, stringBuilder, pro, partCount);
        }, res -> {
            if (res.succeeded())
                promise.complete((String) res.result());
            else
                promise.fail(res.cause());
        });
        return promise.future();
    }

    private void getAllChunks(Message msg, StringBuilder stringBuilder, Promise pro, AtomicInteger partCount) {

        msg.replyAndRequest(this.cnf, (Handler<AsyncResult<Message<String>>>) res1 -> {
            if (res1.succeeded()) {
                stringBuilder.append(res1.result().body());
                partCount.getAndIncrement();
                if (Integer.parseInt(res1.result().headers().get("parts")) > Integer.parseInt(res1.result().headers().get("part")))
                    getAllChunks(res1.result(), stringBuilder, pro, partCount);
                else {
                    res1.result().reply("I get all parts, successfully.");
                    //System.out.println("partCount: " + partCount.get() + "Length: " + stringBuilder.length());
                    pro.complete(this.cnf.getBoolean("compression") ? LZString.decompressFromUTF16(stringBuilder.toString()) : stringBuilder.toString());
                }
            } else {
                pro.fail(res1.cause());
            }
        });
    }

    public Future<String> send(Message<Object> msg, String str) {
        Promise<String> promise = Promise.promise();
        vertx.executeBlocking(pro -> {
            //StringBuilder stringBuilder = new StringBuilder();
            AtomicInteger partCount = new AtomicInteger(0);
            sendAllChunks(msg, Lists.newArrayList(Splitter.fixedLength(cnf.getInteger("chunkLength"))
                    .split(this.cnf.getBoolean("compression") ? LZString.compressToUTF16(str) : str)), partCount);
        }, res -> {
            if (res.succeeded())
                promise.complete((String) res.result());
            else
                promise.fail(res.cause());
        });
        return promise.future();
    }

    public Future<String> send(Message<Object> msg, String str, GraphProfile gp) {
        Promise<String> promise = Promise.promise();
        vertx.executeBlocking(pro -> {
            //StringBuilder stringBuilder = new StringBuilder();
            AtomicInteger partCount = new AtomicInteger(0);
            sendAllChunks(msg, Lists.newArrayList(Splitter.fixedLength(cnf.getInteger("chunkLength"))
                    .split(this.cnf.getBoolean("compression") ? LZString.compressToUTF16(str) : str)), gp, partCount);
        }, res -> {
            if (res.succeeded())
                promise.complete((String) res.result());
            else
                promise.fail(res.cause());
        });
        return promise.future();
    }

    private void sendAllChunks(Message msg, List<String> list, AtomicInteger pc) {
        if (list.size() > (pc.get() + 1)) {
            msg.replyAndRequest(list.get(pc.get()), new DeliveryOptions()
                            .addHeader("parts", list.size() + "")
                            .addHeader("part", pc.incrementAndGet() + ""),
                    (Handler<AsyncResult<Message<String>>>) res -> {
                        if (res.succeeded()) {
                            //System.out.println("pc: " + pc.get());
                            sendAllChunks(res.result(), list, pc);
                        } else {
                            res.cause().printStackTrace();
                            //System.out.println("There is an error on sending chunks: " + res.cause());
                        }
                    });
        } else if (list.size() == (pc.get() + 1)) {
            msg.reply(list.get(pc.get()), new DeliveryOptions()
                    .addHeader("parts", list.size() + "")
                    .addHeader("part", pc.incrementAndGet() + ""));
        }
    }

    private void sendAllChunks(Message msg, List<String> list, GraphProfile gp, AtomicInteger pc) {
        if (list.size() > (pc.get() + 1)) {
            msg.replyAndRequest(list.get(pc.get()), new DeliveryOptions()
                            .setHeaders(gp.toSimpleMultiMap())
                            .addHeader("parts", list.size() + "")
                            .addHeader("part", pc.incrementAndGet() + ""),
                    (Handler<AsyncResult<Message<String>>>) res -> {
                        if (res.succeeded()) {
                            //System.out.println("pc: " + pc.get());
                            sendAllChunks(res.result(), list, gp, pc);
                        } else {
                            res.cause().printStackTrace();
                            //System.out.println("There is an error on sending chunks: " + res.cause());
                        }
                    });
        } else if (list.size() == (pc.get() + 1)) {
            msg.reply(list.get(pc.get()), new DeliveryOptions()
                    .setHeaders(gp.toSimpleMultiMap())
                    .addHeader("parts", list.size() + "")
                    .addHeader("part", pc.incrementAndGet() + ""));
        }
    }

    /*private void getAllChunks(Message<String> msg, StringBuilder stringBuilder, Promise pro) {
        msg.replyAndRequest("part", (Handler<AsyncResult<Message<String>>>) res1 -> {
            if (res1.succeeded()) {
                stringBuilder.append(res1.result().body());
                if (Integer.parseInt(res1.result().headers().get("parts")) > Integer.parseInt(res1.result().headers().get("part")))
                    getAllChunks(res1.result(), stringBuilder, pro);
                else
                    pro.complete(stringBuilder.toString());
            } else {
                pro.fail(res1.cause());
            }
        });
    }*/
}
