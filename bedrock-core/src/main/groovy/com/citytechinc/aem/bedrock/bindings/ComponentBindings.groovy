/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.bindings

import com.citytechinc.aem.bedrock.content.request.ComponentRequest
import com.day.cq.wcm.api.WCMMode

import javax.script.Bindings

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

    private @Delegate Map<String, Object> map = [:]

    private final def componentRequest

    public ComponentBindings(ComponentRequest componentRequest) {
        this.componentRequest = componentRequest

        def mode = componentRequest.getWCMMode()

        map.put(IS_AUTHOR, mode != WCMMode.DISABLED)
        map.put(IS_PUBLISH, mode == WCMMode.DISABLED)
        map.put(IS_EDIT_MODE, mode == WCMMode.EDIT)
        map.put(IS_DESIGN_MODE, mode == WCMMode.DESIGN)
        map.put(IS_PREVIEW_MODE, mode == WCMMode.PREVIEW)
        map.put(IS_ANALYTICS_MODE, mode == WCMMode.ANALYTICS)
        map.put(IS_READ_ONLY_MODE, mode == WCMMode.READ_ONLY)

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
