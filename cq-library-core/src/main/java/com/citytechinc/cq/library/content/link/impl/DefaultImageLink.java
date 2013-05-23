/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.link.impl;

import com.citytechinc.cq.library.content.link.ImageLink;
import com.citytechinc.cq.library.content.link.Link;

import java.util.List;
import java.util.Map;

public final class DefaultImageLink implements ImageLink {

    private final Link link;

    private final String imageSrc;

    public DefaultImageLink(final Link link, final String imageSrc) {
        this.link = link;
        this.imageSrc = imageSrc;
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

    @Override
    public String getImageSrc() {
        return imageSrc;
    }
}
