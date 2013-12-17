/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags;

import com.citytechinc.cq.library.components.annotations.AutoInstantiate;
import com.citytechinc.cq.library.constants.ComponentConstants;
import com.citytechinc.cq.library.content.request.ComponentRequest;
import com.citytechinc.cq.library.content.request.impl.DefaultComponentRequest;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.components.Component;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspTagException;

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_CURRENT_PAGE_NAME;
import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_PAGE_MANAGER_NAME;
import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_REQUEST_NAME;
import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_SLING_NAME;

/**
 * Add to and/or override attributes set in pageContext for use in JSPs.
 */
public final class DefineObjectsTag extends AbstractComponentInstanceTag {

    // attribute names

    public static final String ATTR_COMPONENT_REQUEST = "componentRequest";

    public static final String ATTR_COMPONENT_NODE = "componentNode";

    public static final String ATTR_COMPONENT_INSTANCE_NAME = "componentInstanceName";

    public static final String ATTR_IS_AUTHOR = "isAuthor";

    public static final String ATTR_IS_EDIT_MODE = "isEditMode";

    public static final String ATTR_IS_DESIGN_MODE = "isDesignMode";

    public static final String ATTR_IS_PREVIEW_MODE = "isPreviewMode";

    public static final String ATTR_IS_PUBLISH = "isPublish";

    public static final String ATTR_IS_DEBUG = "isDebug";

    public static final String PARAMETER_DEBUG = "debug";

    private static final Logger LOG = LoggerFactory.getLogger(DefineObjectsTag.class);

    private static final long serialVersionUID = 1L;

    @Override
    public int doEndTag() throws JspTagException {
        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) pageContext.getAttribute(
            DEFAULT_REQUEST_NAME);

        final WCMMode mode = WCMMode.fromRequest(slingRequest);
        final boolean isPublish = mode.equals(WCMMode.DISABLED);

        pageContext.setAttribute(ATTR_IS_AUTHOR, !isPublish);
        pageContext.setAttribute(ATTR_IS_PUBLISH, isPublish);
        pageContext.setAttribute(ATTR_IS_EDIT_MODE, mode.equals(WCMMode.EDIT));
        pageContext.setAttribute(ATTR_IS_DESIGN_MODE, mode.equals(WCMMode.DESIGN));
        pageContext.setAttribute(ATTR_IS_PREVIEW_MODE, mode.equals(WCMMode.PREVIEW));
        pageContext.setAttribute(ATTR_IS_DEBUG, Boolean.valueOf(slingRequest.getParameter(PARAMETER_DEBUG)));

        final ComponentRequest request = new DefaultComponentRequest(pageContext);

        if (LOG.isDebugEnabled()) {
            final Resource resource = request.getResource();
            final SlingScriptHelper sling = (SlingScriptHelper) pageContext.getAttribute(DEFAULT_SLING_NAME);
            final String scriptResourcePath = sling.getScript().getScriptResource().getPath();

            LOG.debug("doEndTag() instantiated component request for resource path = {} with type = {} and script = {}",
                new Object[]{ resource.getPath(), resource.getResourceType(), scriptResourcePath });
        }

        pageContext.setAttribute(ATTR_COMPONENT_REQUEST, request);
        pageContext.setAttribute(ATTR_COMPONENT_NODE, request.getComponentNode());
        pageContext.setAttribute(DEFAULT_PAGE_MANAGER_NAME, request.getPageManager());
        pageContext.setAttribute(DEFAULT_CURRENT_PAGE_NAME, request.getCurrentPage());

        instantiateComponentClass(request);

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
            LOG.debug("instantiateComponentClass() component is null, not instantiating component class");
        }
    }

    private void setInstance(final Component component) throws Exception {
        final String className = component.getProperties().get(ComponentConstants.PROPERTY_CLASS_NAME, String.class);

        if (className != null) {
            final Class<?> clazz = Class.forName(className);

            if (clazz.isAnnotationPresent(AutoInstantiate.class)) {
                final String instanceName = getInstanceName(clazz);
                final Object instance = getInstance(clazz);

                LOG.debug("setInstance() class name = {}, instance name = {}, setting component in page context",
                    className, instanceName);

                pageContext.setAttribute(instanceName, instance);
                pageContext.setAttribute(ATTR_COMPONENT_INSTANCE_NAME, instanceName);
            } else {
                LOG.debug("setInstance() annotation not present for class name = {}, not instantiating component class",
                    className);
            }
        } else {
            LOG.debug("setInstance() no class name property for component = {}, not instantiating component class",
                component.getResourceType());
        }
    }

    private String getInstanceName(final Class<?> clazz) {
        final AutoInstantiate autoInstantiate = clazz.getAnnotation(AutoInstantiate.class);
        final String instanceName = autoInstantiate.instanceName();

        return instanceName.isEmpty() ? StringUtils.uncapitalize(clazz.getSimpleName()) : instanceName;
    }
}
