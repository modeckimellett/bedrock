/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags;

import com.citytechinc.cq.library.content.node.ComponentNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspTagException;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public final class PropertyBooleanTag extends AbstractPropertyTag {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyTag.class);

    private static final long serialVersionUID = 1L;

    /**
     * Default value if property does not exist.
     */
    private String defaultValue;

    private String f;

    private String t;

    @Override
    public int doEndTag() throws JspTagException {
        final ComponentNode componentNode = getComponentNode();

        final boolean value;

        if (isInherit()) {
            if (isEmpty(defaultValue)) {
                value = componentNode.getInherited(propertyName, false);
            } else {
                value = componentNode.getInherited(propertyName, Boolean.valueOf(defaultValue));
            }
        } else {
            if (isEmpty(defaultValue)) {
                value = componentNode.get(propertyName, false);
            } else {
                value = componentNode.get(propertyName, Boolean.valueOf(defaultValue));
            }
        }

        final String result = value ? getResult(value, t) : getResult(value, f);

        try {
            pageContext.getOut().write(result);
        } catch (IOException ioe) {
            LOG.error("error writing property value = " + value, ioe);

            throw new JspTagException(ioe);
        }

        return EVAL_PAGE;
    }

    private String getResult(final boolean value, final String text) {
        final String result;

        if (isEmpty(text)) {
            result = String.valueOf(value);
        } else {
            result = text;
        }

        return result;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getFalse() {
        return f;
    }

    public void setFalse(final String f) {
        this.f = f;
    }

    public String getTrue() {
        return t;
    }

    public void setTrue(final String t) {
        this.t = t;
    }
}
