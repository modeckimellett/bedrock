package com.citytechinc.aem.bedrock.core.tags

import com.citytechinc.aem.bedrock.core.components.AbstractComponent

import groovy.util.logging.Slf4j

import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.scripting.SlingBindings
import org.apache.sling.models.annotations.Model

import javax.script.SimpleBindings
import javax.servlet.jsp.JspTagException

import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_BINDINGS_NAME

@Slf4j("LOG")
abstract class AbstractComponentInstanceTag extends AbstractScopedTag {

	protected final Object getInstance(Class<?> clazz) {
		def instance

		try {
			boolean useModels;
			if(clazz.isAnnotationPresent(Model.class)){
				Class<Object>[] adaptables=clazz.getAnnotation(Model.class).adaptables();
				
				if (adaptables.contains(SlingHttpServletRequest.class)) {
					useModels=true;
					instance = ((SlingHttpServletRequest)pageContext.getRequest()).adaptTo(clazz)
				}
				else if (adaptables.contains(Resource.class)) {
					useModels=true;
					instance = ((SlingHttpServletRequest)pageContext.getRequest()).getResource().adaptTo(clazz);
				}
			}
			if(!useModels){
				instance = clazz.newInstance()

				if (instance instanceof AbstractComponent) {
					def slingBindings = pageContext.getAttribute(DEFAULT_BINDINGS_NAME) as SlingBindings
					def bindings = new SimpleBindings(slingBindings)

					((AbstractComponent) instance).init(bindings)
				}
			}
		} catch (InstantiationException e) {
			LOG.error "error instantiating component class", e

			throw new JspTagException(e)
		} catch (IllegalAccessException e) {
			LOG.error "error instantiating component class", e

			throw new JspTagException(e)
		}

		instance
	}

	protected final Object getInstance(String className) {
		def clazz

		try {
			clazz = Class.forName(className)
		} catch (ClassNotFoundException e) {
			LOG.error "class not found = $className", e

			throw new JspTagException(e)
		}

		getInstance(clazz)
	}
}
