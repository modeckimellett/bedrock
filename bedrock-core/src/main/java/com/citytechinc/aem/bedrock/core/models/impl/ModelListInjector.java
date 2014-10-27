package com.citytechinc.aem.bedrock.core.models.impl;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.osgi.framework.Constants;

@Component
@Service
@Property(name = Constants.SERVICE_RANKING, intValue = 999)
public class ModelListInjector implements Injector {

	@Override
	public String getName() {
		return "model-list";
	}

	@Override
	public Object getValue(Object adaptable, String name, Type declaredType, AnnotatedElement element,
		DisposalCallbackRegistry callbackRegistry) {

		if ((adaptable instanceof Resource || adaptable instanceof SlingHttpServletRequest)
			&& declaredType instanceof ParameterizedType
			&& ((ParameterizedType) declaredType).getRawType().equals(List.class)) {
			Class<?> typeClass = getActualType((ParameterizedType) declaredType);
			List<Object> returnList = new ArrayList<Object>();
			Resource resource = null;
			if (adaptable instanceof Resource) {
				resource = (Resource) adaptable;
			} else {
				resource = ((SlingHttpServletRequest) adaptable).getResource();
			}
			Resource childResource = resource.getChild(name);
			if (childResource != null) {
				for (Resource grandChildResource : childResource.getChildren()) {
					returnList.add(grandChildResource.adaptTo(typeClass));
				}
				return returnList;
			}
		}
		return null;
	}

	private Class<?> getActualType(ParameterizedType declaredType) {
		Type[] types = declaredType.getActualTypeArguments();
		if (types != null && types.length > 0) {
			return (Class<?>) types[0];
		}
		return null;
	}
}
