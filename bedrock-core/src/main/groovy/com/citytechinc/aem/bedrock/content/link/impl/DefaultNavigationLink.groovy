/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.content.link.impl

import com.citytechinc.aem.bedrock.content.link.Link
import com.citytechinc.aem.bedrock.content.link.NavigationLink
import groovy.transform.Immutable

@Immutable(knownImmutableClasses = [Link])
class DefaultNavigationLink implements NavigationLink {

    @Delegate Link link

    boolean active

    List<NavigationLink> children
}
