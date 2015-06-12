package com.citytechinc.aem.bedrock.core.tags

import groovy.util.logging.Slf4j
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.Resource
import org.apache.sling.models.annotations.Model

import javax.servlet.jsp.JspTagException

@Slf4j("LOG")
abstract class AbstractComponentInstanceTag extends AbstractScopedTag {

    protected final Object getInstance(Class<?> clazz) {
        def instance

        if (clazz.isAnnotationPresent(Model)) {
            def adaptables = clazz.getAnnotation(Model).adaptables()

            if (adaptables.contains(SlingHttpServletRequest)) {
                instance = ((SlingHttpServletRequest) pageContext.request).adaptTo(clazz)
            } else if (adaptables.contains(Resource)) {
                instance = ((SlingHttpServletRequest) pageContext.request).resource.adaptTo(clazz)
            } else {
                throw new JspTagException("component class ${clazz.name} is not adaptable from request or resource");
            }
        } else {
            try {
                instance = clazz.newInstance()
            } catch (InstantiationException | IllegalAccessException e) {
                LOG.error("error instantiating component class", e)

                throw new JspTagException(e)
            }
        }

        instance
    }

    protected final Object getInstance(String className) {
        def clazz

        try {
            clazz = Class.forName(className)
        } catch (ClassNotFoundException e) {
            LOG.error "class not found = $className", e

            throw new JspTagException(e)
        }

        getInstance(clazz)
    }
}
