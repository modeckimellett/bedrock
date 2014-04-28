package com.citytechinc.aem.bedrock.core.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base class for scoped tag handlers containing a "scope" attribute corresponding to a <code>PageContext</code> scope
 * constant.
 */
public abstract class AbstractScopedTag extends TagSupport {

    private static final Map<String, Integer> SCOPES;

    /**
     * Scope of instance in page context.  Defaults to "page".
     */
    private String scope;

    static {
        SCOPES = new HashMap<String, Integer>();
        SCOPES.put("page", PageContext.PAGE_SCOPE);
        SCOPES.put("request", PageContext.REQUEST_SCOPE);
        SCOPES.put("session", PageContext.SESSION_SCOPE);
        SCOPES.put("application", PageContext.APPLICATION_SCOPE);
    }

    /**
     * @param scope scope value
     * @return tag result
     * @throws JspTagException if error occurs in tag operation
     */
    public abstract int doEndTag(final int scope) throws JspTagException;

    @Override
    public final int doEndTag() throws JspException {
        final Integer scopeValue = scope == null ? PageContext.PAGE_SCOPE : SCOPES.get(scope);

        checkNotNull(scopeValue, "scope attribute is invalid = " + scope + ", must be one of " + SCOPES.keySet());

        return doEndTag(scopeValue);
    }

    public final void setScope(final String scope) {
        this.scope = scope;
    }
}
