/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags.xss;

import com.adobe.granite.xss.XSSAPI;

public final class EncodeForXmlTag extends AbstractXssSourceTag {

    @Override
    protected String getOutput(final XSSAPI xssApi) {
        return xssApi.encodeForXML(source);
    }
}
