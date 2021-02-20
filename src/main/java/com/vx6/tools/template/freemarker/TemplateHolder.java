package com.vx6.tools.template.freemarker;

import io.vertx.core.shareddata.Shareable;

public class TemplateHolder<T> implements Shareable {
    private final T template;
    private final String baseDir;

    public TemplateHolder(T template) {
        this(template, (String)null);
    }

    public TemplateHolder(T template, String baseDir) {
        this.template = template;
        this.baseDir = baseDir;
    }

    public T template() {
        return this.template;
    }

    public String baseDir() {
        return this.baseDir;
    }
}
