/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.commons.WCMUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

/**
 * Render the keywords for the current page.
 */
public final class KeywordsTag extends TagSupport {

    private static final Logger LOG = LoggerFactory.getLogger(KeywordsTag.class);

    private static final long serialVersionUID = 1L;

    @Override
    public int doEndTag() throws JspTagException {
        final Page currentPage = (Page) pageContext.getAttribute(DefineObjectsTag.ATTR_CURRENT_PAGE);

        try {
            pageContext.getOut().write(escapeHtml4(WCMUtils.getKeywords(currentPage, false)));
        } catch (IOException ioe) {
            LOG.error("error writing keywords for page = " + currentPage.getPath(), ioe);

            throw new JspTagException(ioe);
        }

        return EVAL_PAGE;
    }
}
