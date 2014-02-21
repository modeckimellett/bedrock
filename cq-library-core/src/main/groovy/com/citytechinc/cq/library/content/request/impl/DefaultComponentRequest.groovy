/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.request.impl

import com.adobe.cq.sightly.WCMBindings
import com.citytechinc.cq.library.content.request.ComponentRequest
import com.citytechinc.cq.library.content.request.ComponentServletRequest
import com.day.cq.wcm.api.components.Component
import com.day.cq.wcm.api.components.ComponentContext
import com.day.cq.wcm.api.components.EditContext
import com.day.cq.wcm.api.designer.Design
import com.day.cq.wcm.api.designer.Designer
import com.day.cq.wcm.api.designer.Style
import com.day.cq.wcm.tags.DefineObjectsTag

import javax.script.Bindings
import javax.servlet.jsp.PageContext

final class DefaultComponentRequest implements ComponentRequest {

    @Delegate
    private final ComponentServletRequest componentServletRequest

    final Component component

    final ComponentContext componentContext

    final Design currentDesign

    final Style currentStyle

    final Designer designer

    final EditContext editContext

    DefaultComponentRequest(Bindings bindings) {
        componentServletRequest = DefaultComponentServletRequest.fromBindings(bindings)

        component = (Component) bindings.get(WCMBindings.COMPONENT)
        componentContext = (ComponentContext) bindings.get(WCMBindings.COMPONENT_CONTEXT)
        editContext = (EditContext) bindings.get(WCMBindings.EDIT_CONTEXT)
        designer = (Designer) bindings.get(WCMBindings.DESIGNER)
        currentDesign = (Design) bindings.get(WCMBindings.CURRENT_DESIGN)
        currentStyle = (Style) bindings.get(WCMBindings.CURRENT_STYLE)
    }

    DefaultComponentRequest(PageContext pageContext) {
        componentServletRequest = DefaultComponentServletRequest.fromPageContext(pageContext)

        component = (Component) pageContext.getAttribute(DefineObjectsTag.DEFAULT_COMPONENT_NAME)
        componentContext = (ComponentContext) pageContext.getAttribute(DefineObjectsTag
            .DEFAULT_COMPONENT_CONTEXT_NAME)
        editContext = (EditContext) pageContext.getAttribute(DefineObjectsTag
            .DEFAULT_EDIT_CONTEXT_NAME)
        designer = (Designer) pageContext.getAttribute(DefineObjectsTag.DEFAULT_DESIGNER_NAME)
        currentDesign = (Design) pageContext.getAttribute(DefineObjectsTag
            .DEFAULT_CURRENT_DESIGN_NAME)
        currentStyle = (Style) pageContext.getAttribute(DefineObjectsTag.DEFAULT_CURRENT_STYLE_NAME)
    }
}
