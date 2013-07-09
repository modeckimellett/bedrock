/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags;

import com.citytechinc.cq.library.content.request.ComponentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspTagException;

/**
 * Instantiates a Component class and sets it in JSP page context.
 */
public final class ComponentTag extends AbstractScopedTag {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentTag.class);

    private static final long serialVersionUID = 1L;

    /**
     * Component class to instantiate.
     */
    private String className;

    /**
     * Name to set in pageContext for the component class.
     */
    private String name;

    @Override
    public int doStartTag() throws JspTagException {
        LOG.debug("doStartTag() class name = {}, attribute name = {}", className, name);

        checkScopeAttribute();

        try {
            final ComponentRequest request = (ComponentRequest) pageContext.getAttribute(
                DefineObjectsTag.ATTR_COMPONENT_REQUEST);

            final Object component = Class.forName(className).getConstructor(ComponentRequest.class).newInstance(
                request);

            pageContext.setAttribute(name, component, getScopeValue());
        } catch (Exception e) {
            LOG.error("error instantiating class = " + className, e);
            throw new JspTagException(e);
        }

        return EVAL_BODY_INCLUDE;
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
}
