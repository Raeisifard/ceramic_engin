package com.vx6.tools.template.freemarker;

import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.web.common.WebEnvironment;
import io.vertx.ext.web.common.template.TemplateEngine;
import io.vertx.ext.web.common.template.impl.TemplateHolder;

import java.util.Objects;

public abstract class CachingTemplateEngine<T> implements TemplateEngine {
    private final LocalMap<String, TemplateHolder<T>> cache;
    protected String extension;

    protected CachingTemplateEngine(Vertx vertx) {
        if (!WebEnvironment.development()) {
            this.cache = vertx.sharedData().getLocalMap("__vertx.ceramic.template.cache");
        } else {
            this.cache = null;
        }
    }

    public TemplateHolder<T> getTemplate(String keyname) {
        return this.cache != null ? (TemplateHolder) this.cache.get(keyname) : null;
    }

    public TemplateHolder<T> putTemplate(String keyname, TemplateHolder<T> templateHolder) {
        return this.cache != null ? (TemplateHolder) this.cache.put(keyname, templateHolder) : null;
    }

    protected String adjustLocation(String location) {
        if (this.extension != null && !location.endsWith(this.extension)) {
            location = location + this.extension;
        }

        return location;
    }
}

