package com.citytechinc.aem.bedrock.core.tags;

import com.citytechinc.aem.bedrock.core.bindings.ComponentBindings;
import com.citytechinc.aem.bedrock.core.components.AbstractComponent;

import static com.citytechinc.aem.bedrock.core.tags.DefineObjectsTag.ATTR_COMPONENT_BINDINGS;

/**
 * Base class for tags that instantiate component classes.
 */
public abstract class AbstractComponentInstanceTag extends AbstractScopedTag {

    protected final Object getInstance(final Class<?> clazz) throws IllegalAccessException, InstantiationException {
        final Object instance = clazz.newInstance();

        if (instance instanceof AbstractComponent) {
            final ComponentBindings componentBindings = (ComponentBindings) pageContext.getAttribute(
                ATTR_COMPONENT_BINDINGS);

            ((AbstractComponent) instance).init(componentBindings);
        }

        return instance;
    }

    protected final Object getInstance(final String className)
        throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        final Class<?> clazz = Class.forName(className);

        return getInstance(clazz);
    }
}
