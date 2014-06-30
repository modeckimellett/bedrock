package com.citytechinc.aem.bedrock.core.bindings

import com.citytechinc.aem.bedrock.api.request.ComponentRequest
import com.citytechinc.aem.bedrock.core.request.impl.DefaultComponentRequest
import com.day.cq.wcm.api.WCMMode

import javax.script.Bindings
import javax.script.SimpleBindings
import javax.servlet.jsp.PageContext

import static com.adobe.cq.sightly.WCMBindings.CURRENT_PAGE
import static com.adobe.cq.sightly.WCMBindings.PAGE_MANAGER
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_BINDINGS_NAME

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

    private static final String PARAMETER_DEBUG = "debug"

    @Delegate
    private final Map<String, Object> map = [:]

    private final ComponentRequest componentRequest

    ComponentBindings(PageContext pageContext) {
        this(pageContext.getAttribute(DEFAULT_BINDINGS_NAME) as SimpleBindings)
    }

    ComponentBindings(Bindings bindings) {
        componentRequest = new DefaultComponentRequest(bindings)

        map.put(COMPONENT_REQUEST, componentRequest)
        map.put(COMPONENT_NODE, componentRequest.componentNode)

        // overrides
        map.put(CURRENT_PAGE, componentRequest.currentPage)
        map.put(PAGE_MANAGER, componentRequest.pageManager)

        // modes
        def mode = componentRequest.WCMMode

        map.put(IS_AUTHOR, mode != WCMMode.DISABLED)
        map.put(IS_PUBLISH, mode == WCMMode.DISABLED)
        map.put(IS_EDIT_MODE, mode == WCMMode.EDIT)
        map.put(IS_DESIGN_MODE, mode == WCMMode.DESIGN)
        map.put(IS_PREVIEW_MODE, mode == WCMMode.PREVIEW)
        map.put(IS_ANALYTICS_MODE, mode == WCMMode.ANALYTICS)
        map.put(IS_READ_ONLY_MODE, mode == WCMMode.READ_ONLY)

        def debug = componentRequest.getRequestParameter(PARAMETER_DEBUG).or(Boolean.FALSE.toString())

        map.put(IS_DEBUG, Boolean.valueOf(debug))
    }

    ComponentRequest getComponentRequest() {
        componentRequest
    }

    @Override
    def put(String key, value) {
        map.put(key, value)
    }
}
