/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.api.link.enums;

import com.google.common.base.Enums;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Enumeration of link target values.
 */
public enum LinkTarget {
    BLANK("_blank"),
    PARENT("_parent"),
    SELF("_self"),
    TOP("_top");

    private final String target;

    private LinkTarget(final String target) {
        this.target = target;
    }

    /**
     * @param target target value
     * @return LinkTarget enum object
     */
    public static LinkTarget forTarget(final String target) {
        checkNotNull(target);
        checkArgument(target.length() > 1);

        return Enums.getIfPresent(LinkTarget.class, target.substring(1).toUpperCase()).or(LinkTarget.SELF);
    }

    /**
     * @return target value
     */
    public String getTarget() {
        return target;
    }
}
