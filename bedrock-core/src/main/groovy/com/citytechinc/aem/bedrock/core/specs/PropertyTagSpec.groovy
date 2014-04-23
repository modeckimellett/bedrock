package com.citytechinc.aem.bedrock.core.specs

import com.citytechinc.aem.bedrock.core.adapter.BedrockAdapterFactory
import com.citytechinc.aem.bedrock.core.bindings.ComponentBindings
import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.prosper.specs.JspTagSpec
import org.apache.sling.api.adapter.AdapterFactory

/**
 * Spock specification for testing CQ component-based tag support classes.
 */
abstract class PropertyTagSpec extends JspTagSpec {

    @Override
    Collection<AdapterFactory> addAdapterFactories() {
        [new BedrockAdapterFactory()]
    }

    /**
     * Set a <code>ComponentNode</code> for the given path in the <code>PageContext</code> for the tag under test.
     *
     * @param path node path
     */
    void setupComponentNode(String path) {
        def componentNode = resourceResolver.getResource(path).adaptTo(ComponentNode)

        tag.pageContext.getAttribute(ComponentBindings.COMPONENT_NODE) >> componentNode
    }
}
