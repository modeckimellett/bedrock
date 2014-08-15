package com.citytechinc.aem.bedrock.core.tags;

import com.citytechinc.aem.bedrock.api.node.ComponentNode;
import org.apache.sling.api.resource.Resource;

import javax.servlet.jsp.tagext.TagSupport;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_RESOURCE_NAME;

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
        final Resource resource = (Resource) pageContext.getAttribute(DEFAULT_RESOURCE_NAME);

        return resource.adaptTo(ComponentNode.class);
    }

    protected final boolean isInherit() {
        return isNullOrEmpty(inherit) ? false : Boolean.valueOf(inherit);
    }

    public final void setInherit(final String inherit) {
        this.inherit = inherit;
    }
}
