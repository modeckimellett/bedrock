/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.filters;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.sling.SlingFilter;
import org.apache.felix.scr.annotations.sling.SlingFilterScope;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

@SlingFilter(label = "Login Page Filter",
    description = "Filters requests to a configurable set of pages to customize default Sling authentication messages.",
    scope = SlingFilterScope.REQUEST, metatype = true,
    order = Integer.MIN_VALUE)
public final class LoginPageFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(LoginPageFilter.class);

    private static final String PATH_LOGIN_PAGE = "/login";

    private static final String FAILURE_REASON = "j_reason";

    private static final String DEFAULT_FAILURE_MESSAGE = "User name and password do not match";

    private static final String DEFAULT_SESSION_TIMEOUT_MESSAGE = "Session timed out, please login again";

    private static final String[] DEFAULT_PATHS = ImmutableList.of(PATH_LOGIN_PAGE).toArray(new String[1]);

    @Property(label = "Failure Message", description = "Login failure message.", value = DEFAULT_FAILURE_MESSAGE)
    private static final String FAILURE_MESSAGE = "failure.message";

    @Property(label = "Session Timeout Message", description = "Authenticated session timeout message.",
        value = DEFAULT_SESSION_TIMEOUT_MESSAGE)
    private static final String SESSION_TIMEOUT_MESSAGE = "session.timeout.message";

    @Property(label = "Paths", description = "Paths to filter.", value = { PATH_LOGIN_PAGE })
    private static final String PATHS = "paths";

    private String failureMessage;

    private String sessionTimeoutMessage;

    private Set<String> paths;

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
        throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        if (isFilteredUri(httpServletRequest)) {
            LOG.debug("doFilter() filtered page requested, returning modified request");

            final HttpServletRequest modifiedRequest = new LoginHttpServletRequestWrapper(httpServletRequest);

            chain.doFilter(modifiedRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    private boolean isFilteredUri(final HttpServletRequest httpServletRequest) {
        final String uri = httpServletRequest.getRequestURI();

        return Iterables.any(paths, new Predicate<String>() {
            @Override
            public boolean apply(final String path) {
                return uri.startsWith(path);
            }
        });
    }

    @Activate
    @Modified
    protected void activate(final Map<String, Object> properties) throws LoginException {
        failureMessage = PropertiesUtil.toString(properties.get(FAILURE_MESSAGE), DEFAULT_FAILURE_MESSAGE);
        sessionTimeoutMessage = PropertiesUtil.toString(properties.get(SESSION_TIMEOUT_MESSAGE),
            DEFAULT_SESSION_TIMEOUT_MESSAGE);
        paths = Sets.newHashSet(PropertiesUtil.toStringArray(properties.get(PATHS), DEFAULT_PATHS));
    }

    class LoginHttpServletRequestWrapper extends HttpServletRequestWrapper {

        public LoginHttpServletRequestWrapper(final HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getParameter(final String name) {
            String value = super.getParameter(name);

            if (name.equals(FAILURE_REASON)) {
                if (DEFAULT_FAILURE_MESSAGE.equals(value)) {
                    value = failureMessage;
                } else if (DEFAULT_SESSION_TIMEOUT_MESSAGE.equals(value)) {
                    value = sessionTimeoutMessage;
                }
            }

            return value;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Map getParameterMap() {
            return Maps.transformEntries(super.getParameterMap(), new Maps.EntryTransformer() {
                @Override
                public Object transformEntry(final Object key, final Object value) {
                    final String name = (String) key;
                    final String[] values = (String[]) value;

                    Object transformedValue = values;

                    if (name.equals(FAILURE_REASON)) {
                        if (DEFAULT_FAILURE_MESSAGE.equals(values[0])) {
                            transformedValue = new String[]{ failureMessage };
                        } else if (DEFAULT_SESSION_TIMEOUT_MESSAGE.equals(values[0])) {
                            transformedValue = new String[]{ sessionTimeoutMessage };
                        }
                    }

                    return transformedValue;
                }
            });
        }

        @Override
        public String[] getParameterValues(final String name) {
            String[] values = super.getParameterValues(name);

            if (name.equals(FAILURE_REASON)) {
                if (DEFAULT_FAILURE_MESSAGE.equals(values[0])) {
                    values = new String[]{ failureMessage };
                } else if (DEFAULT_SESSION_TIMEOUT_MESSAGE.equals(values[0])) {
                    values = new String[]{ sessionTimeoutMessage };
                }
            }

            return values;
        }
    }
}
