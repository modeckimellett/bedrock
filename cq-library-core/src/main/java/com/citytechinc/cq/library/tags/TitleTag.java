/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags;

import com.day.cq.wcm.api.Page;
import com.google.common.escape.Escaper;
import com.google.common.html.HtmlEscapers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspTagException;
import java.io.IOException;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Render the title for the current page.
 */
public final class TitleTag extends AbstractPropertyTag {

    private static final Logger LOG = LoggerFactory.getLogger(TitleTag.class);

    private static final long serialVersionUID = 1L;

    private static final String TAG_START = "<title>";

    private static final String TAG_END = "</title>";

    private static final Escaper ESCAPER = HtmlEscapers.htmlEscaper();

    private String suffix;

    @Override
    public int doEndTag() throws JspTagException {
        final Page currentPage = (Page) pageContext.getAttribute(DefineObjectsTag.ATTR_CURRENT_PAGE);

        final String title = isNullOrEmpty(currentPage.getTitle()) ? currentPage.getName() : currentPage.getTitle();

        final StringBuilder builder = new StringBuilder();

        builder.append(TAG_START);

        if (hasPropertyName()) {
            builder.append(ESCAPER.escape(currentPage.getProperties().get(propertyName, title)));
        } else {
            builder.append(ESCAPER.escape(title));
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
