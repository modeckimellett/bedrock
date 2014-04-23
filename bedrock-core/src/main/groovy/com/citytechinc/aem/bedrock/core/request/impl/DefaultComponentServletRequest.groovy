/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.core.request.impl

import com.citytechinc.aem.bedrock.api.request.ComponentResourceRequest
import com.citytechinc.aem.bedrock.api.request.ComponentServletRequest
import com.day.cq.wcm.api.WCMMode
import com.google.common.base.Function
import com.google.common.base.Optional
import com.google.common.collect.ImmutableList
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.request.RequestParameter
import org.apache.sling.api.resource.Resource

import javax.script.Bindings

import static com.day.cq.wcm.api.WCMMode.fromRequest
import static com.google.common.base.Preconditions.checkNotNull
import static org.apache.sling.api.scripting.SlingBindings.REQUEST
import static org.apache.sling.api.scripting.SlingBindings.RESOURCE
import static org.apache.sling.api.scripting.SlingBindings.RESPONSE

final class DefaultComponentServletRequest implements ComponentServletRequest {

    private static final def REQUEST_PARAMETER_TO_STRING = new Function<RequestParameter, String>() {
        @Override
        String apply(RequestParameter parameter) {
            parameter.string
        }
    }

    private static final def REQUEST_PARAMETERS_TO_LIST = new Function<RequestParameter[], List<String>>() {
        @Override
        List<String> apply(RequestParameter[] parameters) {
            ImmutableList.copyOf(parameters*.string)
        }
    }

    final SlingHttpServletRequest slingRequest

    final SlingHttpServletResponse slingResponse

    @Delegate
    private final ComponentResourceRequest delegate

    DefaultComponentServletRequest(Bindings bindings) {
        slingRequest = bindings.get(REQUEST) as SlingHttpServletRequest
        slingResponse = bindings.get(RESPONSE) as SlingHttpServletResponse

        delegate = new DefaultComponentResourceRequest(bindings.get(RESOURCE) as Resource)
    }

    DefaultComponentServletRequest(SlingHttpServletRequest slingRequest, SlingHttpServletResponse slingResponse) {
        this.slingRequest = slingRequest
        this.slingResponse = slingResponse

        delegate = new DefaultComponentResourceRequest(slingRequest.resource)
    }

    @Override
    Optional<String> getRequestParameter(String parameterName) {
        checkNotNull(parameterName)

        Optional.fromNullable(slingRequest.getRequestParameter(parameterName)).transform(REQUEST_PARAMETER_TO_STRING)
    }

    @Override
    String getRequestParameter(String parameterName, String defaultValue) {
        checkNotNull(parameterName)
        checkNotNull(defaultValue)

        def parameter = slingRequest.getRequestParameter(parameterName)

        parameter ? parameter.string : defaultValue
    }

    @Override
    Optional<List<String>> getRequestParameters(String parameterName) {
        checkNotNull(parameterName)

        Optional.fromNullable(slingRequest.getRequestParameters(parameterName)).transform(REQUEST_PARAMETERS_TO_LIST)
    }

    @Override
    List<String> getSelectors() {
        ImmutableList.copyOf(slingRequest.requestPathInfo.selectors)
    }

    @Override
    WCMMode getWCMMode() {
        fromRequest(slingRequest)
    }
}
