package com.citytechinc.aem.bedrock.models.impl;

import com.citytechinc.aem.bedrock.api.node.ComponentNode;
import com.citytechinc.aem.bedrock.models.annotations.InheritInject;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory;
import org.osgi.framework.Constants;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

@Component
@Service
@Property(name = Constants.SERVICE_RANKING, intValue = 4000)
public class InheritInjector extends AbstractComponentNodeInjector implements InjectAnnotationProcessorFactory {

    public static final String NAME = "inherit";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Object getValue(ComponentNode componentNode, String name, Type declaredType, AnnotatedElement element,
        DisposalCallbackRegistry callbackRegistry) {
        return componentNode.getInherited(name, declaredType);
    }

    @Override
    public InjectAnnotationProcessor createAnnotationProcessor(Object adaptable, AnnotatedElement element) {
        InheritInject annotation = element.getAnnotation(InheritInject.class);

        return annotation != null ? new InheritAnnotationProcessor(annotation) : null;
    }

    private static class InheritAnnotationProcessor extends AbstractInjectAnnotationProcessor {

        private final InheritInject annotation;

        public InheritAnnotationProcessor(InheritInject annotation) {
            this.annotation = annotation;
        }

        @Override
        public Boolean isOptional() {
            return annotation.optional();
        }
    }
}
