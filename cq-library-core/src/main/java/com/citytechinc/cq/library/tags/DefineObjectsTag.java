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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspTagException;

/**
 * Add to and/or override attributes set in pageContext for use in JSPs.
 */
public final class DefineObjectsTag extends AbstractComponentInstanceTag {

    // attribute names

    public static final String ATTR_COMPONENT_REQUEST = "componentRequest";

    public static final String ATTR_COMPONENT_NODE = "componentNode";

    public static final String ATTR_COMPONENT_INSTANCE_NAME = "componentInstanceName";

    public static final String ATTR_CURRENT_PAGE = "currentPage";

    public static final String ATTR_IS_AUTHOR = "isAuthor";

    public static final String ATTR_IS_EDIT_MODE = "isEditMode";

    public static final String ATTR_IS_DESIGN_MODE = "isDesignMode";

    public static final String ATTR_IS_PREVIEW_MODE = "isPreviewMode";

    public static final String ATTR_IS_PUBLISH = "isPublish";

    public static final String ATTR_PAGE_MANAGER = "pageManager";

    public static final String ATTR_SLING_REQUEST = "slingRequest";

    public static final String ATTR_SLING_RESPONSE = "slingResponse";

    public static final String ATTR_IS_DEBUG = "isDebug";

    public static final String PARAMETER_DEBUG = "debug";

    private static final Logger LOG = LoggerFactory.getLogger(DefineObjectsTag.class);

    private static final long serialVersionUID = 1L;

    @Override
    public int doEndTag() throws JspTagException {
        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) pageContext.getAttribute(
            ATTR_SLING_REQUEST);

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

            LOG.debug("doEndTag() instantiated component request for resource = {} with type = {}", resource.getPath(),
                resource.getResourceType());
        }

        pageContext.setAttribute(ATTR_COMPONENT_REQUEST, request);
        pageContext.setAttribute(ATTR_COMPONENT_NODE, request.getComponentNode());
        pageContext.setAttribute(ATTR_PAGE_MANAGER, request.getPageManager());
        pageContext.setAttribute(ATTR_CURRENT_PAGE, request.getCurrentPage());

        instantiateComponentClass(request);

        return EVAL_PAGE;
    }

    private void instantiateComponentClass(final ComponentRequest request) {
        try {
            final Component component = request.getComponent();

            if (component != null) {
                final String className = component.getProperties().get(ComponentConstants.PROPERTY_CLASS_NAME,
                    String.class);

                if (className != null) {
                    final Class<?> clazz = Class.forName(className);

                    if (clazz.isAnnotationPresent(AutoInstantiate.class)) {
                        final String instanceName = getInstanceName(clazz);
                        final Object instance = getInstance(clazz);

                        pageContext.setAttribute(instanceName, instance);
                        pageContext.setAttribute(ATTR_COMPONENT_INSTANCE_NAME, instanceName);
                    } else {
                        LOG.debug(
                            "instantiateComponentClass() annotation not present for class name = {}, not instantiating component class",
                            className);
                    }
                } else {
                    LOG.debug(
                        "instantiateComponentClass() class name attribute not present for component = {}, not instantiating component class",
                        component.getResourceType());
                }
            } else {
                LOG.debug("instantiateComponentClass() component is null, not instantiating component class");
            }
        } catch (Exception e) {
            LOG.error("error instantiating component class", e);
        }
    }

    private String getInstanceName(final Class<?> clazz) {
        final AutoInstantiate autoInstantiate = clazz.getAnnotation(AutoInstantiate.class);
        final String instanceName;

        if (autoInstantiate.instanceName().isEmpty()) {
            instanceName = StringUtils.uncapitalize(clazz.getSimpleName());
        } else {
            instanceName = autoInstantiate.instanceName();
        }

        LOG.debug("getInstanceName() component instance name = {}", instanceName);

        return instanceName;
    }
}
