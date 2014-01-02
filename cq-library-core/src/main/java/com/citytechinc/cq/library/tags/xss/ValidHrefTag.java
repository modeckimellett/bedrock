/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags.xss;

import com.adobe.granite.xss.XSSAPI;

public final class ValidHrefTag extends AbstractXssTag {

    private String url;

    @Override
    protected String getOutput(final XSSAPI xssApi) {
        return xssApi.getValidHref(url);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }
}
