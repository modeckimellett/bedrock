/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.request.impl;

import com.citytechinc.cq.library.content.node.ComponentNode;
import com.citytechinc.cq.library.content.page.PageDecorator;
import com.citytechinc.cq.library.content.page.PageManagerDecorator;
import com.citytechinc.cq.library.content.request.ComponentServletRequest;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMMode;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.jsp.PageContext;

import static com.citytechinc.cq.library.tags.DefineObjectsTag.ATTR_CURRENT_PAGE;
import static com.citytechinc.cq.library.tags.DefineObjectsTag.ATTR_SLING_REQUEST;
import static com.citytechinc.cq.library.tags.DefineObjectsTag.ATTR_SLING_RESPONSE;
import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_PROPERTIES_NAME;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_NODE_NAME;
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_RESOURCE_NAME;

public final class DefaultComponentServletRequest implements ComponentServletRequest {

    private static final Function<RequestParameter, String> REQUEST_PARAMETER_TO_STRING = new Function<RequestParameter, String>() {
        @Override
        public String apply(final RequestParameter parameter) {
            return parameter.getString();
        }
    };

    private static final Function<RequestParameter[], String[]> REQUEST_PARAMETERS_TO_STRING_ARRAY = new Function<RequestParameter[], String[]>() {
        @Override
        public String[] apply(final RequestParameter[] parameters) {
            return Lists.transform(Lists.newArrayList(parameters), REQUEST_PARAMETER_TO_STRING).toArray(
                new String[parameters.length]);
        }
    };

    private final ComponentNode componentNode;

    private final Node currentNode;

    private final PageDecorator currentPage;

    private final PageManagerDecorator pageManager;

    private final ValueMap properties;

    private final Resource resource;

    private final ResourceResolver resourceResolver;

    private final SlingHttpServletRequest slingRequest;

    private final SlingHttpServletResponse slingResponse;

    public DefaultComponentServletRequest(final SlingHttpServletRequest slingRequest,
        final SlingHttpServletResponse slingResponse) {
        this.slingRequest = slingRequest;
        this.slingResponse = slingResponse;

        resource = slingRequest.getResource();
        properties = ResourceUtil.getValueMap(resource);
        currentNode = resource.adaptTo(Node.class);
        resourceResolver = slingRequest.getResourceResolver();
        pageManager = resourceResolver.adaptTo(PageManagerDecorator.class);

        final Page page = pageManager.getContainingPage(resource);

        currentPage = pageManager.getPage(page);
        componentNode = resource.adaptTo(ComponentNode.class);
    }

    protected DefaultComponentServletRequest(final PageContext pageContext) {
        slingRequest = (SlingHttpServletRequest) pageContext.getAttribute(ATTR_SLING_REQUEST);
        slingResponse = (SlingHttpServletResponse) pageContext.getAttribute(ATTR_SLING_RESPONSE);
        resource = (Resource) pageContext.getAttribute(DEFAULT_RESOURCE_NAME);
        resourceResolver = resource.getResourceResolver();
        pageManager = resourceResolver.adaptTo(PageManagerDecorator.class);

        final Page page = (Page) pageContext.getAttribute(ATTR_CURRENT_PAGE);

        currentPage = pageManager.getPage(page);
        currentNode = (Node) pageContext.getAttribute(DEFAULT_NODE_NAME);
        properties = (ValueMap) pageContext.getAttribute(DEFAULT_PROPERTIES_NAME);
        componentNode = resource.adaptTo(ComponentNode.class);
    }

    @Override
    public ComponentNode getComponentNode() {
        return componentNode;
    }

    @Override
    public Node getCurrentNode() {
        return currentNode;
    }

    @Override
    public PageDecorator getCurrentPage() {
        return currentPage;
    }

    @Override
    public PageManagerDecorator getPageManager() {
        return pageManager;
    }

    @Override
    public ValueMap getProperties() {
        return properties;
    }

    @Override
    public Optional<String> getRequestParameter(final String parameterName) {
        checkNotNull(parameterName);

        return Optional.fromNullable(slingRequest.getRequestParameter(parameterName)).transform(
            REQUEST_PARAMETER_TO_STRING);
    }

    @Override
    public String getRequestParameter(final String parameterName, final String defaultValue) {
        checkNotNull(parameterName);
        checkNotNull(defaultValue);

        final RequestParameter parameter = slingRequest.getRequestParameter(parameterName);

        return parameter == null ? defaultValue : parameter.getString();
    }

    @Override
    public Optional<String[]> getRequestParameters(final String parameterName) {
        checkNotNull(parameterName);

        return Optional.fromNullable(slingRequest.getRequestParameters(parameterName)).transform(
            REQUEST_PARAMETERS_TO_STRING_ARRAY);
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    @Override
    public ResourceResolver getResourceResolver() {
        return resourceResolver;
    }

    @Override
    public String[] getSelectors() {
        return slingRequest.getRequestPathInfo().getSelectors();
    }

    @Override
    public Session getSession() {
        return resourceResolver.adaptTo(Session.class);
    }

    @Override
    public SlingHttpServletRequest getSlingRequest() {
        return slingRequest;
    }

    @Override
    public SlingHttpServletResponse getSlingResponse() {
        return slingResponse;
    }

    @Override
    public WCMMode getWCMMode() {
        return WCMMode.fromRequest(slingRequest);
    }
}
