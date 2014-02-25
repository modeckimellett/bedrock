/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.binding

import com.citytechinc.cq.library.content.request.ComponentRequest
import com.citytechinc.cq.library.content.request.impl.DefaultComponentRequest
import com.day.cq.wcm.api.WCMMode

import javax.script.Bindings
import javax.servlet.jsp.PageContext

final class ComponentBindings implements Bindings {

    public static final String COMPONENT_REQUEST = "componentRequest"

    public static final String COMPONENT_NODE = "componentNode"

    public static final String IS_ANALYTICS_MODE = "isAnalyticsMode"

    public static final String IS_AUTHOR = "isAuthor"

    public static final String IS_EDIT_MODE = "isEditMode"

    public static final String IS_DESIGN_MODE = "isDesignMode"

    public static final String IS_PREVIEW_MODE = "isPreviewMode"

    public static final String IS_READ_ONLY_MODE = "isReadOnlyMode"

    public static final String IS_PUBLISH = "isPublish"

    public static final String IS_DEBUG = "isDebug"

    static final String PARAMETER_DEBUG = "debug"

    static ComponentBindings fromBindings(Bindings bindings) {
        def componentRequest = new DefaultComponentRequest(bindings)

        new ComponentBindings(componentRequest)
    }

    static ComponentBindings fromPageContext(PageContext pageContext) {
        def componentRequest = new DefaultComponentRequest(pageContext)

        new ComponentBindings(componentRequest)
    }

    private @Delegate Map<String, Object> map = [:]

    private final def componentRequest

    private ComponentBindings(ComponentRequest componentRequest) {
        this.componentRequest = componentRequest

        def mode = componentRequest.getWCMMode()

        map.put(IS_AUTHOR, !mode.equals(WCMMode.DISABLED))
        map.put(IS_PUBLISH, mode.equals(WCMMode.DISABLED))
        map.put(IS_EDIT_MODE, mode.equals(WCMMode.EDIT))
        map.put(IS_DESIGN_MODE, mode.equals(WCMMode.DESIGN))
        map.put(IS_PREVIEW_MODE, mode.equals(WCMMode.PREVIEW))
        map.put(IS_ANALYTICS_MODE, mode.equals(WCMMode.ANALYTICS))
        map.put(IS_READ_ONLY_MODE, mode.equals(WCMMode.READ_ONLY))

        def debug = componentRequest.getRequestParameter(PARAMETER_DEBUG).or(Boolean.FALSE.toString())

        map.put(IS_DEBUG, Boolean.valueOf(debug))
        map.put(COMPONENT_REQUEST, componentRequest)
        map.put(COMPONENT_NODE, componentRequest.componentNode)
    }

    ComponentRequest getComponentRequest() {
        componentRequest
    }

    @Override
    def put(String key, value) {
        map.put(key, value)
    }
}
