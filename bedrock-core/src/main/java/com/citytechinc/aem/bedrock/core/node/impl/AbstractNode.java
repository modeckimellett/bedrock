package com.citytechinc.aem.bedrock.core.node.impl;

import com.citytechinc.aem.bedrock.api.link.Link;
import com.citytechinc.aem.bedrock.api.page.PageDecorator;
import com.citytechinc.aem.bedrock.api.page.PageManagerDecorator;
import com.citytechinc.aem.bedrock.core.link.builders.impl.DefaultLinkBuilder;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractNode {

    protected final Resource resource;

    protected AbstractNode(final Resource resource) {
        checkNotNull(resource);

        this.resource = resource;
    }

    protected Optional<Link> getLinkOptional(final Optional<String> pathOptional, final boolean strict,
        final boolean mapped) {
        return pathOptional.transform(new Function<String, Link>() {
            @Override
            public Link apply(final String path) {
                final ResourceResolver resourceResolver = resource.getResourceResolver();
                final String mappedPath = mapped ? resourceResolver.map(path) : path;

                final Link link;

                if (strict) {
                    link = DefaultLinkBuilder.forPath(resourceResolver, mappedPath).build();
                } else {
                    link = DefaultLinkBuilder.forPath(mappedPath).build();
                }

                return link;
            }
        });
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
