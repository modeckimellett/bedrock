package com.citytechinc.aem.bedrock.core.link.impl;

import com.citytechinc.aem.bedrock.api.link.Link;
import com.citytechinc.aem.bedrock.core.link.builders.impl.DefaultLinkBuilder;
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
            return DefaultLinkBuilder.forPath(path).build();
        }
    };

    private LinkFunctions() {

    }
}
