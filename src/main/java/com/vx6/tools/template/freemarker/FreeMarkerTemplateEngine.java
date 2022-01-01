package com.vx6.tools.template.freemarker;

import freemarker.cache.NullCacheStorage;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.common.template.impl.TemplateHolder;

import java.io.*;
import java.util.Locale;
import java.util.Map;

public abstract class FreeMarkerTemplateEngine extends CachingTemplateEngine<Template> {
    private final Configuration config;

    public FreeMarkerTemplateEngine(Vertx vertx) throws IOException {
        super(vertx);
        /* ------------------------------------------------------------------------ */
        /* You should do this ONLY ONCE in the whole application life-cycle:        */

        /* Create and adjust the configuration singleton */
        this.config = new Configuration(Configuration.VERSION_2_3_29);
        //this.config.setDirectoryForTemplateLoading(new File("/templates"));
        // Recommended settings for new projects:
        this.config.setDefaultEncoding("UTF-8");
        this.config.setIncompatibleImprovements(new Version(2, 3, 20));
        this.config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        this.config.setLogTemplateExceptions(false);
        this.config.setWrapUncheckedExceptions(true);
        this.config.setFallbackOnNullLoopVariable(false);
        /* ------------------------------------------------------------------------ */
        this.config.setCacheStorage(new NullCacheStorage());
    }

    @Override
    public Configuration unwrap() {
        return this.config;
    }

    public void render(Map<String, Object> context, String templateName, String templateStr, Handler<AsyncResult<Buffer>> handler) {
        try {
            TemplateHolder<Template> template = getRegister(context, templateName);
            if (!templateStr.trim().isEmpty())
                register(context, templateName, templateStr);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Throwable er = null;
            try {
                (template.template()).process(context, new OutputStreamWriter(baos));
                handler.handle(Future.succeededFuture(Buffer.buffer(baos.toByteArray())));
            } catch (Throwable e) {
                er = e;
                throw e;
            } finally {
                if (baos != null) {
                    if (er != null) {
                        try {
                            baos.close();
                        } catch (Throwable var19) {
                            er.addSuppressed(var19);
                        }
                    } else {
                        baos.close();
                    }
                }
            }
        } catch (Exception var23) {
            handler.handle(Future.failedFuture(var23));
        }
    }

    public void register(Map<String, Object> context, String templateName, String templateStr) throws Exception {
        TemplateHolder<Template> template;
        synchronized (this) {
            template = new TemplateHolder(new Template(templateName, new StringReader(templateStr), this.config));
        }
        Locale locale = context.containsKey("lang") ? Locale.forLanguageTag((String) context.get("lang")) : Locale.getDefault();
        String src = this.adjustLocation(templateName);
        String key = src + "_" + locale.toLanguageTag();
        this.putTemplate(key, template);
    }

    public TemplateHolder<Template> getRegister(Map<String, Object> context, String templateName) {
        Locale locale = context.containsKey("lang") ? Locale.forLanguageTag((String) context.get("lang")) : Locale.getDefault();
        String src = this.adjustLocation(templateName);
        String key = src + "_" + locale.toLanguageTag();
        return this.getRegister(key);
    }

    public TemplateHolder<Template> getRegister(String key) {
        return this.getTemplate(key);
    }

    @Override
    public void render(JsonObject context, String templateName, Handler<AsyncResult<Buffer>> handler) {
        this.render(context.getMap(), templateName, handler);
    }

    @Override
    public Future<Buffer> render(JsonObject context, String templateFileName) {
        return null;
    }

    @Override
    public void render(Map<String, Object> map, String s, Handler<AsyncResult<Buffer>> handler) {
        this.render(map, s, "", handler);
    }

    @Override
    public Future<Buffer> render(Map<String, Object> context, String templateFileName) {
        return null;
    }
}
