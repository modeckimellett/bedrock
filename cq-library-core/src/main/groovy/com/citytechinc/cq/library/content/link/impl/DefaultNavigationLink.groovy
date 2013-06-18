/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.link.impl

import com.citytechinc.cq.library.content.link.Link
import com.citytechinc.cq.library.content.link.NavigationLink
import groovy.transform.Immutable

@Immutable(knownImmutableClasses = [Link])
class DefaultNavigationLink implements NavigationLink {

    @Delegate Link link

    boolean active

    List<NavigationLink> children
}
