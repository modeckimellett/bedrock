/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.node.impl;

import com.citytechinc.cq.library.content.link.Link;
import com.citytechinc.cq.library.content.link.builders.LinkBuilder;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import static com.citytechinc.cq.library.content.link.impl.LinkFunctions.PATH_TO_LINK;
import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractNode {

    protected final Resource resource;

    protected AbstractNode(final Resource resource) {
        checkNotNull(resource);

        this.resource = resource;
    }

    protected Optional<Link> getLinkOptional(final Optional<String> pathOptional, final boolean strict,
        final boolean mapped) {
        final ResourceResolver resourceResolver = resource.getResourceResolver();

        final Function<String, Link> toLink;

        if (strict) {
            toLink = new Function<String, Link>() {
                @Override
                public Link apply(final String path) {
                    return LinkBuilder.forPath(resourceResolver, path).build();
                }
            };
        } else {
            toLink = PATH_TO_LINK;
        }

        return pathOptional.transform(new Function<String, String>() {
            @Override
            public String apply(final String path) {
                return mapped ? resourceResolver.map(path) : path;
            }
        }).transform(toLink);
    }
}
