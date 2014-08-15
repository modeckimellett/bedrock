package com.citytechinc.aem.bedrock.core.tags;

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
    public int doEndTag(final int scope) throws JspTagException {
        LOG.debug("class name = {}, attribute name = {}", className, name);

        pageContext.setAttribute(name, getInstance(className), scope);

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
