package com.citytechinc.aem.bedrock.core.models.impl

import com.day.cq.wcm.api.PageManager
import com.day.cq.wcm.api.designer.Designer
import com.day.cq.wcm.commons.WCMUtils
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.scripting.SlingBindings
import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.Injector
import org.osgi.framework.Constants

import javax.script.Bindings
import javax.script.SimpleBindings
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Type

import static com.adobe.cq.sightly.WCMBindings.COMPONENT
import static com.adobe.cq.sightly.WCMBindings.COMPONENT_CONTEXT
import static com.adobe.cq.sightly.WCMBindings.CURRENT_DESIGN
import static com.adobe.cq.sightly.WCMBindings.CURRENT_STYLE
import static com.adobe.cq.sightly.WCMBindings.DESIGNER
import static com.adobe.cq.sightly.WCMBindings.EDIT_CONTEXT
import static org.apache.sling.api.scripting.SlingBindings.REQUEST
import static org.apache.sling.api.scripting.SlingBindings.RESOURCE

@Component
@Service
@Property(name = Constants.SERVICE_RANKING, intValue = Integer.MAX_VALUE)
class BindingsInjector implements Injector {

    @Override
    String getName() {
        "bindings"
    }

    @Override
    Object getValue(Object adaptable, String name, Type declaredType, AnnotatedElement element,
        DisposalCallbackRegistry callbackRegistry) {
        if (!(adaptable instanceof Resource || adaptable instanceof SlingHttpServletRequest) || !declaredType.equals(
            Bindings.class)) {
            return null
        }

        def simpleBindings = new SimpleBindings()
        def resource = null

        if (adaptable instanceof SlingHttpServletRequest) {
            def request = (SlingHttpServletRequest) adaptable
            def bindings = (SlingBindings) request.getAttribute(SlingBindings.class.name)

            if (bindings != null) {
                return new SimpleBindings(bindings)
            }

            def componentContext = WCMUtils.getComponentContext(request)

            simpleBindings.put(REQUEST, request)
            simpleBindings.put(COMPONENT_CONTEXT, componentContext)

            if (componentContext) {
                simpleBindings.put(EDIT_CONTEXT, componentContext.editContext)
            }

            resource = request.resource
        }

        if (adaptable instanceof Resource) {
            resource = (Resource) adaptable
        }

        def resourceResolver = resource.resourceResolver
        def pageManager = resourceResolver.adaptTo(PageManager)
        def designer = resourceResolver.adaptTo(Designer)
        def currentPage = pageManager.getContainingPage(resource)
        def currentDesign = designer.getDesign(currentPage)

        simpleBindings.put(RESOURCE, resource)
        simpleBindings.put(COMPONENT, WCMUtils.getComponent(resource))
        simpleBindings.put(DESIGNER, designer)
        simpleBindings.put(CURRENT_DESIGN, currentDesign)

        if (currentDesign) {
            simpleBindings.put(CURRENT_STYLE, currentDesign.getStyle(resource))
        }

        simpleBindings
    }
}
