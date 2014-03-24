/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.bindings

import com.citytechinc.aem.bedrock.content.request.ComponentRequest
import com.citytechinc.aem.bedrock.content.request.ComponentRequestFactory

import javax.script.Bindings
import javax.servlet.jsp.PageContext

final class ComponentBindingsFactory {

    static ComponentBindings fromBindings(Bindings bindings) {
        def componentRequest = ComponentRequestFactory.fromBindings(bindings)

        new ComponentBindings(componentRequest)
    }

    static ComponentBindings fromPageContext(PageContext pageContext) {
        def componentRequest = ComponentRequestFactory.fromPageContext(pageContext)

        new ComponentBindings(componentRequest)
    }

    static ComponentBindings fromRequest(ComponentRequest componentRequest) {
        new ComponentBindings(componentRequest)
    }

    private ComponentBindingsFactory() {

    }
}
