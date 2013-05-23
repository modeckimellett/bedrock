/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.link;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Link with associated attributes.
 */
public interface Link extends Serializable {

    String getExtension();

    /**
     * @return href
     */
    String getHref();

    String getPath();

    /**
     * @return property map
     */
    Map<String, String> getProperties();

    String getQueryString();

    List<String> getSelectors();

    /**
     * @return link target
     */
    String getTarget();

    /**
     * @return link title
     */
    String getTitle();

    /**
     * @return true if href is null or empty
     */
    boolean isEmpty();

    /**
     * @return if href is to an external URL
     */
    boolean isExternal();
}
