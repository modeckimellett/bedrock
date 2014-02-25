/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.tags;

import com.citytechinc.aem.bedrock.content.node.ComponentNode;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspTagException;
import java.io.IOException;

/**
 * Render an href attribute value for the given property name.
 */
public final class HrefTag extends AbstractPropertyTag {

    private static final Logger LOG = LoggerFactory.getLogger(HrefTag.class);

    private static final long serialVersionUID = 1L;

    /**
     * Default value if property does not exist.
     */
    private String defaultValue = "";

    @Override
    public int doEndTag() throws JspTagException {
        final ComponentNode componentNode = getComponentNode();

        final Optional<String> optionalHref;

        if (isInherit()) {
            optionalHref = componentNode.getAsHrefInherited(propertyName);
        } else {
            optionalHref = componentNode.getAsHref(propertyName);
        }

        final String href = optionalHref.or(defaultValue);

        try {
            pageContext.getOut().write(href);
        } catch (IOException e) {
            LOG.error("error writing href = " + href, e);

            throw new JspTagException(e);
        }

        return EVAL_PAGE;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
