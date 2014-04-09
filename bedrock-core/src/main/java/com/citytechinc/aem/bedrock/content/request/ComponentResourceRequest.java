/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.content.request;

import com.citytechinc.aem.bedrock.content.node.ComponentNode;
import com.citytechinc.aem.bedrock.content.page.PageDecorator;
import com.citytechinc.aem.bedrock.content.page.PageManagerDecorator;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import javax.jcr.Node;
import javax.jcr.Session;

/**
 * Facade for <code>Resource</code>-related request objects.
 */
public interface ComponentResourceRequest {

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
     * @return resource
     */
    Resource getResource();

    /**
     * @return resource resolver
     */
    ResourceResolver getResourceResolver();

    /**
     * @return JCR session bound to this request
     */
    Session getSession();
}
