/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspTagException;

/**
 * Instantiates a Component class and sets it in JSP page context.
 */
public final class ComponentTag extends AbstractComponentInstanceTag {

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
    public int doEndTag() throws JspTagException {
        LOG.debug("doEndTag() class name = {}, attribute name = {}", className, name);

        checkScopeAttribute();

        try {
            final Object component = getInstance(className);

            pageContext.setAttribute(name, component, getScopeValue());
        } catch (Exception e) {
            LOG.error("error instantiating component for class name = " + className, e);

            throw new JspTagException(e);
        }

        return EVAL_PAGE;
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
