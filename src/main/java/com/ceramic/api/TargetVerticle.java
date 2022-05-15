package com.ceramic.api;

import com.vx6.tools.MultipartStringMessage;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class TargetVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(TargetVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        MultipartStringMessage msm = new MultipartStringMessage(vertx, config().getJsonObject("multiChunksMessage"));
        vertx.eventBus().consumer("test05", msg -> {
           vertx.eventBus().request("test04", "Hey! give me then...", res->{
               Future<String> fut = msm.get(res.result());
               fut.onComplete(res1 -> {
                   System.out.println("TargetVerticle received(test04): " + res1.result().length());
                   System.out.println(res1.result());
                   //System.out.println(LZString.decompressFromUTF16(res1.result()));
               });
               fut.onFailure(res1 -> {
                   res1.getCause().printStackTrace();
               });
           });
        });
        /*vertx.eventBus().consumer("test04", msg -> {
            Future<String> fut = msm.get(msg);
            fut.onComplete(res -> {
                System.out.println("TargetVerticle received(test04): " + res.result().length());
                System.out.println(res.result());
                System.out.println(LZString.decompressFromUTF16(res.result()));
            });
            fut.onFailure(res -> {
                res.getCause().printStackTrace();
            });
        });*/
        /*vertx.eventBus().consumer("test02", msg -> {
            Future<String> fut = msm.get(msg);
            fut.onComplete(res -> {
                //byte[] data = ((String)((PromiseImpl) res).result()).getBytes();
                //ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
                String decompressedUTF16 = LZString.decompressFromUTF16(res.result());
                System.out.println("Compress.res.result().length(): " + res.result().length());
                System.out.println("Uncompressed.res.result().length(): " + decompressedUTF16.length());
            });
            fut.onFailure(res -> {
                System.out.println(res.toString());
            });
        });
        vertx.eventBus().consumer("test03", msg -> {
            Future<String> fut = msm.get(msg);
            fut.onComplete(res -> {
                System.out.println("Compress.res.result().length(): " + res.result().length());
            });
            fut.onFailure(res -> {
                System.out.println(res.toString());
            });
        });*/
        startPromise.complete();
    }
}
