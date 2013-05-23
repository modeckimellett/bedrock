/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.link;

import java.util.List;

public interface NavigationLink extends Link {

    /**
     * List of child links. Useful for building navigation hierarchies.
     *
     * @return list of child links for this link
     */
    List<NavigationLink> getChildren();

    /**
     * @return true if link is active
     */
    boolean isActive();
}
