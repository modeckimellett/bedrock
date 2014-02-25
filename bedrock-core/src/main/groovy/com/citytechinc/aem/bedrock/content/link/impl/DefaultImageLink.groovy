/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.content.link.impl

import com.citytechinc.aem.bedrock.content.link.ImageLink
import com.citytechinc.aem.bedrock.content.link.Link
import groovy.transform.Immutable

@Immutable(knownImmutableClasses = [Link])
class DefaultImageLink implements ImageLink {

    @Delegate Link link

    String imageSource
}
