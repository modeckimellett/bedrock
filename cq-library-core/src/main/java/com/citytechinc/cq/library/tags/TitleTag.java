/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags;

import com.day.cq.wcm.api.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspTagException;
import java.io.IOException;

public final class TitleTag extends AbstractPropertyTag {

    private static final Logger LOG = LoggerFactory.getLogger(TitleTag.class);

    private static final long serialVersionUID = 1L;

    private String suffix;

    @Override
    public int doEndTag() throws JspTagException {
        final Page currentPage = (Page) pageContext.getAttribute(DefineObjectsTag.ATTR_CURRENT_PAGE);

        final String title = currentPage.getTitle() == null ? currentPage.getName() : currentPage.getTitle();

        final StringBuilder builder = new StringBuilder();

        if (hasPropertyName()) {
            builder.append(currentPage.getProperties().get(propertyName, title));
        } else {
            builder.append(title);
        }

        builder.append(suffix);

        final String content = builder.toString();

        LOG.debug("doEndTag() title content = {}", content);

        try {
            pageContext.getOut().write(content);
        } catch (IOException ioe) {
            LOG.error("error writing title content = " + content, ioe);

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
