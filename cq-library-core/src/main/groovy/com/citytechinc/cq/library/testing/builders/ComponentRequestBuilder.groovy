/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.testing.builders

import com.citytechinc.cq.groovy.testing.builders.RequestBuilder
import com.citytechinc.cq.groovy.testing.builders.ResponseBuilder
import com.citytechinc.cq.library.content.request.impl.DefaultComponentServletRequest
import com.citytechinc.cq.library.testing.mocks.MockComponentRequest

class ComponentRequestBuilder {

    @Delegate RequestBuilder requestBuilder

    @Delegate ResponseBuilder responseBuilder

    ComponentRequestBuilder(resourceResolver) {
        requestBuilder = new RequestBuilder(resourceResolver)
        responseBuilder = new ResponseBuilder()
    }

    def build(Closure closure) {
        closure.delegate = this
        closure.resolveStrategy = Closure.DELEGATE_ONLY
        closure()

        def slingRequest = requestBuilder.build()
        def slingResponse = responseBuilder.build()

        def componentServletRequest = new DefaultComponentServletRequest(slingRequest, slingResponse)

        new MockComponentRequest(componentServletRequest)
    }
}
