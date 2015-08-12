package com.citytechinc.aem.bedrock.core.node.impl

import com.citytechinc.aem.bedrock.api.link.Link
import com.citytechinc.aem.bedrock.api.page.PageDecorator
import com.citytechinc.aem.bedrock.api.page.PageManagerDecorator
import com.citytechinc.aem.bedrock.core.link.builders.factory.LinkBuilderFactory
import com.citytechinc.aem.bedrock.core.utils.PathUtils
import com.google.common.base.Function
import com.google.common.base.Optional
import org.apache.sling.api.resource.Resource

import static com.google.common.base.Preconditions.checkNotNull

abstract class AbstractNode {

    protected final Resource resourceInternal

    protected AbstractNode(Resource resource) {
        this.resourceInternal = checkNotNull(resource)
    }

    protected <AdapterType> Optional<AdapterType> getAsTypeOptional(String path, Class<AdapterType> type) {
        def resource = path ? resourceInternal.resourceResolver.getResource(path) : null
        def result = (type == Resource ? resource : resource?.adaptTo(type)) as AdapterType

        Optional.fromNullable(result)
    }

    protected Optional<Link> getLinkOptional(Optional<String> pathOptional, boolean strict, boolean mapped) {
        return pathOptional.transform(new Function<String, Link>() {
            @Override
            Link apply(String path) {
                def resourceResolver = resourceInternal.resourceResolver
                def mappedPath = mapped ? resourceResolver.map(path) : path

                def builder = LinkBuilderFactory.forPath(mappedPath)

                if (strict) {
                    builder.external = PathUtils.isExternal(mappedPath, resourceResolver)
                }

                builder.build()
            }
        })
    }

    protected Optional<PageDecorator> getPageOptional(String path) {
        def pageOptional

        if (path) {
            def page = resourceInternal.resourceResolver.adaptTo(PageManagerDecorator).getPage(path)

            pageOptional = Optional.fromNullable(page)
        } else {
            pageOptional = Optional.absent()
        }

        pageOptional
    }
}
