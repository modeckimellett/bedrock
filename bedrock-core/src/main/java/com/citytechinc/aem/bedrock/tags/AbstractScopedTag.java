/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.tags;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

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

    public final String getScope() {
        return scope;
    }

    public final void setScope(final String scope) {
        this.scope = scope;
    }

    protected final void checkScopeAttribute() {
        checkArgument(scope == null || SCOPES.containsKey(scope),
            "scope attribute is invalid = " + scope + ", must be one of " + SCOPES.keySet());
    }

    protected final int getScopeValue() {
        return scope == null ? PageContext.PAGE_SCOPE : SCOPES.get(scope);
    }
}
