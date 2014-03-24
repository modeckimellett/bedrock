/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.testing.specs

import com.citytechinc.aem.bedrock.bindings.ComponentBindingsFactory
import com.citytechinc.aem.bedrock.components.AbstractSightlyComponent
import com.citytechinc.aem.bedrock.content.request.ComponentRequest

import javax.script.Bindings

/**
 * Base class for testing Sightly components.
 */
abstract class SightlyComponentSpec extends ComponentSpec {

    /**
     * Instantiate and initialize a Sightly component to be tested.
     *
     * @param request component request built from <code>ComponentRequestBuilder</code>
     * @param type type of component
     * @return component initialized from mocked <code>ComponentRequest</code>
     */
    public <T extends AbstractSightlyComponent> T getComponent(ComponentRequest request, Class<T> type) {
        def bindings = ComponentBindingsFactory.fromRequest(request)

        def instance = type.newInstance()
        def method = type.getMethod("init", Bindings)

        method.invoke(instance, bindings)

        instance
    }
}