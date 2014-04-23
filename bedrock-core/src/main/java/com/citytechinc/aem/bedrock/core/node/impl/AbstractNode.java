/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.core.node.impl;

import com.citytechinc.aem.bedrock.api.link.Link;
import com.citytechinc.aem.bedrock.core.link.builders.impl.DefaultLinkBuilder;
import com.citytechinc.aem.bedrock.api.page.PageDecorator;
import com.citytechinc.aem.bedrock.api.page.PageManagerDecorator;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import static com.citytechinc.aem.bedrock.core.link.impl.LinkFunctions.PATH_TO_LINK;
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

        final Function<String, Link> linkFunction = getLinkFunction(strict);
        final Function<String, String> mapFunction = new Function<String, String>() {
            @Override
            public String apply(final String path) {
                return mapped ? resourceResolver.map(path) : path;
            }
        };

        return pathOptional.transform(mapFunction).transform(linkFunction);
    }

    private Function<String, Link> getLinkFunction(final boolean strict) {
        final ResourceResolver resourceResolver = resource.getResourceResolver();

        final Function<String, Link> toLink;

        if (strict) {
            toLink = new Function<String, Link>() {
                @Override
                public Link apply(final String path) {
                    return DefaultLinkBuilder.forPath(resourceResolver, path).build();
                }
            };
        } else {
            toLink = PATH_TO_LINK;
        }

        return toLink;
    }

    protected Optional<PageDecorator> getPageOptional(final String path) {
        final Optional<PageDecorator> pageOptional;

        if (path.isEmpty()) {
            pageOptional = Optional.absent();
        } else {
            pageOptional = resource.getResourceResolver().adaptTo(PageManagerDecorator.class).getPageOptional(path);
        }

        return pageOptional;
    }
}
