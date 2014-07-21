package com.citytechinc.aem.bedrock.core.tags;

import com.citytechinc.aem.bedrock.api.components.annotations.AutoInstantiate;
import com.citytechinc.aem.bedrock.api.request.ComponentRequest;
import com.citytechinc.aem.bedrock.core.bindings.ComponentBindings;
import com.day.cq.wcm.api.components.Component;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspTagException;
import java.util.Map;

import static com.citytechinc.aem.bedrock.core.constants.ComponentConstants.PROPERTY_CLASS_NAME;
import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_SLING_NAME;

/**
 * Add to and/or override attributes set in pageContext for use in JSPs.
 */
public final class DefineObjectsTag extends AbstractComponentInstanceTag {

    // attribute names

    public static final String ATTR_COMPONENT_BINDINGS = "componentBindings";

    public static final String ATTR_COMPONENT_INSTANCE_NAME = "componentInstanceName";

    private static final Logger LOG = LoggerFactory.getLogger(DefineObjectsTag.class);

    private static final long serialVersionUID = 1L;

    @Override
    public int doEndTag(final int scope) throws JspTagException {
        final ComponentBindings componentBindings = new ComponentBindings(pageContext);

        pageContext.setAttribute(ATTR_COMPONENT_BINDINGS, componentBindings);

        for (final Map.Entry<String, Object> entry : componentBindings.entrySet()) {
            pageContext.setAttribute(entry.getKey(), entry.getValue());
        }

        final ComponentRequest componentRequest = componentBindings.getComponentRequest();

        if (LOG.isDebugEnabled()) {
            final Resource resource = componentRequest.getResource();
            final SlingScriptHelper sling = (SlingScriptHelper) pageContext.getAttribute(DEFAULT_SLING_NAME);
            final String scriptResourcePath = sling.getScript().getScriptResource().getPath();

            LOG.debug("instantiated component request for resource path = {} with type = {} and script = {}",
                resource.getPath(), resource.getResourceType(), scriptResourcePath);
        }

        instantiateComponentClass(componentRequest);

        return EVAL_PAGE;
    }

    private void instantiateComponentClass(final ComponentRequest request) throws JspTagException {
        final Component component = request.getComponent();

        if (component != null) {
            setComponentInstance(component);
        } else {
            LOG.debug("component is null, not instantiating component class");
        }
    }

    private void setComponentInstance(final Component component) throws JspTagException {
        final String className = component.getProperties().get(PROPERTY_CLASS_NAME, String.class);
        final Class<?> clazz;

        try {
            clazz = className == null ? null : Class.forName(className);
        } catch (ClassNotFoundException e) {
            LOG.error("class not found for name = " + className, e);

            throw new JspTagException(e);
        }

        if (clazz != null) {
            if (clazz.isAnnotationPresent(AutoInstantiate.class)) {
                final AutoInstantiate autoInstantiate = clazz.getAnnotation(AutoInstantiate.class);

                final String instanceName = autoInstantiate.instanceName().isEmpty() ? StringUtils.uncapitalize(
                    clazz.getSimpleName()) : autoInstantiate.instanceName();

                final Object instance = getInstance(clazz);

                LOG.debug("class name = {}, instance name = {}, setting component in page context", className,
                    instanceName);

                pageContext.setAttribute(instanceName, instance);
                pageContext.setAttribute(ATTR_COMPONENT_INSTANCE_NAME, instanceName);
            } else {
                LOG.debug("annotation not present for class name = {}, not instantiating component class", className);
            }
        } else {
            LOG.debug("no class name property for component = {}, not instantiating component class",
                component.getResourceType());
        }
    }
}
