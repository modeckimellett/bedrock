package com.citytechinc.aem.bedrock.core.node.impl;

import com.citytechinc.aem.bedrock.api.link.Link;
import com.citytechinc.aem.bedrock.api.link.builders.LinkBuilder;
import com.citytechinc.aem.bedrock.api.page.PageDecorator;
import com.citytechinc.aem.bedrock.api.page.PageManagerDecorator;
import com.citytechinc.aem.bedrock.core.link.builders.impl.DefaultLinkBuilder;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import static com.citytechinc.aem.bedrock.core.utils.PathUtils.isExternal;
import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractNode {

    protected final Resource resource;

    protected AbstractNode(final Resource resource) {
        this.resource = checkNotNull(resource);
    }

    protected Optional<Link> getLinkOptional(final Optional<String> pathOptional, final boolean strict,
        final boolean mapped) {
        return pathOptional.transform(new Function<String, Link>() {
            @Override
            public Link apply(final String path) {
                final ResourceResolver resourceResolver = resource.getResourceResolver();
                final String mappedPath = mapped ? resourceResolver.map(path) : path;

                final LinkBuilder builder = DefaultLinkBuilder.forPath(mappedPath);

                if (strict) {
                    builder.setExternal(isExternal(mappedPath, resourceResolver));
                }

                return builder.build();
            }
        });
    }

    protected Optional<PageDecorator> getPageOptional(final String path) {
        final Optional<PageDecorator> pageOptional;

        if (path.isEmpty()) {
            pageOptional = Optional.absent();
        } else {
            final PageDecorator page = resource.getResourceResolver().adaptTo(PageManagerDecorator.class).getPage(path);

            pageOptional = Optional.fromNullable(page);
        }

        return pageOptional;
    }
}
