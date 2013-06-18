/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.link.impl

import com.citytechinc.cq.library.content.link.ImageLink
import com.citytechinc.cq.library.content.link.Link
import groovy.transform.Immutable

@Immutable(knownImmutableClasses = [Link])
class DefaultImageLink implements ImageLink {

    @Delegate Link link

    String imageSrc
}
