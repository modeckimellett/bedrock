/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags;

import com.day.cq.wcm.api.WCMMode;
import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

public final class DisableAuthorTag extends TagSupport {

    private static final Logger LOG = LoggerFactory.getLogger(DisableAuthorTag.class);

    private static final long serialVersionUID = 1L;

    private static final String ATTR_PREVIOUS_WCMMODE = "previousWcmMode_";

    @Override
    public int doStartTag() throws JspException {
        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) pageContext.getAttribute(
            DefineObjectsTag.ATTR_SLING_REQUEST);

        final String path = slingRequest.getResource().getPath();

        LOG.debug("doStartTag() disabling authoring for path = {}", path);

        slingRequest.setAttribute(ATTR_PREVIOUS_WCMMODE + path, WCMMode.fromRequest(slingRequest));

        WCMMode.DISABLED.toRequest(slingRequest);

        return EVAL_BODY_INCLUDE;
    }

    @Override
    public int doEndTag() throws JspTagException {
        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) pageContext.getAttribute(
            DefineObjectsTag.ATTR_SLING_REQUEST);

        final String path = slingRequest.getResource().getPath();

        final WCMMode mode = (WCMMode) slingRequest.getAttribute(ATTR_PREVIOUS_WCMMODE + path);

        LOG.debug("doEndTag() restoring mode = {} for path = {}", mode.name(), path);

        mode.toRequest(slingRequest);

        return EVAL_PAGE;
    }
}
