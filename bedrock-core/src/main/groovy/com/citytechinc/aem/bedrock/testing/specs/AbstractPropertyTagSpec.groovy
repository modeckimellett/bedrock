/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.testing.specs

import com.citytechinc.aem.bedrock.adapter.BedrockAdapterFactory
import com.citytechinc.aem.bedrock.bindings.ComponentBindings
import com.citytechinc.aem.bedrock.content.node.impl.DefaultComponentNode
import com.citytechinc.aem.spock.specs.tag.AbstractTagSpec
import org.apache.sling.api.adapter.AdapterFactory

/**
 * Spock specification for testing CQ component-based tag support classes.
 */
abstract class AbstractPropertyTagSpec extends AbstractTagSpec {

    @Override
    Collection<AdapterFactory> addAdapterFactories() {
        [new BedrockAdapterFactory()]
    }

    /**
     * Set a <code>ComponentNode</code> for the given path in the <code>PageContext</code> for the tag under test.
     *
     * @param path node path
     */
    void setupComponentNode(path) {
        def resource = resourceResolver.getResource(path)
        def componentNode = new DefaultComponentNode(resource)

        tag.pageContext.getAttribute(ComponentBindings.COMPONENT_NODE) >> componentNode
    }
}
