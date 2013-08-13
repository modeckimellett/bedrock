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

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

/**
 * Render the title for the current page.
 */
public final class TitleTag extends AbstractPropertyTag {

    private static final Logger LOG = LoggerFactory.getLogger(TitleTag.class);

    private static final long serialVersionUID = 1L;

    private static final String TAG_START = "<title>";

    private static final String TAG_END = "</title>";

    private String suffix;

    @Override
    public int doEndTag() throws JspTagException {
        final Page currentPage = (Page) pageContext.getAttribute(DefineObjectsTag.ATTR_CURRENT_PAGE);

        final String title = isNullOrEmpty(currentPage.getTitle()) ? currentPage.getName() : currentPage.getTitle();

        final StringBuilder builder = new StringBuilder();

        builder.append(TAG_START);

        if (hasPropertyName()) {
            builder.append(escapeHtml4(currentPage.getProperties().get(propertyName, title)));
        } else {
            builder.append(escapeHtml4(title));
        }

        if (!isNullOrEmpty(suffix)) {
            builder.append(suffix);
        }

        builder.append(TAG_END);

        try {
            pageContext.getOut().write(builder.toString());
        } catch (IOException ioe) {
            LOG.error("error writing title tag", ioe);

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
