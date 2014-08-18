package com.citytechinc.aem.bedrock.core.tags

import javax.servlet.jsp.JspException
import javax.servlet.jsp.JspTagException
import javax.servlet.jsp.PageContext
import javax.servlet.jsp.tagext.TagSupport

import static com.google.common.base.Preconditions.checkArgument

/**
 * Base class for scoped tag handlers containing a "scope" attribute corresponding to a <code>PageContext</code> scope
 * constant.
 */
abstract class AbstractScopedTag extends TagSupport {

    private static final def SCOPES = [
        "page"       : PageContext.PAGE_SCOPE,
        "request"    : PageContext.REQUEST_SCOPE,
        "session"    : PageContext.SESSION_SCOPE,
        "application": PageContext.APPLICATION_SCOPE]

    /**
     * Scope of instance in page context.  Defaults to "page".
     */
    private String scope

    /**
     * @param scope scope value
     * @return tag result
     * @throws javax.servlet.jsp.JspTagException if error occurs in tag operation
     */
    abstract int doEndTag(int scope) throws JspTagException

    @Override
    final int doEndTag() throws JspException {
        checkArgument(!scope || SCOPES[scope], "scope attribute is invalid = %s, must be one of %s", scope,
            SCOPES.keySet())

        doEndTag(!scope ? PageContext.PAGE_SCOPE : SCOPES[scope])
    }

    final void setScope(String scope) {
        this.scope = scope
    }
}
