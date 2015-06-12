package com.citytechinc.aem.bedrock.core.models.impl

import com.day.cq.wcm.api.WCMMode
import com.day.cq.wcm.api.components.ComponentContext
import com.day.cq.wcm.api.components.EditContext
import com.day.cq.wcm.api.designer.Design
import com.day.cq.wcm.api.designer.Designer
import com.day.cq.wcm.api.designer.Style
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.scripting.SlingBindings
import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.Injector
import org.osgi.framework.Constants

import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Type

import static com.adobe.cq.sightly.WCMBindings.COMPONENT
import static com.adobe.cq.sightly.WCMBindings.COMPONENT_CONTEXT
import static com.adobe.cq.sightly.WCMBindings.CURRENT_DESIGN
import static com.adobe.cq.sightly.WCMBindings.CURRENT_STYLE
import static com.adobe.cq.sightly.WCMBindings.DESIGNER
import static com.adobe.cq.sightly.WCMBindings.EDIT_CONTEXT

/**
 * Injector for objects derived from the current request context.
 */
@Component
@Service(Injector)
@Property(name = Constants.SERVICE_RANKING, intValue = Integer.MAX_VALUE)
@Slf4j("LOG")
class RequestContextInjector extends AbstractBedrockInjector {

    @Override
    String getName() {
        "request-context"
    }

    @Override
    Object inject(Object adaptable, String name, Type type, AnnotatedElement element,
        DisposalCallbackRegistry registry) {
        def value = null

        if (type instanceof Class) {
            def request = getRequest(adaptable)

            if (request) {
                value = getValueForClass(type as Class, request)
            }
        }

        value
    }

    private static def getValueForClass(Class clazz, SlingHttpServletRequest request) {
        def value = null

        def bindings = request.getAttribute(SlingBindings.class.name) as SlingBindings

        if (clazz == SlingHttpServletRequest) {
            value = request
        } else if (clazz == WCMMode) {
            value = WCMMode.fromRequest(request)
        } else if (clazz == com.day.cq.wcm.api.components.Component) {
            value = bindings[COMPONENT] as com.day.cq.wcm.api.components.Component
        } else if (clazz == ComponentContext) {
            value = bindings[COMPONENT_CONTEXT] as ComponentContext
        } else if (clazz == EditContext) {
            value = bindings[EDIT_CONTEXT] as EditContext
        } else if (clazz == Designer) {
            value = bindings[DESIGNER] as Designer
        } else if (clazz == Design) {
            value = bindings[CURRENT_DESIGN] as Design
        } else if (clazz == Style) {
            value = bindings[CURRENT_STYLE] as Style
        } else {
            LOG.debug("class = {} is not supported by this injector", clazz.name)
        }

        LOG.debug("injecting class = {} with instance = {}", clazz.name, value)

        value
    }
}
