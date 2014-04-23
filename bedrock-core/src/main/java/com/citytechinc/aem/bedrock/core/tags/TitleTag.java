/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.core.tags;

import com.day.cq.wcm.api.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspTagException;
import java.io.IOException;

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_CURRENT_PAGE_NAME;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Render the title for the current page.
 */
public final class TitleTag extends AbstractMetaTag {

    private static final Logger LOG = LoggerFactory.getLogger(TitleTag.class);

    private static final long serialVersionUID = 1L;

    private static final String TAG_START = "<title>";

    private static final String TAG_END = "</title>";

    private String suffix;

    @Override
    public int doEndTag() throws JspTagException {
        final Page currentPage = (Page) pageContext.getAttribute(DEFAULT_CURRENT_PAGE_NAME);
        final String pageTitle = isNullOrEmpty(currentPage.getTitle()) ? currentPage.getName() : currentPage.getTitle();

        final StringBuilder builder = new StringBuilder();

        builder.append(TAG_START);

        final StringBuilder title = new StringBuilder();

        if (hasPropertyName()) {
            title.append(currentPage.getProperties().get(propertyName, pageTitle));
        } else {
            title.append(pageTitle);
        }

        if (!isNullOrEmpty(suffix)) {
            title.append(suffix);
        }

        builder.append(getXssApi().encodeForHTML(title.toString()));
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
