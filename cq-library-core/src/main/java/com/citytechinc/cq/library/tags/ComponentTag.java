/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags;

import com.citytechinc.cq.library.content.request.ComponentRequest;
import com.citytechinc.cq.library.content.request.impl.DefaultComponentRequest;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspTagException;

import static com.citytechinc.cq.library.tags.DefineObjectsTag.ATTR_SLING_REQUEST;

/**
 * Instantiates a Component class and sets it in JSP page context.
 */
public final class ComponentTag extends AbstractScopedTag {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentTag.class);

    private static final long serialVersionUID = 1L;

    private static final String ATTR_ADMIN_RESOURCE_RESOLVER = "admin-resource-resolver";

    /**
     * Component class to instantiate.
     */
    private String className;

    /**
     * Name to set in pageContext for the component class.
     */
    private String name;

    /**
     * Bind this component instance to an admin session. This is useful for adapting to services that require
     * administrator privileges and should not be constrained by a user's permissions. For example, accessing user
     * profile data in publish. The admin session and resource resolver will be available via the getters on the
     * ComponentRequest.
     */
    private String admin;

    @Override
    public int doEndTag() throws JspTagException {
        if (Boolean.valueOf(admin)) {
            final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) pageContext.getAttribute(
                ATTR_SLING_REQUEST);

            final ResourceResolver resourceResolver = (ResourceResolver) slingRequest.getAttribute(
                ATTR_ADMIN_RESOURCE_RESOLVER);

            if (resourceResolver != null) {
                resourceResolver.close();
            }

            LOG.debug("doEndTag() closed admin resource resolver for component = {}", getResourcePath());
        }

        return EVAL_PAGE;
    }

    @Override
    public int doStartTag() throws JspTagException {
        LOG.debug("doStartTag() class name = {}, attribute name = {}", className, name);

        checkScopeAttribute();

        try {
            final ComponentRequest request;

            if (Boolean.valueOf(admin)) {
                final ResourceResolver resourceResolver = getAdminResourceResolver();

                LOG.debug("doStartTag() created admin session for path = {}", getResourcePath());

                request = new DefaultComponentRequest(pageContext, resourceResolver);
            } else {
                request = (ComponentRequest) pageContext.getAttribute(DefineObjectsTag.ATTR_COMPONENT_REQUEST);
            }

            final Object component = Class.forName(className).getConstructor(ComponentRequest.class).newInstance(
                request);

            pageContext.setAttribute(name, component, getScopeValue());
        } catch (Exception e) {
            LOG.error("error instantiating class = " + className, e);
            throw new JspTagException(e);
        }

        return EVAL_BODY_INCLUDE;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(final String className) {
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    private ResourceResolver getAdminResourceResolver() throws LoginException {
        final SlingScriptHelper sling = (SlingScriptHelper) pageContext.getAttribute(SlingBindings.SLING);

        final ResourceResolverFactory resourceResolverFactory = sling.getService(ResourceResolverFactory.class);
        final ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);

        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) pageContext.getAttribute(
            ATTR_SLING_REQUEST);

        slingRequest.setAttribute(ATTR_ADMIN_RESOURCE_RESOLVER, resourceResolver);

        return resourceResolver;
    }

    private String getResourcePath() {
        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) pageContext.getAttribute(
            ATTR_SLING_REQUEST);

        return slingRequest.getResource().getPath();
    }
}
