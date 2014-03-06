/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.testing.specs

import com.citytechinc.aem.bedrock.adapter.BedrockAdapterFactory
import com.citytechinc.aem.bedrock.content.node.ComponentNode
import com.citytechinc.aem.bedrock.content.page.PageManagerDecorator
import com.citytechinc.aem.spock.specs.AbstractSlingRepositorySpec
import org.apache.sling.api.adapter.AdapterFactory
import spock.lang.Shared

/**
 * Spock specification for AEM testing.
 */
abstract class AbstractBedrockSpec extends AbstractSlingRepositorySpec {

    @Shared pageManager

    @Override
    Collection<AdapterFactory> addAdapterFactories() {
        [new BedrockAdapterFactory()]
    }

    def setupSpec() {
        pageManager = resourceResolver.adaptTo(PageManagerDecorator)
    }

    def getComponentNode(path) {
        resourceResolver.getResource(path).adaptTo(ComponentNode)
    }

    def getPage(path) {
        pageManager.getPage(path)
    }
}
