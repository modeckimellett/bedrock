package com.citytechinc.aem.bedrock.core.tags;

import com.citytechinc.aem.bedrock.api.components.annotations.AutoInstantiate;
import com.citytechinc.aem.bedrock.core.bindings.BedrockBindings;
import com.day.cq.wcm.api.components.Component;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspTagException;
import java.util.Map;

import static com.citytechinc.aem.bedrock.core.constants.ComponentConstants.PROPERTY_CLASS_NAME;
import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_COMPONENT_NAME;
import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_SLING_NAME;
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_REQUEST_NAME;

/**
 * Add to and/or override attributes set in pageContext for use in JSPs.
 */
public final class DefineObjectsTag extends AbstractComponentInstanceTag {

    public static final String ATTR_COMPONENT_INSTANCE_NAME = "componentInstanceName";

    private static final Logger LOG = LoggerFactory.getLogger(DefineObjectsTag.class);

    private static final long serialVersionUID = 1L;

    @Override
    public int doEndTag(final int scope) throws JspTagException {
        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) pageContext.getAttribute(
            DEFAULT_REQUEST_NAME);

        setBindings(slingRequest);

        if (LOG.isDebugEnabled()) {
            final Resource resource = slingRequest.getResource();
            final SlingScriptHelper sling = (SlingScriptHelper) pageContext.getAttribute(DEFAULT_SLING_NAME);
            final String scriptResourcePath = sling.getScript().getScriptResource().getPath();

            LOG.debug("instantiated component request for resource path = {} with type = {} and script = {}",
                resource.getPath(), resource.getResourceType(), scriptResourcePath);
        }

        instantiateComponentClass();

        return EVAL_PAGE;
    }

    private void setBindings(final SlingHttpServletRequest slingRequest) {
        // add bedrock attributes to page context
        final BedrockBindings bindings = new BedrockBindings(slingRequest);

        for (final Map.Entry<String, Object> entry : bindings.entrySet()) {
            pageContext.setAttribute(entry.getKey(), entry.getValue());
        }
    }

    private void instantiateComponentClass() throws JspTagException {
        final Component component = (Component) pageContext.getAttribute(DEFAULT_COMPONENT_NAME);

        if (component != null) {
            final String className = component.getProperties().get(PROPERTY_CLASS_NAME, String.class);

            final Class<?> clazz;

            try {
                clazz = className == null ? null : Class.forName(className);
            } catch (ClassNotFoundException e) {
                LOG.error("class not found for name = " + className, e);

                throw new JspTagException(e);
            }

            if (clazz != null) {
                setComponentInstance(clazz, className);
            } else {
                LOG.debug("class not found for component = {}, not instantiating component class",
                    component.getResourceType());
            }
        } else {
            LOG.debug("component is null, not instantiating component class");
        }
    }

    private void setComponentInstance(final Class<?> clazz, final String className) throws JspTagException {
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
    }
}
