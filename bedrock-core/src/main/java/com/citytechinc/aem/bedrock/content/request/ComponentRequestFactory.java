/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.content.request;

import com.citytechinc.aem.bedrock.content.request.impl.DefaultComponentRequest;
import com.citytechinc.aem.bedrock.content.request.impl.DefaultComponentServletRequest;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.scripting.SlingBindings;

import javax.script.Bindings;
import javax.servlet.jsp.PageContext;

import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_REQUEST_NAME;
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_RESPONSE_NAME;

public final class ComponentRequestFactory {

    public static ComponentRequest fromBindings(final Bindings bindings) {
        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) bindings.get(SlingBindings.REQUEST);
        final SlingHttpServletResponse slingResponse = (SlingHttpServletResponse) bindings.get(SlingBindings.RESPONSE);

        final ComponentServletRequest componentServletRequest = new DefaultComponentServletRequest(slingRequest,
            slingResponse);

        return new DefaultComponentRequest(componentServletRequest, bindings);
    }

    public static ComponentRequest fromPageContext(final PageContext pageContext) {
        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) pageContext.getAttribute(
            DEFAULT_REQUEST_NAME);
        final SlingHttpServletResponse slingResponse = (SlingHttpServletResponse) pageContext.getAttribute(
            DEFAULT_RESPONSE_NAME);

        final ComponentServletRequest componentServletRequest = new DefaultComponentServletRequest(slingRequest,
            slingResponse);

        return new DefaultComponentRequest(componentServletRequest, pageContext);
    }

    private ComponentRequestFactory() {

    }
}
