/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Base class for component tag handlers that access a JCR property.
 */
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
