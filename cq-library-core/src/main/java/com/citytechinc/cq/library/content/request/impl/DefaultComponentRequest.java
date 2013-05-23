/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.request.impl;

import com.citytechinc.cq.library.content.node.ComponentNode;
import com.citytechinc.cq.library.content.page.PageDecorator;
import com.citytechinc.cq.library.content.page.PageManagerDecorator;
import com.citytechinc.cq.library.content.request.ComponentRequest;
import com.citytechinc.cq.library.content.request.ComponentServletRequest;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.components.EditContext;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;
import com.google.common.base.Optional;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.jsp.PageContext;

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_COMPONENT_CONTEXT_NAME;
import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_COMPONENT_NAME;
import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_CURRENT_DESIGN_NAME;
import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_CURRENT_STYLE_NAME;
import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_DESIGNER_NAME;
import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_EDIT_CONTEXT_NAME;

public final class DefaultComponentRequest implements ComponentRequest {

    private final Component component;

    private final ComponentContext componentContext;

    private final Design currentDesign;

    private final Style currentStyle;

    private final Designer designer;

    private final EditContext editContext;

    private final ComponentServletRequest componentServletRequest;

    /**
     * Create a new request from the current JSP page context.
     *
     * @param pageContext page context
     */
    public DefaultComponentRequest(final PageContext pageContext) {
        component = (Component) pageContext.getAttribute(DEFAULT_COMPONENT_NAME);
        componentContext = (ComponentContext) pageContext.getAttribute(DEFAULT_COMPONENT_CONTEXT_NAME);
        editContext = (EditContext) pageContext.getAttribute(DEFAULT_EDIT_CONTEXT_NAME);
        designer = (Designer) pageContext.getAttribute(DEFAULT_DESIGNER_NAME);
        currentDesign = (Design) pageContext.getAttribute(DEFAULT_CURRENT_DESIGN_NAME);
        currentStyle = (Style) pageContext.getAttribute(DEFAULT_CURRENT_STYLE_NAME);

        componentServletRequest = new DefaultComponentServletRequest(pageContext);
    }

    /**
     * Create a new request from the current JSP page context.
     *
     * @param pageContext page context
     * @param resourceResolver resource resolver, use if differing from the session/context authorization level
     */
    public DefaultComponentRequest(final PageContext pageContext, final ResourceResolver resourceResolver) {
        component = (Component) pageContext.getAttribute(DEFAULT_COMPONENT_NAME);
        componentContext = (ComponentContext) pageContext.getAttribute(DEFAULT_COMPONENT_CONTEXT_NAME);
        editContext = (EditContext) pageContext.getAttribute(DEFAULT_EDIT_CONTEXT_NAME);
        designer = (Designer) pageContext.getAttribute(DEFAULT_DESIGNER_NAME);
        currentDesign = (Design) pageContext.getAttribute(DEFAULT_CURRENT_DESIGN_NAME);
        currentStyle = (Style) pageContext.getAttribute(DEFAULT_CURRENT_STYLE_NAME);

        componentServletRequest = new DefaultComponentServletRequest(pageContext, resourceResolver);
    }

    @Override
    public Component getComponent() {
        return component;
    }

    @Override
    public ComponentContext getComponentContext() {
        return componentContext;
    }

    @Override
    public ComponentNode getComponentNode() {
        return componentServletRequest.getComponentNode();
    }

    @Override
    public Design getCurrentDesign() {
        return currentDesign;
    }

    @Override
    public Node getCurrentNode() {
        return componentServletRequest.getCurrentNode();
    }

    @Override
    public PageDecorator getCurrentPage() {
        return componentServletRequest.getCurrentPage();
    }

    @Override
    public Style getCurrentStyle() {
        return currentStyle;
    }

    @Override
    public Designer getDesigner() {
        return designer;
    }

    @Override
    public EditContext getEditContext() {
        return editContext;
    }

    @Override
    public PageManagerDecorator getPageManager() {
        return componentServletRequest.getPageManager();
    }

    @Override
    public ValueMap getProperties() {
        return componentServletRequest.getProperties();
    }

    @Override
    public Optional<String> getRequestParameter(final String parameterName) {
        return componentServletRequest.getRequestParameter(parameterName);
    }

    @Override
    public Optional<String[]> getRequestParameters(final String parameterName) {
        return componentServletRequest.getRequestParameters(parameterName);
    }

    @Override
    public String getRequestParameter(final String parameterName, final String defaultValue) {
        return componentServletRequest.getRequestParameter(parameterName, defaultValue);
    }

    @Override
    public Resource getResource() {
        return componentServletRequest.getResource();
    }

    @Override
    public ResourceResolver getResourceResolver() {
        return componentServletRequest.getResourceResolver();
    }

    @Override
    public String[] getSelectors() {
        return componentServletRequest.getSelectors();
    }

    @Override
    public Session getSession() {
        return componentServletRequest.getSession();
    }

    @Override
    public SlingHttpServletRequest getSlingRequest() {
        return componentServletRequest.getSlingRequest();
    }

    @Override
    public SlingHttpServletResponse getSlingResponse() {
        return componentServletRequest.getSlingResponse();
    }

    @Override
    public WCMMode getWCMMode() {
        return componentServletRequest.getWCMMode();
    }
}
