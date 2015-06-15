package com.citytechinc.aem.bedrock.models.impl;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory;
import org.osgi.framework.Constants;

import com.citytechinc.aem.bedrock.api.node.ComponentNode;
import com.citytechinc.aem.bedrock.models.annotations.InheritInject;

@Component
@Service
@Property(name = Constants.SERVICE_RANKING, intValue = 4000)
public class InheritInjector implements Injector, InjectAnnotationProcessorFactory {
	public static final String NAME = "inherit";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Object getValue(Object adaptable, String name, Type declaredType, AnnotatedElement element,
		DisposalCallbackRegistry callbackRegistry) {
		Resource resource = null;
		if (adaptable instanceof Resource) {
			resource = (Resource) adaptable;
		} else if (adaptable instanceof SlingHttpServletRequest) {
			resource = ((SlingHttpServletRequest) adaptable).getResource();
		}
		if (resource != null) {
			ComponentNode componentNode = resource.adaptTo(ComponentNode.class);
			if (componentNode != null) {
				return componentNode.getInherited(name, declaredType);
			}
		}
		return null;
	}

	@Override
	public InjectAnnotationProcessor createAnnotationProcessor(Object adaptable, AnnotatedElement element) {
		// check if the element has the expected annotation
		InheritInject annotation = element.getAnnotation(InheritInject.class);
		if (annotation != null) {
			return new InheritAnnotationProcessor(annotation, adaptable);
		}
		return null;
	}

	private static class InheritAnnotationProcessor extends AbstractInjectAnnotationProcessor {

		private final InheritInject annotation;

		public InheritAnnotationProcessor(InheritInject annotation, Object adaptable) {
			this.annotation = annotation;
		}

		@Override
		public Boolean isOptional() {
			return annotation.optional();
		}

	}
}
