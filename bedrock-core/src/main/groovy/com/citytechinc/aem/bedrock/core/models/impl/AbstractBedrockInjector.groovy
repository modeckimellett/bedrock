package com.citytechinc.aem.bedrock.core.models.impl

import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.Resource
import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.Injector

import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Type

/**
 * Base injector with convenience methods for custom Sling model injectors.
 */
abstract class AbstractBedrockInjector implements Injector {

    abstract Object inject(Object adaptable, String name, Type type, AnnotatedElement element,
        DisposalCallbackRegistry registry)

    @Override
    Object getValue(Object adaptable, String name, Type type, AnnotatedElement element,
        DisposalCallbackRegistry registry) {
        def value = null

        if (adaptable instanceof Resource || adaptable instanceof SlingHttpServletRequest) {
            value = inject(adaptable, name, type, element, registry)
        }

        value
    }

    protected SlingHttpServletRequest getRequest(adaptable) {
        def request = null

        if (adaptable instanceof SlingHttpServletRequest) {
            request = adaptable as SlingHttpServletRequest
        }

        request
    }

    protected Resource getResource(adaptable) {
        def resource = null

        if (adaptable instanceof Resource) {
            resource = adaptable as Resource
        } else if (adaptable instanceof SlingHttpServletRequest) {
            resource = (adaptable as SlingHttpServletRequest).resource
        }

        resource
    }
}
