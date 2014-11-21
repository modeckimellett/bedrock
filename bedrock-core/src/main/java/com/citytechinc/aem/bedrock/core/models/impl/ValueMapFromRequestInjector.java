package com.citytechinc.aem.bedrock.core.models.impl;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.osgi.framework.Constants;

@Component
@Service
@Property(name = Constants.SERVICE_RANKING, intValue = 2500)
public class ValueMapFromRequestInjector implements Injector {

	@Override
	public String getName() {
		return "valuemap";
	}

	@Override
	public Object getValue(Object adaptable, String name, Type type, AnnotatedElement element,
		DisposalCallbackRegistry callbackRegistry) {
		if (adaptable instanceof SlingHttpServletRequest) {
			SlingHttpServletRequest request = (SlingHttpServletRequest) adaptable;
			if (request.getResource() != null) {
				ValueMap map = request.getResource().getValueMap();
				if (map != null && type instanceof Class<?>) {
					return map.get(name, (Class<?>) type);
				}
			}
		}
		return null;
	}
}
