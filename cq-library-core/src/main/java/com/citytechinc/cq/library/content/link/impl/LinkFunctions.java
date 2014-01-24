/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.link.impl;

import com.citytechinc.cq.library.content.link.Link;
import com.citytechinc.cq.library.content.link.builders.LinkBuilder;
import com.google.common.base.Function;

public final class LinkFunctions {

    public static final Function<Link, String> LINK_TO_HREF = new Function<Link, String>() {
        @Override
        public String apply(final Link link) {
            return link.getHref();
        }
    };

    public static final Function<String, Link> PATH_TO_LINK = new Function<String, Link>() {
        @Override
        public Link apply(final String path) {
            return LinkBuilder.forPath(path).build();
        }
    };

    private LinkFunctions() {

    }
}
