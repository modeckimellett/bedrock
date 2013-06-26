/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags;

import static com.google.common.base.Strings.isNullOrEmpty;

public abstract class AbstractPropertyTag extends AbstractComponentTag {

    private static final long serialVersionUID = 1L;

    protected String propertyName;

    public final String getPropertyName() {
        return propertyName;
    }

    public final void setPropertyName(final String propertyName) {
        this.propertyName = propertyName;
    }

    protected final boolean hasPropertyName() {
        return !isNullOrEmpty(propertyName);
    }
}
