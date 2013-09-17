/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags;

import com.citytechinc.cq.library.content.node.ComponentNode;

import javax.servlet.jsp.tagext.TagSupport;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Base class for tag handlers that access a <code>ComponentNode</code> from the current context.
 */
public abstract class AbstractComponentTag extends TagSupport {

    private static final long serialVersionUID = 1L;

    /**
     * Should property value be inherited? Defaults to false.
     */
    private String inherit;

    public final String getInherit() {
        return inherit;
    }

    protected final ComponentNode getComponentNode() {
        return (ComponentNode) pageContext.getAttribute(DefineObjectsTag.ATTR_COMPONENT_NODE);
    }

    protected final boolean isInherit() {
        return isNullOrEmpty(inherit) ? false : Boolean.valueOf(inherit);
    }

    public final void setInherit(final String inherit) {
        this.inherit = inherit;
    }
}
