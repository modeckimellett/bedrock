/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.content.request.impl

import com.citytechinc.aem.bedrock.content.node.ComponentNode
import com.citytechinc.aem.bedrock.content.page.PageDecorator
import com.citytechinc.aem.bedrock.content.page.PageManagerDecorator
import com.citytechinc.aem.bedrock.content.request.ComponentResourceRequest
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ResourceUtil
import org.apache.sling.api.resource.ValueMap

import javax.jcr.Node
import javax.jcr.Session

final class DefaultComponentResourceRequest implements ComponentResourceRequest {

    final ComponentNode componentNode

    final Node currentNode

    final PageDecorator currentPage

    final PageManagerDecorator pageManager

    final ValueMap properties

    final Resource resource

    final ResourceResolver resourceResolver

    DefaultComponentResourceRequest(Resource resource) {
        this.resource = resource

        resourceResolver = resource.resourceResolver
        properties = ResourceUtil.getValueMap(resource)
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
