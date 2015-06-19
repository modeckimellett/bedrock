package com.citytechinc.aem.bedrock.models.impl

import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.models.utils.ModelUtils
import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.Injector

import java.lang.reflect.AnnotatedElement
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

abstract class AbstractTypedComponentNodeInjector<T> implements Injector {

    abstract Object getValue(ComponentNode componentNode, String name, Class<T> declaredType, AnnotatedElement element,
        DisposalCallbackRegistry callbackRegistry)

    @Override
    Object getValue(Object adaptable, String name, Type declaredType, AnnotatedElement element,
        DisposalCallbackRegistry callbackRegistry) {
        def clazz = (getClass().genericSuperclass as ParameterizedType).actualTypeArguments[0]

        def value = null

        if (declaredType == clazz) {
            def componentNode = ModelUtils.getResource(adaptable)?.adaptTo(ComponentNode)

            if (componentNode) {
                value = getValue(componentNode, name, declaredType, element, callbackRegistry)
            }
        }

        value
    }
}
