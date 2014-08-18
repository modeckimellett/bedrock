package com.citytechinc.aem.bedrock.core.tags

import groovy.util.logging.Slf4j

import javax.servlet.jsp.JspTagException

/**
 * Instantiates a Component class and sets it in JSP page context.
 */
@Slf4j("LOG")
final class ComponentTag extends AbstractComponentInstanceTag {

    /**
     * Component class to instantiate.
     */
    String className

    /**
     * Name to set in pageContext for the component class.
     */
    String name

    @Override
    int doEndTag(int scope) throws JspTagException {
        LOG.debug "class name = {}, attribute name = {}", className, name

        pageContext.setAttribute(name, getInstance(className), scope)

        EVAL_PAGE
    }
}
