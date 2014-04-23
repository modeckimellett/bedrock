/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.core.tags;

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;
import java.util.List;

import static javax.servlet.jsp.PageContext.REQUEST_SCOPE;

public final class IncludeDeferredClientLibTag extends AbstractDeferredClientLibTag {

    private static final Logger LOG = LoggerFactory.getLogger(IncludeDeferredClientLibTag.class);

    private static final Joiner JOINER = Joiner.on(',');

    @Override
    public int doEndTag() throws JspException {
        final List<String> categories = getRequestCategories();

        categories.addAll(getCategories());

        LOG.debug("doEndTag() client libraries for request = {}", categories);

        pageContext.setAttribute(ATTR_CATEGORIES, JOINER.join(categories), REQUEST_SCOPE);

        return EVAL_PAGE;
    }
}
