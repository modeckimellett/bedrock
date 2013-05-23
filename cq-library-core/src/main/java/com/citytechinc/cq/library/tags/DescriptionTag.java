/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspTagException;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public final class DescriptionTag extends AbstractPropertyTag {

    private static final Logger LOG = LoggerFactory.getLogger(DescriptionTag.class);

    private static final long serialVersionUID = 1L;

    private String suffix;

    @Override
    public int doEndTag() throws JspTagException {
        final Page currentPage = (Page) pageContext.getAttribute(DefineObjectsTag.ATTR_CURRENT_PAGE);

        final ValueMap properties = currentPage.getProperties();

        final StringBuilder builder = new StringBuilder();

        final String description;

        if (hasPropertyName()) {
            description = properties.get(propertyName, "");
        } else {
            description = properties.get("jcr:description", "");
        }

        if (isNotEmpty(description)) {
            builder.append(description);
            builder.append(suffix);
        }

        try {
            pageContext.getOut().write(builder.toString());
        } catch (IOException ioe) {
            LOG.error("error writing description for page = " + currentPage.getPath(), ioe);

            throw new JspTagException(ioe);
        }

        return EVAL_PAGE;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(final String suffix) {
        this.suffix = suffix;
    }
}
