package com.ceramic.api;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import com.ceramic.service.HTTPServer;

import javax.net.ssl.SSLServerSocketFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import static com.ceramic.service.HTTPServer.*;
//import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        try {
            File dir = new File(config().getString("location"));
            if (!dir.canRead())
                throw new FileNotFoundException(dir.getAbsolutePath());
            int port = config().containsKey("http-port") ? config().getInteger("http-port") : 80;
            // set up server
            for (File f : Arrays.asList(new File("/etc/mime.types"), new File(dir, ".mime.types")))
                if (f.exists())
                    addContentTypes(new FileInputStream(f));
            HTTPServer server = new HTTPServer(port);
            if (System.getProperty("javax.net.ssl.keyStore") != null) // enable SSL if configured
                server.setServerSocketFactory(SSLServerSocketFactory.getDefault());
            HTTPServer.VirtualHost host = server.getVirtualHost(null); // default host
            host.setAllowGeneratedIndex(true); // with directory index pages
            host.addContext("/", new HTTPServer.FileContextHandler(dir));
            File dirFreeboard = new File(config().getString("location").replace("ceramic", "freeboard"));
            if (!dirFreeboard.canRead())
                throw new FileNotFoundException(dirFreeboard.getAbsolutePath());
            host.addContext("/freeboard", new HTTPServer.FileContextHandler(dirFreeboard));
            File dirPublic = new File(config().getString("location").replace("ceramic", "public"));
            if (!dirPublic.canRead())
                throw new FileNotFoundException(dirPublic.getAbsolutePath());
            host.addContext("/public", new HTTPServer.FileContextHandler(dirPublic));
            host.addContext("/api/time", new HTTPServer.ContextHandler() {
                public int serve(HTTPServer.Request req, HTTPServer.Response resp) throws IOException {
                    long now = System.currentTimeMillis();
                    resp.getHeaders().add("Content-Type", "text/plain");
                    resp.send(200, String.format("%tF %<tT", now));
                    return 0;
                }
            });
            host.addContext("/parquet", new HTTPServer.ContextHandler() {
                public int serve(HTTPServer.Request req, HTTPServer.Response resp) throws IOException {
                    File file = new File(dir.getCanonicalFile(), "\\index.html").getCanonicalFile();
                    serveFileContent(file, req, resp);
                    return 0;
                }
            });
            host.addContext("/chart", new HTTPServer.ContextHandler() {
                public int serve(HTTPServer.Request req, HTTPServer.Response resp) throws IOException {
                    File file = new File(dir.getCanonicalFile(), "\\index.html").getCanonicalFile();
                    serveFileContent(file, req, resp);
                    return 0;
                }
            });
            host.addContext("/config.json", new HTTPServer.ContextHandler() {
                public int serve(HTTPServer.Request req, HTTPServer.Response resp) throws IOException {
                    resp.getHeaders().add("Content-Type", "application/json");
                    resp.send(200, config().getJsonObject("bus").toString());
                    return 0;
                }
            });
            /*host.addContext("/stt/form", new HTTPServer.ContextHandler() {
                public int serve(HTTPServer.Request req, HTTPServer.Response resp) throws IOException {
                    final String origin = req.getHeaders().get("Origin");
                    resp.getHeaders().add("Access-Control-Allow-Origin", "origin");
                    resp.getHeaders().add("Access-Control-Allow-Methods", "GET, PUT, POST, OPTIONS");
                    String reqHead = req.getHeaders().get("Access-Control-Request-Headers");
                    if (isNotEmpty(reqHead))
                        resp.getHeaders().add("Access-Control-Allow-Headers", reqHead);
                    resp.send(200, config().getJsonObject("bus").toString());
                    return 0;
                }
            }, "OPTIONS", "POST");*/
            server.start();
            System.out.println("HTTPServer is listening on port " + port);
            startPromise.complete();
        } catch (Exception e) {
            System.err.println("error: " + e);
            startPromise.fail(e);
        }
    }
}

