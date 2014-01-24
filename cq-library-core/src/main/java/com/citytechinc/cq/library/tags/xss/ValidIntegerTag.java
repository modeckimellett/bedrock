/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags.xss;

import com.adobe.granite.xss.XSSAPI;

public final class ValidIntegerTag extends AbstractXssTag {

    private String integer;

    private int defaultValue;

    @Override
    protected String getOutput(final XSSAPI xssApi) {
        return String.valueOf(xssApi.getValidInteger(integer, defaultValue));
    }

    public String getInteger() {
        return integer;
    }

    public void setInteger(final String integer) {
        this.integer = integer;
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(final int defaultValue) {
        this.defaultValue = defaultValue;
    }
}
