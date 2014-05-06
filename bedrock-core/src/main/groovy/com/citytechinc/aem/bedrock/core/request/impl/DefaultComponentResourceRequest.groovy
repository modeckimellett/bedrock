package com.citytechinc.aem.bedrock.core.request.impl

import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.api.page.PageDecorator
import com.citytechinc.aem.bedrock.api.page.PageManagerDecorator
import com.citytechinc.aem.bedrock.api.request.ComponentResourceRequest
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ValueMap

import javax.jcr.Node
import javax.jcr.Session

import static com.google.common.base.Preconditions.checkNotNull

final class DefaultComponentResourceRequest implements ComponentResourceRequest {

    final ComponentNode componentNode

    final Node currentNode

    final PageDecorator currentPage

    final PageManagerDecorator pageManager

    final ValueMap properties

    final Resource resource

    final ResourceResolver resourceResolver

    DefaultComponentResourceRequest(Resource resource) {
        this.resource = checkNotNull(resource)

        resourceResolver = resource.resourceResolver
        properties = resource.valueMap
        currentNode = resource.adaptTo(Node)
        componentNode = resource.adaptTo(ComponentNode)
        pageManager = resourceResolver.adaptTo(PageManagerDecorator)
        currentPage = pageManager.getContainingPage(resource)
    }

    @Override
    Session getSession() {
        resourceResolver.adaptTo(Session)
    }
}
