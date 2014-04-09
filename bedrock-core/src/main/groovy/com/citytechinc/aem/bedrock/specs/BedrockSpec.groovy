/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.specs

import com.citytechinc.aem.bedrock.adapter.BedrockAdapterFactory
import com.citytechinc.aem.bedrock.content.node.ComponentNode
import com.citytechinc.aem.bedrock.content.page.PageDecorator
import com.citytechinc.aem.bedrock.content.page.PageManagerDecorator
import com.citytechinc.aem.prosper.specs.ProsperSpec
import org.apache.sling.api.adapter.AdapterFactory
import spock.lang.Shared

/**
 * Spock specification for AEM testing.
 */
abstract class BedrockSpec extends ProsperSpec {

    @Shared pageManagerDecorator

    @Override
    Collection<AdapterFactory> addAdapterFactories() {
        [new BedrockAdapterFactory()]
    }

    def setupSpec() {
        pageManagerDecorator = resourceResolver.adaptTo(PageManagerDecorator)
    }

    ComponentNode getComponentNode(String path) {
        resourceResolver.getResource(path).adaptTo(ComponentNode)
    }

    PageDecorator getPage(String path) {
        pageManagerDecorator.getPage(path)
    }

    @Override
    PageManagerDecorator getPageManager() {
        pageManagerDecorator
    }
}
