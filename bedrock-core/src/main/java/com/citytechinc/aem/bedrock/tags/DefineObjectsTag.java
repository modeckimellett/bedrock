/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.tags;

import com.citytechinc.aem.bedrock.bindings.ComponentBindings;
import com.citytechinc.aem.bedrock.components.annotations.AutoInstantiate;
import com.citytechinc.aem.bedrock.content.request.ComponentRequest;
import com.day.cq.wcm.api.components.Component;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import javax.servlet.jsp.JspTagException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static com.citytechinc.aem.bedrock.constants.ComponentConstants.PROPERTY_CLASS_NAME;
import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_SLING_NAME;
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_BINDINGS_NAME;

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
    public int doEndTag() throws JspTagException {
        final Bindings bindings = (Bindings) pageContext.getAttribute(DEFAULT_BINDINGS_NAME);
        final ComponentBindings componentBindings = new ComponentBindings(bindings);

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

    private void instantiateComponentClass(final ComponentRequest request) {
        final Component component = request.getComponent();

        if (component != null) {
            try {
                setInstance(component);

                Component superComponent = component.getSuperComponent();

                while (superComponent != null) {
                    setInstance(superComponent);

                    superComponent = superComponent.getSuperComponent();
                }
            } catch (Exception e) {
                LOG.error("error instantiating component class", e);
            }
        } else {
            LOG.debug("component is null, not instantiating component class");
        }
    }

    private void setInstance(final Component component)
        throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException,
        IllegalAccessException {
        final String className = component.getProperties().get(PROPERTY_CLASS_NAME, String.class);

        if (className != null) {
            final Class<?> clazz = Class.forName(className);

            if (clazz.isAnnotationPresent(AutoInstantiate.class)) {
                final String instanceName = getInstanceName(clazz);
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

    private String getInstanceName(final Class<?> clazz) {
        final AutoInstantiate autoInstantiate = clazz.getAnnotation(AutoInstantiate.class);
        final String instanceName = autoInstantiate.instanceName();

        return instanceName.isEmpty() ? StringUtils.uncapitalize(clazz.getSimpleName()) : instanceName;
    }
}
