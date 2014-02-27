/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.testing.specs

import com.citytechinc.aem.bedrock.testing.builders.ComponentRequestBuilder

/**
 * Spock specification for testing CQ components with builder for creating <code>ComponentRequest</code> instances.
 */
abstract class AbstractComponentSpec extends AbstractBedrockSpec {

    /**
     * Get a component request builder for the current session.
     *
     * @return builder
     */
    ComponentRequestBuilder getComponentRequestBuilder() {
        new ComponentRequestBuilder(resourceResolver)
    }

    /**
     * Get a component request builder for the current session.
     *
     * @param path content path for request
     * @return builder
     */
    ComponentRequestBuilder getComponentRequestBuilder(String path) {
        new ComponentRequestBuilder(resourceResolver, path)
    }
}
