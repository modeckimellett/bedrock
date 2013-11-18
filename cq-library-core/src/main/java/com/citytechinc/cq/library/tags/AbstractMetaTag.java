/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags;

import com.adobe.granite.xss.XSSAPI;
import com.day.cq.commons.Doctype;

import static com.day.cq.wcm.tags.DefineObjectsTEI.ATTR_XSSAPI_NAME;

public abstract class AbstractMetaTag extends AbstractPropertyTag {

    @SuppressWarnings("deprecation")
    protected String getTagEnd() {
        final StringBuilder builder = new StringBuilder();

        builder.append('"');
        builder.append(Doctype.isXHTML(pageContext.getRequest()) ? "/" : "");
        builder.append('>');

        return builder.toString();
    }

    protected final XSSAPI getXssApi() {
        return (XSSAPI) pageContext.getAttribute(ATTR_XSSAPI_NAME);
    }
}
