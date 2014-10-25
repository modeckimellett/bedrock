package com.citytechinc.aem.bedrock.core.models.impl;

import static com.adobe.cq.sightly.WCMBindings.COMPONENT;
import static com.adobe.cq.sightly.WCMBindings.COMPONENT_CONTEXT;
import static com.adobe.cq.sightly.WCMBindings.CURRENT_DESIGN;
import static com.adobe.cq.sightly.WCMBindings.CURRENT_STYLE;
import static com.adobe.cq.sightly.WCMBindings.DESIGNER;
import static com.adobe.cq.sightly.WCMBindings.EDIT_CONTEXT;
import static org.apache.sling.api.scripting.SlingBindings.REQUEST;
import static org.apache.sling.api.scripting.SlingBindings.RESOURCE;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import javax.script.Bindings;
import javax.script.SimpleBindings;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.osgi.framework.Constants;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.commons.WCMUtils;

@Component
@Service
@Property(name = Constants.SERVICE_RANKING, intValue = 1000)
public class BindingsInjector implements Injector {

	@Override
	public String getName() {
		return "bindings";
	}

	@Override
	public Object getValue(Object adaptable, String name, Type declaredType, AnnotatedElement element,
		DisposalCallbackRegistry callbackRegistry) {

		if (!(adaptable instanceof Resource || adaptable instanceof SlingHttpServletRequest)
			|| declaredType.equals(Bindings.class)) {
			return null;
		}

		Bindings simpleBindings = new SimpleBindings();
		Resource resource = null;
		if (adaptable instanceof SlingHttpServletRequest) {
			SlingHttpServletRequest request = (SlingHttpServletRequest) adaptable;
			SlingBindings bindings = (SlingBindings) request.getAttribute(SlingBindings.class.getName());
			if (bindings != null) {
				return bindings;
			}
			ComponentContext componentContext = WCMUtils.getComponentContext(request);
			simpleBindings.put(REQUEST, request);
			simpleBindings.put(COMPONENT_CONTEXT, componentContext);
			if (componentContext != null) {
				simpleBindings.put(EDIT_CONTEXT, componentContext.getEditContext());
			}
			resource = request.getResource();
		}

		if (adaptable instanceof Resource) {
			resource = (Resource) adaptable;
		}
		ResourceResolver resourceResolver = resource.getResourceResolver();
		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		Designer designer = resourceResolver.adaptTo(Designer.class);
		Page currentPage = pageManager.getContainingPage(resource);
		Design currentDesign = designer.getDesign(currentPage);
		simpleBindings.put(RESOURCE, resource);
		simpleBindings.put(COMPONENT, WCMUtils.getComponent(resource));
		simpleBindings.put(DESIGNER, designer);
		simpleBindings.put(CURRENT_DESIGN, currentDesign);
		simpleBindings.put(CURRENT_STYLE, currentDesign.getStyle(resource));

		return simpleBindings;
	}
}
