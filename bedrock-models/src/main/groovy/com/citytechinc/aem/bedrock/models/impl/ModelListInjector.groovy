package com.citytechinc.aem.bedrock.models.impl

import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.Resource
import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.Injector
import org.osgi.framework.Constants

import java.lang.reflect.AnnotatedElement
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

@Component
@Service
@Property(name = Constants.SERVICE_RANKING, intValue = 999)
class ModelListInjector implements Injector {

    @Override
    String getName() {
        "model-list"
    }

    @Override
    Object getValue(Object adaptable, String name, Type declaredType, AnnotatedElement element,
        DisposalCallbackRegistry callbackRegistry) {
        def value = null

        if ((adaptable instanceof Resource || adaptable instanceof SlingHttpServletRequest)
            && declaredType instanceof ParameterizedType && (((ParameterizedType) declaredType)
            .rawType) as Class == List) {
            def typeClass = getActualType((ParameterizedType) declaredType)

            def resource

            if (adaptable instanceof Resource) {
                resource = (Resource) adaptable
            } else {
                resource = ((SlingHttpServletRequest) adaptable).resource
            }

            def childResource = resource.getChild(name)

            if (childResource) {
                value = childResource.children.collect { grandChildResource -> grandChildResource.adaptTo(typeClass) }
            }
        }

        value
    }

    private static Class<?> getActualType(ParameterizedType declaredType) {
        def types = declaredType.actualTypeArguments

        types ? (Class<?>) types[0] : null
    }
}
