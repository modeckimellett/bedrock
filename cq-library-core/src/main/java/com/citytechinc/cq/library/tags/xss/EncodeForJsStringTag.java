/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags.xss;

import com.adobe.granite.xss.XSSAPI;

public final class EncodeForJsStringTag extends AbstractXssSourceTag {

    @Override
    protected String getOutput(final XSSAPI xssApi) {
        return xssApi.encodeForJSString(source);
    }
}
