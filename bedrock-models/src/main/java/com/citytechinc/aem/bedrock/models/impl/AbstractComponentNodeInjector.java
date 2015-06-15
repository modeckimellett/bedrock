package com.citytechinc.aem.bedrock.models.impl;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;

import com.citytechinc.aem.bedrock.api.node.ComponentNode;

public abstract class AbstractComponentNodeInjector implements Injector {

	abstract Object getValue(ComponentNode componentNode, String name, Type declaredType, AnnotatedElement element,
		DisposalCallbackRegistry callbackRegistry);

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
				return getValue(componentNode, name, declaredType, element, callbackRegistry);
			}
		}
		return null;
	}

}
