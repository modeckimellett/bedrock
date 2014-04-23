package com.citytechinc.aem.bedrock.core.request.impl

import com.citytechinc.aem.bedrock.api.request.ComponentRequest
import com.citytechinc.aem.bedrock.api.request.ComponentServletRequest
import com.day.cq.wcm.api.components.Component
import com.day.cq.wcm.api.components.ComponentContext
import com.day.cq.wcm.api.components.EditContext
import com.day.cq.wcm.api.designer.Design
import com.day.cq.wcm.api.designer.Designer
import com.day.cq.wcm.api.designer.Style
import org.apache.sling.api.scripting.SlingScriptHelper

import javax.script.Bindings

import static com.adobe.cq.sightly.WCMBindings.COMPONENT
import static com.adobe.cq.sightly.WCMBindings.COMPONENT_CONTEXT
import static com.adobe.cq.sightly.WCMBindings.CURRENT_DESIGN
import static com.adobe.cq.sightly.WCMBindings.CURRENT_STYLE
import static com.adobe.cq.sightly.WCMBindings.DESIGNER
import static com.adobe.cq.sightly.WCMBindings.EDIT_CONTEXT
import static org.apache.sling.api.scripting.SlingBindings.SLING

final class DefaultComponentRequest implements ComponentRequest {

    @Delegate
    private final ComponentServletRequest componentServletRequest

    final Component component

    final ComponentContext componentContext

    final Design currentDesign

    final Style currentStyle

    final Designer designer

    final EditContext editContext

    final SlingScriptHelper sling

    DefaultComponentRequest(Bindings bindings) {
        componentServletRequest = new DefaultComponentServletRequest(bindings)

        component = bindings.get(COMPONENT) as Component
        componentContext = bindings.get(COMPONENT_CONTEXT) as ComponentContext
        editContext = bindings.get(EDIT_CONTEXT) as EditContext
        designer = bindings.get(DESIGNER) as Designer
        currentDesign = bindings.get(CURRENT_DESIGN) as Design
        currentStyle = bindings.get(CURRENT_STYLE) as Style
        sling = bindings.get(SLING) as SlingScriptHelper
    }
}
