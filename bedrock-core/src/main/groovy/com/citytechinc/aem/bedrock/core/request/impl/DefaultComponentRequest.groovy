package com.citytechinc.aem.bedrock.core.request.impl

import com.citytechinc.aem.bedrock.api.request.ComponentRequest
import com.citytechinc.aem.bedrock.api.request.ComponentServletRequest
import com.citytechinc.aem.bedrock.api.services.ServiceProvider
import com.citytechinc.aem.bedrock.core.services.impl.DefaultServiceProvider
import com.day.cq.wcm.api.components.Component
import com.day.cq.wcm.api.components.ComponentContext
import com.day.cq.wcm.api.components.EditContext
import com.day.cq.wcm.api.designer.Design
import com.day.cq.wcm.api.designer.Designer
import com.day.cq.wcm.api.designer.Style
import com.day.cq.wcm.commons.WCMUtils
import org.apache.sling.api.scripting.SlingScriptHelper
import org.osgi.framework.FrameworkUtil

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
    private final ComponentServletRequest request

    final Component component

    final ComponentContext componentContext

    final Design currentDesign

    final Style currentStyle

    final Designer designer

    final EditContext editContext

    final ServiceProvider serviceProvider

    /**
     * Create a <code>ComponentRequest</code> for the given script bindings
     *
     * @param bindings script bindings
     */
    DefaultComponentRequest(Bindings bindings) {
        request = new DefaultComponentServletRequest(bindings)

        component = bindings.get(COMPONENT) as Component
        componentContext = bindings.get(COMPONENT_CONTEXT) as ComponentContext
        editContext = bindings.get(EDIT_CONTEXT) as EditContext
        designer = bindings.get(DESIGNER) as Designer
        currentDesign = bindings.get(CURRENT_DESIGN) as Design
        currentStyle = bindings.get(CURRENT_STYLE) as Style

        def sling = bindings.get(SLING) as SlingScriptHelper

        serviceProvider = new DefaultServiceProvider(sling)
    }

    /**
     * Adapting constructor for creating a <code>ComponentRequest</code> from a servlet context.
     *
     * @param request existing servlet request
     */
    DefaultComponentRequest(ComponentServletRequest request) {
        this.request = request

        componentContext = WCMUtils.getComponentContext(request.slingRequest)
        component = componentContext?.component
        editContext = componentContext?.editContext
        designer = request.resourceResolver.adaptTo(Designer)
        currentDesign = designer?.getDesign(request.currentPage)
        currentStyle = !componentContext || !currentDesign ? null : currentDesign.getStyle(componentContext.cell)

        def bundleContext = FrameworkUtil.getBundle(DefaultComponentRequest).bundleContext

        serviceProvider = new DefaultServiceProvider(bundleContext)
    }
}
