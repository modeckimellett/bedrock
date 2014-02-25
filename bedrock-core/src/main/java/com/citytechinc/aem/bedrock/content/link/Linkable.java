/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.content.link;

import com.citytechinc.aem.bedrock.content.link.builders.LinkBuilder;

/**
 * Definition for "linkable" items, such as pages and components (i.e. path-addressable resources).
 */
public interface Linkable {

    /**
     * Get the URL for this item.
     *
     * @return href
     */
    String getHref();

    /**
     * Get the mapped URL for this item.
     *
     * @param mapped if true, the path will be routed through the Resource Resolver to determine the mapped path (e.g.
     * without leading "/content").
     * @return mapped href
     */
    String getHref(boolean mapped);

    /**
     * Get a link for this item.
     *
     * @return link
     */
    Link getLink();

    /**
     * Get a link for this item.
     *
     * @param mapped if true, the <code>Link</code> path will be routed through the Resource Resolver to determine the
     * mapped path (e.g. without leading "/content").
     * @return mapped link
     */
    Link getLink(boolean mapped);

    /**
     * @return builder instance for this item
     */
    LinkBuilder getLinkBuilder();

    /**
     * @param mapped if true, the <code>LinkBuilder</code> for this resource will be routed through the Resource
     * Resolver to determine the mapped path (e.g. without leading "/content").
     * @return builder instance for this item containing the mapped link
     */
    LinkBuilder getLinkBuilder(boolean mapped);

}
