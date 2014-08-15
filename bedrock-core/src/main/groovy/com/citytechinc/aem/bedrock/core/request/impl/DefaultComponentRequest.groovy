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
     * Create a <code>ComponentRequest</code> for the given script bindings.
     *
     * @param bindings script bindings
     */
    DefaultComponentRequest(Bindings bindings) {
        request = new DefaultComponentServletRequest(bindings)

        component = bindings[COMPONENT] as Component
        componentContext = bindings[COMPONENT_CONTEXT] as ComponentContext
        editContext = bindings[EDIT_CONTEXT] as EditContext
        designer = bindings[DESIGNER] as Designer
        currentDesign = bindings[CURRENT_DESIGN] as Design
        currentStyle = bindings[CURRENT_STYLE] as Style

        def slingScriptHelper = bindings[SLING] as SlingScriptHelper

        if (slingScriptHelper) {
            serviceProvider = new DefaultServiceProvider(slingScriptHelper)
        } else {
            def bundleContext = FrameworkUtil.getBundle(DefaultComponentRequest)?.bundleContext

            serviceProvider = !bundleContext ? null : new DefaultServiceProvider(bundleContext)
        }
    }
}
