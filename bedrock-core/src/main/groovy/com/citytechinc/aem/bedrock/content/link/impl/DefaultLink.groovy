/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.content.link.impl

import com.citytechinc.aem.bedrock.content.link.Link
import groovy.transform.Immutable

@Immutable
class DefaultLink implements Link {

    String path

    String extension

    String suffix

    String href

    List<String> selectors

    String queryString

    boolean external

    String target

    String title

    Map<String, String> properties

    @Override
    boolean isEmpty() {
        !href
    }
}
