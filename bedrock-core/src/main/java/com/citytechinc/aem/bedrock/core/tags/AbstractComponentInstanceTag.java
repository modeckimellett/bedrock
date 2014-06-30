package com.citytechinc.aem.bedrock.core.tags;

import com.citytechinc.aem.bedrock.core.bindings.ComponentBindings;
import com.citytechinc.aem.bedrock.core.components.AbstractComponent;
import com.google.common.collect.Sets;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

import java.util.Set;
import org.apache.sling.api.scripting.SlingBindings;

import java.util.Map;

import static com.citytechinc.aem.bedrock.core.tags.DefineObjectsTag.ATTR_COMPONENT_BINDINGS;
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_BINDINGS_NAME;

/**
 * Base class for tags that instantiate component classes.
 */
public abstract class AbstractComponentInstanceTag extends AbstractScopedTag {

    protected final Object getInstance(final Class<?> clazz) throws IllegalAccessException, InstantiationException {
        final Object instance = instantiateInstance(clazz);

        if (instance instanceof AbstractComponent) {
            final ComponentBindings componentBindings = (ComponentBindings) pageContext.getAttribute(
                ATTR_COMPONENT_BINDINGS);

            // add sling bindings
            final SlingBindings bindings = (SlingBindings) pageContext.getAttribute(DEFAULT_BINDINGS_NAME);

            for (final Map.Entry<String, Object> entry : bindings.entrySet()) {
                final String key = entry.getKey();

                if (!componentBindings.containsKey(key)) {
                    componentBindings.put(key, entry.getValue());
                }
            }

            ((AbstractComponent) instance).init(componentBindings);
        }

        return instance;
    }

    protected final Object getInstance(final String className)
        throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        final Class<?> clazz = Class.forName(className);

        return getInstance(clazz);
    }

    private Object instantiateInstance(final Class<?> clazz) throws IllegalAccessException, InstantiationException {

        Model slingModelAnnotation = clazz.getAnnotation(Model.class);

        if (slingModelAnnotation != null) {

            final ComponentBindings componentBindings = (ComponentBindings) pageContext.getAttribute(ATTR_COMPONENT_BINDINGS);

            Set<Class<?>> adaptablesSet = Sets.newHashSet(slingModelAnnotation.adaptables());

            if (adaptablesSet.contains(Resource.class)) {
                return componentBindings.getComponentRequest().getResource().adaptTo(clazz);
            }
            else if (adaptablesSet.contains(SlingHttpServletRequest.class)) {
                return componentBindings.getComponentRequest().getSlingRequest().adaptTo(clazz);
            }

        }

        return clazz.newInstance();

    }
}
