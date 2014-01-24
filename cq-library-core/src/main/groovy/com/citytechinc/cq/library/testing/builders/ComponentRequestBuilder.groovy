/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.testing.builders

import com.citytechinc.cq.groovy.testing.builders.RequestBuilder
import com.citytechinc.cq.groovy.testing.builders.ResponseBuilder
import com.citytechinc.cq.library.content.request.ComponentRequest
import com.citytechinc.cq.library.content.request.impl.DefaultComponentServletRequest
import com.citytechinc.cq.library.testing.mocks.MockComponentRequest
import com.day.cq.wcm.api.WCMMode

class ComponentRequestBuilder {

    @Delegate RequestBuilder requestBuilder

    @Delegate ResponseBuilder responseBuilder

    def mode

    ComponentRequestBuilder(resourceResolver) {
        requestBuilder = new RequestBuilder(resourceResolver)
        responseBuilder = new ResponseBuilder()
    }

    ComponentRequestBuilder(resourceResolver, path) {
        requestBuilder = new RequestBuilder(resourceResolver, path)
        responseBuilder = new ResponseBuilder()
    }

    void mode(WCMMode mode) {
        this.mode = mode
    }

    ComponentRequest build() {
        build(null)
    }

    ComponentRequest build(Closure closure) {
        if (closure) {
            closure.delegate = this
            closure.resolveStrategy = Closure.DELEGATE_ONLY
            closure()
        }

        def slingRequest = requestBuilder.build()
        def slingResponse = responseBuilder.build()

        if (mode) {
            mode.toRequest(slingRequest)
        }

        def componentServletRequest = new DefaultComponentServletRequest(slingRequest, slingResponse)

        new MockComponentRequest(componentServletRequest)
    }
}
