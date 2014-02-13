/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags;

import com.day.cq.widget.HtmlLibraryManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_REQUEST_NAME;
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_SLING_NAME;

public final class DeferredClientLibTag extends AbstractDeferredClientLibTag {

    private static final Logger LOG = LoggerFactory.getLogger(DeferredClientLibTag.class);

    @Override
    public int doEndTag() throws JspException {
        final SlingScriptHelper sling = (SlingScriptHelper) pageContext.getAttribute(
            DEFAULT_SLING_NAME);
        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) pageContext
            .getAttribute(DEFAULT_REQUEST_NAME);

        final HtmlLibraryManager htmlLibraryManager = sling.getService(HtmlLibraryManager.class);

        final List<String> requestCategories = getRequestCategories();
        final Set<String> uniqueCategories = new LinkedHashSet<String>(requestCategories);

        LOG.debug("doEndTag() writing deferred client libraries = {}", uniqueCategories);

        try {
            htmlLibraryManager.writeJsInclude(slingRequest, pageContext.getOut(),
                uniqueCategories.toArray(new String[uniqueCategories.size()]));
        } catch (IOException e) {
            LOG.error("error writing deferred client libraries = " + uniqueCategories, e);
        }

        return EVAL_PAGE;
    }
}
