package com.citytechinc.aem.bedrock.core.node.impl

import com.citytechinc.aem.bedrock.api.link.Link
import com.citytechinc.aem.bedrock.api.page.PageDecorator
import com.citytechinc.aem.bedrock.api.page.PageManagerDecorator
import com.citytechinc.aem.bedrock.core.link.builders.impl.DefaultLinkBuilder
import com.google.common.base.Function
import com.google.common.base.Optional
import org.apache.sling.api.resource.Resource

import static com.citytechinc.aem.bedrock.core.utils.PathUtils.isExternal
import static com.google.common.base.Preconditions.checkNotNull

abstract class AbstractNode {

    protected final Resource resourceInternal

    protected AbstractNode(Resource resource) {
        this.resourceInternal = checkNotNull(resource)
    }

    protected Optional<Link> getLinkOptional(Optional<String> pathOptional, boolean strict,
        boolean mapped) {
        return pathOptional.transform(new Function<String, Link>() {
            @Override
            Link apply(String path) {
                def resourceResolver = resourceInternal.resourceResolver
                def mappedPath = mapped ? resourceResolver.map(path) : path

                def builder = DefaultLinkBuilder.forPath(mappedPath)

                if (strict) {
                    builder.external = isExternal(mappedPath, resourceResolver)
                }

                builder.build()
            }
        })
    }

    protected Optional<PageDecorator> getPageOptional(String path) {
        def pageOptional

        if (path.isEmpty()) {
            pageOptional = Optional.absent()
        } else {
            def page = resourceInternal.resourceResolver.adaptTo(PageManagerDecorator).getPage(path)

            pageOptional = Optional.fromNullable(page)
        }

        pageOptional
    }
}
