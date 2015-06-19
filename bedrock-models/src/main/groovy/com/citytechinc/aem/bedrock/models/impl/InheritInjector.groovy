package com.citytechinc.aem.bedrock.models.impl

import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.models.annotations.InheritInject
import com.citytechinc.aem.bedrock.models.constants.ModelsConstants
import groovy.transform.TupleConstructor
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory
import org.osgi.framework.Constants

import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Type

@Component
@Service
@Property(name = Constants.SERVICE_RANKING, intValue = 4000)
class InheritInjector extends AbstractComponentNodeInjector implements InjectAnnotationProcessorFactory {

    @Override
    String getName() {
        ModelsConstants.INHERIT_NAME
    }

    @Override
    Object getValue(ComponentNode componentNode, String name, Type declaredType, AnnotatedElement element,
        DisposalCallbackRegistry callbackRegistry) {
        componentNode.getInherited(name, declaredType)
    }

    @Override
    InjectAnnotationProcessor createAnnotationProcessor(Object adaptable, AnnotatedElement element) {
        def annotation = element.getAnnotation(InheritInject)

        annotation ? new InheritAnnotationProcessor(annotation) : null
    }

    @TupleConstructor
    private static class InheritAnnotationProcessor extends AbstractInjectAnnotationProcessor {

        InheritInject annotation

        @Override
        Boolean isOptional() {
            annotation.optional()
        }
    }
}
