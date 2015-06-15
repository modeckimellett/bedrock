package com.citytechinc.aem.bedrock.models.impl;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.models.spi.AcceptsNullName;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory;
import org.osgi.framework.Constants;

import com.citytechinc.aem.bedrock.api.link.Link;
import com.citytechinc.aem.bedrock.api.link.builders.LinkBuilder;
import com.citytechinc.aem.bedrock.api.node.ComponentNode;
import com.citytechinc.aem.bedrock.core.link.builders.factory.LinkBuilderFactory;
import com.citytechinc.aem.bedrock.models.annotations.LinkInject;
import com.google.common.base.Optional;

@Component
@Service
@Property(name = Constants.SERVICE_RANKING, intValue = 4000)
public class LinkInjector extends AbstractComponentNodeInjector<Link> implements Injector,
	InjectAnnotationProcessorFactory, AcceptsNullName {
	public static final String NAME = "links";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Object getValue(ComponentNode componentNode, String name, Type declaredType, AnnotatedElement element,
		DisposalCallbackRegistry callbackRegistry) {
		LinkInject injectAnnotation = element.getAnnotation(LinkInject.class);
		Optional<String> pathOptional = null;
		String title = null;
		if (injectAnnotation != null) {
			if (injectAnnotation.inherit()) {
				pathOptional = componentNode.getInherited(name, String.class);
				if (StringUtils.isNotEmpty(injectAnnotation.titleProperty())) {
					title = componentNode.getInherited(injectAnnotation.titleProperty(), String.class).orNull();
				}
			} else {
				pathOptional = componentNode.get(name, String.class);
				if (StringUtils.isNotEmpty(injectAnnotation.titleProperty())) {
					title = componentNode.getInherited(injectAnnotation.titleProperty(), String.class).orNull();
				}
			}
		} else {
			pathOptional = componentNode.get(name, String.class);
		}
		if (pathOptional.isPresent()) {
			LinkBuilder linkBuilder = LinkBuilderFactory.forPath(pathOptional.get());
			linkBuilder.setTitle(title);
			return linkBuilder.build();
		}

		return null;
	}

	@Override
	public InjectAnnotationProcessor createAnnotationProcessor(Object adaptable, AnnotatedElement element) {
		// check if the element has the expected annotation
		LinkInject annotation = element.getAnnotation(LinkInject.class);
		if (annotation != null) {
			return new LinkAnnotationProcessor(annotation, adaptable);
		}
		return null;
	}

	private static class LinkAnnotationProcessor extends AbstractInjectAnnotationProcessor {

		private final LinkInject annotation;

		public LinkAnnotationProcessor(LinkInject annotation, Object adaptable) {
			this.annotation = annotation;
		}

		@Override
		public Boolean isOptional() {
			return annotation.optional();
		}

	}
}
