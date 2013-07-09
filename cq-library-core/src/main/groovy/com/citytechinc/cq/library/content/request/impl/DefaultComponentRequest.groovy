/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.request.impl

import com.citytechinc.cq.library.content.request.ComponentRequest
import com.citytechinc.cq.library.content.request.ComponentServletRequest
import com.day.cq.wcm.api.components.Component
import com.day.cq.wcm.api.components.ComponentContext
import com.day.cq.wcm.api.components.EditContext
import com.day.cq.wcm.api.designer.Design
import com.day.cq.wcm.api.designer.Designer
import com.day.cq.wcm.api.designer.Style

import javax.servlet.jsp.PageContext

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_COMPONENT_CONTEXT_NAME
import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_COMPONENT_NAME
import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_CURRENT_DESIGN_NAME
import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_CURRENT_STYLE_NAME
import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_DESIGNER_NAME
import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_EDIT_CONTEXT_NAME

final class DefaultComponentRequest implements ComponentRequest {

    @Delegate
    private final ComponentServletRequest componentServletRequest

    final Component component

    final ComponentContext componentContext

    final Design currentDesign

    final Style currentStyle

    final Designer designer

    final EditContext editContext

    DefaultComponentRequest(PageContext pageContext) {
        componentServletRequest = new DefaultComponentServletRequest(pageContext)

        component = (Component) pageContext.getAttribute(DEFAULT_COMPONENT_NAME)
        componentContext = (ComponentContext) pageContext.getAttribute(DEFAULT_COMPONENT_CONTEXT_NAME)
        editContext = (EditContext) pageContext.getAttribute(DEFAULT_EDIT_CONTEXT_NAME)
        designer = (Designer) pageContext.getAttribute(DEFAULT_DESIGNER_NAME)
        currentDesign = (Design) pageContext.getAttribute(DEFAULT_CURRENT_DESIGN_NAME)
        currentStyle = (Style) pageContext.getAttribute(DEFAULT_CURRENT_STYLE_NAME)
    }
}
