/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.link.impl;

import com.citytechinc.cq.library.constants.Constants;
import com.citytechinc.cq.library.content.link.Link;
import com.citytechinc.cq.library.content.link.enums.LinkTarget;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class DefaultLink implements Link {

    public static final Link EMPTY = new DefaultLink();

    private final String path;

    private final String extension;

    private final String href;

    private final List<String> selectors;

    private final String queryString;

    private final boolean external;

    private final String target;

    private final String title;

    private final Map<String, String> properties;

    public DefaultLink(final String path, final String extension, final String href, final List<String> selectors,
        final String queryString, final boolean external, final String target, final String title,
        final Map<String, String> properties) {
        this.path = path;
        this.extension = extension;
        this.href = href;
        this.selectors = selectors;
        this.queryString = queryString;
        this.external = external;
        this.target = target;
        this.title = title;
        this.properties = properties;
    }

    private DefaultLink() {
        path = "";
        extension = Constants.EXTENSION_HTML;
        href = "";
        selectors = Collections.emptyList();
        queryString = "";
        external = true;
        target = LinkTarget.SELF.getTarget();
        title = "";
        properties = Collections.emptyMap();
    }

    public String getExtension() {
        return extension;
    }

    public String getHref() {
        return href;
    }

    public List<String> getSelectors() {
        return ImmutableList.copyOf(selectors);
    }

    public String getQueryString() {
        return queryString;
    }

    public boolean isExternal() {
        return external;
    }

    public String getTarget() {
        return target;
    }

    public String getTitle() {
        return title;
    }

    public Map<String, String> getProperties() {
        return ImmutableMap.copyOf(properties);
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean isEmpty() {
        return Strings.isNullOrEmpty(href);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(final Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
