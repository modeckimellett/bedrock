/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.link.impl;

import com.citytechinc.cq.library.content.link.Link;
import com.citytechinc.cq.library.content.link.NavigationLink;

import java.util.List;
import java.util.Map;

public final class DefaultNavigationLink implements NavigationLink {

    private final Link link;

    private final boolean active;

    private final List<NavigationLink> children;

    public DefaultNavigationLink(final Link link, final boolean active, final List<NavigationLink> children) {
        this.link = link;
        this.active = active;
        this.children = children;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public List<NavigationLink> getChildren() {
        return children;
    }

    @Override
    public String getExtension() {
        return link.getExtension();
    }

    @Override
    public String getHref() {
        return link.getHref();
    }

    @Override
    public String getPath() {
        return link.getPath();
    }

    @Override
    public Map<String, String> getProperties() {
        return link.getProperties();
    }

    @Override
    public String getQueryString() {
        return link.getQueryString();
    }

    @Override
    public List<String> getSelectors() {
        return link.getSelectors();
    }

    @Override
    public String getTarget() {
        return link.getTarget();
    }

    @Override
    public String getTitle() {
        return link.getTitle();
    }

    @Override
    public boolean isEmpty() {
        return link.isEmpty();
    }

    @Override
    public boolean isExternal() {
        return link.isExternal();
    }
}
