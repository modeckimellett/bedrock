package com.citytechinc.aem.bedrock.core.tags;

import com.citytechinc.aem.bedrock.core.bindings.ComponentBindings;
import com.citytechinc.aem.bedrock.core.components.AbstractComponent;
import org.apache.sling.api.scripting.SlingBindings;

import javax.script.Bindings;
import java.util.Map;

import static com.citytechinc.aem.bedrock.core.tags.DefineObjectsTag.ATTR_COMPONENT_BINDINGS;
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_BINDINGS_NAME;

/**
 * Base class for tags that instantiate component classes.
 */
public abstract class AbstractComponentInstanceTag extends AbstractScopedTag {

    protected final Object getInstance(final Class<?> clazz) throws IllegalAccessException, InstantiationException {
        final Object instance = clazz.newInstance();

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
}
