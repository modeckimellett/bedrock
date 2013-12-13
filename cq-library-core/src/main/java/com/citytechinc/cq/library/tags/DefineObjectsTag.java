/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags;

import com.citytechinc.cq.library.components.annotations.AutoInstantiate;
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
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Add to and/or override attributes set in pageContext for use in JSPs.
 */
public final class DefineObjectsTag extends TagSupport {

    // attribute names

    public static final String ATTR_COMPONENT_REQUEST = "componentRequest";

    public static final String ATTR_COMPONENT_NODE = "componentNode";

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

    /**
     * Property name in component descriptor containing annotated class name.
     */
    private static final String PROPERTY_COMPONENT_CLASS_NAME = "className";

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

        final ComponentRequest componentRequest = new DefaultComponentRequest(pageContext);

        if (LOG.isDebugEnabled()) {
            final Resource resource = componentRequest.getResource();

            LOG.debug("doEndTag() instantiated component request for resource = {} with type = {}", resource.getPath(),
                resource.getResourceType());
        }

        pageContext.setAttribute(ATTR_COMPONENT_REQUEST, componentRequest);
        pageContext.setAttribute(ATTR_COMPONENT_NODE, componentRequest.getComponentNode());
        pageContext.setAttribute(ATTR_PAGE_MANAGER, componentRequest.getPageManager());
        pageContext.setAttribute(ATTR_CURRENT_PAGE, componentRequest.getCurrentPage());

        instantiateComponent(componentRequest);

        return EVAL_PAGE;
    }

    private void instantiateComponent(final ComponentRequest componentRequest) {
        final Component component = componentRequest.getComponent();

        if (component != null) {
            final String className = component.getProperties().get(PROPERTY_COMPONENT_CLASS_NAME, "");

            if (!className.isEmpty()) {
                try {
                    final Class<?> clazz = Class.forName(className);

                    if (clazz.isAnnotationPresent(AutoInstantiate.class)) {
                        LOG.debug("instantiateComponent() instantiating component for class name = {}", className);

                        final AutoInstantiate autoInstantiate = clazz.getAnnotation(AutoInstantiate.class);
                        final String instanceName = autoInstantiate.instanceName();

                        final Object instance = clazz.getConstructor(ComponentRequest.class).newInstance(
                            componentRequest);

                        if (instanceName.isEmpty()) {
                            pageContext.setAttribute(StringUtils.uncapitalize(clazz.getSimpleName()), instance);
                        } else {
                            pageContext.setAttribute(instanceName, instance);
                        }
                    } else {
                        LOG.debug(
                            "instantiateComponent() auto instantiate annotation not present for class name = {}, skipping",
                            className);
                    }
                } catch (Exception e) {
                    LOG.error("error instantiating component for class name = " + className, e);
                }
            }
        }
    }
}
