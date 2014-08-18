package com.citytechinc.aem.bedrock.core.tags

import com.citytechinc.aem.bedrock.api.components.annotations.AutoInstantiate
import com.citytechinc.aem.bedrock.core.bindings.BedrockBindings
import com.day.cq.wcm.api.components.Component
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.scripting.SlingScriptHelper

import javax.servlet.jsp.JspTagException

import static com.citytechinc.aem.bedrock.core.constants.ComponentConstants.PROPERTY_CLASS_NAME
import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_COMPONENT_NAME
import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_SLING_NAME
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_REQUEST_NAME

/**
 * Add to and/or override attributes set in pageContext for use in JSPs.
 */
@Slf4j("LOG")
final class DefineObjectsTag extends AbstractComponentInstanceTag {

    private static final def ATTR_COMPONENT_INSTANCE_NAME = "componentInstanceName"

    @Override
    int doEndTag(int scope) throws JspTagException {
        def slingRequest = pageContext.getAttribute(DEFAULT_REQUEST_NAME) as SlingHttpServletRequest

        setBindings(slingRequest)

        if (LOG.debugEnabled) {
            def resource = slingRequest.resource
            def sling = pageContext.getAttribute(DEFAULT_SLING_NAME) as SlingScriptHelper

            LOG.debug "instantiated component request for resource path = {} with type = {} and script = {}",
                resource.path, resource.resourceType, sling.script.scriptResource.path
        }

        instantiateComponentClass()

        EVAL_PAGE
    }

    private void setBindings(SlingHttpServletRequest slingRequest) {
        // add bedrock attributes to page context
        def bindings = new BedrockBindings(slingRequest)

        bindings.each { key, value ->
            pageContext.setAttribute(key, value)
        }
    }

    private void instantiateComponentClass() throws JspTagException {
        def component = pageContext.getAttribute(DEFAULT_COMPONENT_NAME) as Component

        if (component) {
            def className = component.properties.get(PROPERTY_CLASS_NAME) as String

            def clazz

            try {
                clazz = !className ? null : Class.forName(className)
            } catch (ClassNotFoundException e) {
                LOG.error "class not found for name = $className", e

                throw new JspTagException(e)
            }

            if (clazz) {
                setComponentInstance(clazz, className)
            } else {
                LOG.debug "class not found for component = {}, not instantiating component class",
                    component.resourceType
            }
        } else {
            LOG.debug "component is null, not instantiating component class"
        }
    }

    private void setComponentInstance(Class<?> clazz, String className) throws JspTagException {
        if (clazz.isAnnotationPresent(AutoInstantiate)) {
            def autoInstantiate = clazz.getAnnotation(AutoInstantiate)

            def instanceName = !autoInstantiate.instanceName() ? StringUtils.uncapitalize(
                clazz.simpleName) : autoInstantiate.instanceName()

            def instance = getInstance(clazz)

            LOG.debug "class name = {}, instance name = {}, setting component in page context", className,
                instanceName

            pageContext.setAttribute(instanceName, instance)
            pageContext.setAttribute(ATTR_COMPONENT_INSTANCE_NAME, instanceName)
        } else {
            LOG.debug "annotation not present for class name = {}, not instantiating component class", className
        }
    }
}
