/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.link;

import com.citytechinc.cq.library.content.link.builders.LinkBuilder;

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
     * Get a link for this item.
     *
     * @return link object
     */
    Link getLink();

    /**
     * @return builder instance for this item
     */
    LinkBuilder getLinkBuilder();

    /**
     * Get the mapped URL for this item.
     * <p/>
     * "Mapped" paths have been routed through the Sling Resource Resolver to obtain the internally mapped path from the
     * resolver configuration (e.g. leading "/content" has been removed).
     *
     * @return mapped href
     */
    String getMappedHref();

    /**
     * Get the mapped link for this item.
     *
     * @return mapped link
     */
    Link getMappedLink();

    /**
     * @return builder instance for this item containing the mapped link
     */
    LinkBuilder getMappedLinkBuilder();

}
