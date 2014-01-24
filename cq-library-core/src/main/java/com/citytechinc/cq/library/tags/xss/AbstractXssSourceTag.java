/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags.xss;

public abstract class AbstractXssSourceTag extends AbstractXssTag {

    protected String source;

    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
    }
}
