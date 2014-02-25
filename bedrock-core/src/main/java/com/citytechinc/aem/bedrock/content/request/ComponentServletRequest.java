/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.content.request;

import com.citytechinc.aem.bedrock.content.node.ComponentNode;
import com.citytechinc.aem.bedrock.content.page.PageDecorator;
import com.citytechinc.aem.bedrock.content.page.PageManagerDecorator;
import com.day.cq.wcm.api.WCMMode;
import com.google.common.base.Optional;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import javax.jcr.Node;
import javax.jcr.Session;

/**
 * Request facade for use in CQ servlets and component beans.
 */
public interface ComponentServletRequest {

    /**
     * @return component node for the current resource
     */
    ComponentNode getComponentNode();

    /**
     * @return current JCR node
     */
    Node getCurrentNode();

    /**
     * @return current CQ page
     */
    PageDecorator getCurrentPage();

    /**
     * @return page manager bound to the current request
     */
    PageManagerDecorator getPageManager();

    /**
     * @return property map
     */
    ValueMap getProperties();

    /**
     * Retrieve a parameter from the request or return an absent <code>Optional</code> if it does not exist.
     *
     * @param parameterName request parameter name
     * @return Optional parameter value
     */
    Optional<String> getRequestParameter(String parameterName);

    /**
     * Retrieve parameters from the request or return an absent <code>Optional</code> if they do not exist.
     *
     * @param parameterName request parameter name
     * @return Optional parameter values
     */
    Optional<String[]> getRequestParameters(String parameterName);

    /**
     * Retrieve a parameter value from the request or return a default value if the parameter does not exist. If the
     * parameter does not exist and the default value is null, the empty string is returned.
     *
     * @param parameterName request parameter name
     * @param defaultValue String of the default value to return if the parameter does not exist.
     * @return String of the parameter or the default value if it doesn't exist. If the default value is null, return
     *         the empty string.
     */
    String getRequestParameter(String parameterName, String defaultValue);

    /**
     * @return resource
     */
    Resource getResource();

    /**
     * @return resource resolver
     */
    ResourceResolver getResourceResolver();

    /**
     * @return Sling request selectors or empty array if the request has no selectors
     */
    String[] getSelectors();

    /**
     * @return JCR session bound to this request
     */
    Session getSession();

    /**
     * @return Sling servlet request
     */
    SlingHttpServletRequest getSlingRequest();

    /**
     * @return Sling servlet response
     */
    SlingHttpServletResponse getSlingResponse();

    /**
     * @return current WCM mode for this request
     */
    WCMMode getWCMMode();
}
