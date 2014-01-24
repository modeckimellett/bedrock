/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags.xss;

import com.adobe.granite.xss.XSSAPI;

public final class ValidDimensionTag extends AbstractXssTag {

    private String dimension;

    private String defaultValue;

    @Override
    protected String getOutput(final XSSAPI xssApi) {
        return xssApi.getValidDimension(dimension, defaultValue);
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(final String dimension) {
        this.dimension = dimension;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
