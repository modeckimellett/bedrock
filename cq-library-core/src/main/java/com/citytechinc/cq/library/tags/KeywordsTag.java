/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.commons.WCMUtils;
import com.google.common.escape.Escaper;
import com.google.common.html.HtmlEscapers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

/**
 * Render the keywords for the current page.
 */
public final class KeywordsTag extends TagSupport {

    private static final Logger LOG = LoggerFactory.getLogger(KeywordsTag.class);

    private static final long serialVersionUID = 1L;

    private static final String TAG_START = "<meta name=\"keywords\" content=\"";

    private static final String TAG_END = "\"/>";

    private static final Escaper ESCAPER = HtmlEscapers.htmlEscaper();

    @Override
    public int doEndTag() throws JspTagException {
        final Page currentPage = (Page) pageContext.getAttribute(DefineObjectsTag.ATTR_CURRENT_PAGE);

        final StringBuilder builder = new StringBuilder();

        builder.append(TAG_START);
        builder.append(ESCAPER.escape(WCMUtils.getKeywords(currentPage, false)));
        builder.append(TAG_END);

        try {
            pageContext.getOut().write(builder.toString());
        } catch (IOException ioe) {
            LOG.error("error writing keywords tag for page = " + currentPage.getPath(), ioe);

            throw new JspTagException(ioe);
        }

        return EVAL_PAGE;
    }
}
