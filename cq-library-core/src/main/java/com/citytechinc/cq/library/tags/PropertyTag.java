/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags;

import com.citytechinc.cq.library.content.node.ComponentNode;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspTagException;
import java.io.IOException;

/**
 * Render a page or component property.
 */
public final class PropertyTag extends AbstractPropertyTag {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyTag.class);

    private static final long serialVersionUID = 1L;

    /**
     * Default value if property does not exist.
     */
    private String defaultValue = "";

    /**
     * Should XML be escaped? Defaults to true.
     */
    private String escapeXml;

    @Override
    public int doEndTag() throws JspTagException {
        final ComponentNode componentNode = getComponentNode();

        final String value;

        if (isInherit()) {
            value = componentNode.getInherited(propertyName, defaultValue);
        } else {
            value = componentNode.get(propertyName, defaultValue);
        }

        final boolean escapeXml = StringUtils.isEmpty(this.escapeXml) ? true : Boolean.valueOf(this.escapeXml);

        try {
            if (escapeXml) {
                pageContext.getOut().write(StringEscapeUtils.escapeXml(value));
            } else {
                pageContext.getOut().write(value);
            }
        } catch (IOException ioe) {
            LOG.error("error writing property value = " + value + " for name = " + propertyName, ioe);

            throw new JspTagException(ioe);
        }

        return EVAL_PAGE;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getEscapeXml() {
        return escapeXml;
    }

    public void setEscapeXml(final String escapeXml) {
        this.escapeXml = escapeXml;
    }
}
