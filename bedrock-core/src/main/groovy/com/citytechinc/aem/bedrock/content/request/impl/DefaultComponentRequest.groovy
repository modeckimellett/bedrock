/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.content.request.impl

import com.citytechinc.aem.bedrock.content.request.ComponentRequest
import com.citytechinc.aem.bedrock.content.request.ComponentServletRequest
import com.day.cq.wcm.api.components.Component
import com.day.cq.wcm.api.components.ComponentContext
import com.day.cq.wcm.api.components.EditContext
import com.day.cq.wcm.api.designer.Design
import com.day.cq.wcm.api.designer.Designer
import com.day.cq.wcm.api.designer.Style

import javax.script.Bindings
import javax.servlet.jsp.PageContext

import static com.adobe.cq.sightly.WCMBindings.COMPONENT
import static com.adobe.cq.sightly.WCMBindings.COMPONENT_CONTEXT
import static com.adobe.cq.sightly.WCMBindings.CURRENT_DESIGN
import static com.adobe.cq.sightly.WCMBindings.CURRENT_STYLE
import static com.adobe.cq.sightly.WCMBindings.DESIGNER
import static com.adobe.cq.sightly.WCMBindings.EDIT_CONTEXT
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

    DefaultComponentRequest(Bindings bindings) {
        componentServletRequest = DefaultComponentServletRequest.fromBindings(bindings)

        component = (Component) bindings.get(COMPONENT)
        componentContext = (ComponentContext) bindings.get(COMPONENT_CONTEXT)
        editContext = (EditContext) bindings.get(EDIT_CONTEXT)
        designer = (Designer) bindings.get(DESIGNER)
        currentDesign = (Design) bindings.get(CURRENT_DESIGN)
        currentStyle = (Style) bindings.get(CURRENT_STYLE)
    }

    DefaultComponentRequest(PageContext pageContext) {
        componentServletRequest = DefaultComponentServletRequest.fromPageContext(pageContext)

        component = (Component) pageContext.getAttribute(DEFAULT_COMPONENT_NAME)
        componentContext = (ComponentContext) pageContext.getAttribute(DEFAULT_COMPONENT_CONTEXT_NAME)
        editContext = (EditContext) pageContext.getAttribute(DEFAULT_EDIT_CONTEXT_NAME)
        designer = (Designer) pageContext.getAttribute(DEFAULT_DESIGNER_NAME)
        currentDesign = (Design) pageContext.getAttribute(DEFAULT_CURRENT_DESIGN_NAME)
        currentStyle = (Style) pageContext.getAttribute(DEFAULT_CURRENT_STYLE_NAME)
    }
}
