/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags;

import com.citytechinc.cq.library.content.node.ComponentNode;

import javax.servlet.jsp.tagext.TagSupport;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public abstract class AbstractPropertyTag extends TagSupport {

    private static final long serialVersionUID = 1L;

    protected String propertyName;

    /**
     * Should property value be inherited? Defaults to false.
     */
    protected String inherit;

    protected final ComponentNode getComponentNode() {
        return (ComponentNode) pageContext.getAttribute(DefineObjectsTag.ATTR_COMPONENT_NODE);
    }

    protected final boolean isInherit() {
        return isEmpty(inherit) ? false : Boolean.valueOf(inherit);
    }

    protected final boolean hasPropertyName() {
        return isNotEmpty(propertyName);
    }

    public final String getInherit() {
        return inherit;
    }

    public final void setInherit(final String inherit) {
        this.inherit = inherit;
    }

    public final String getPropertyName() {
        return propertyName;
    }

    public final void setPropertyName(final String propertyName) {
        this.propertyName = propertyName;
    }
}
