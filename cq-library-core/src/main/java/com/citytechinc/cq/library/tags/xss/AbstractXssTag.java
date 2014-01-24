/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags.xss;

import com.adobe.granite.xss.XSSAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_XSSAPI_NAME;

/**
 * Base class for tags providing XSS support.
 */
public abstract class AbstractXssTag extends TagSupport {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractXssTag.class);

    @Override
    public int doEndTag() throws JspException {
        final XSSAPI xssApi = (XSSAPI) pageContext.getAttribute(DEFAULT_XSSAPI_NAME);

        final String value = getOutput(xssApi);

        try {
            pageContext.getOut().write(value);
        } catch (IOException e) {
            LOG.error("error writing XSS-escaped/validated value = " + value, e);

            throw new JspException(e);
        }

        return EVAL_PAGE;
    }

    /**
     * Get the output value routed through the XSS service.
     *
     * @param xssApi XSS service
     * @return XSS-escaped or validated output
     */
    protected abstract String getOutput(final XSSAPI xssApi);
}
