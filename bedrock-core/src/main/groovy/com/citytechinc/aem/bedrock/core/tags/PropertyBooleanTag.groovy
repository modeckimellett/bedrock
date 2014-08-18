package com.citytechinc.aem.bedrock.core.tags

import groovy.util.logging.Slf4j

import javax.servlet.jsp.JspTagException

/**
 * Render a boolean property value, optionally setting arbitrary string values to use instead of "true" and "false".
 */
@Slf4j("LOG")
class PropertyBooleanTag extends AbstractPropertyTag {

    /**
     * Default value if property does not exist.
     */
    String defaultValue

    String f

    String t

    @Override
    int doEndTag() throws JspTagException {
        def defaultValue = this.defaultValue ? Boolean.valueOf(this.defaultValue) : false
        def value

        if (inherit) {
            value = componentNode.getInherited(propertyName, defaultValue)
        } else {
            value = componentNode.get(propertyName, defaultValue)
        }

        def text = value ? t : f
        def result = text ?: value as String

        try {
            pageContext.out.write(result)
        } catch (IOException ioe) {
            LOG.error "error writing property value = $value", ioe

            throw new JspTagException(ioe)
        }

        EVAL_PAGE
    }
}
