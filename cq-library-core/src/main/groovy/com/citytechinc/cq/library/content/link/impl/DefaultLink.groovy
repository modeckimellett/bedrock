/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.link.impl

import com.citytechinc.cq.library.content.link.Link
import groovy.transform.Immutable

@Immutable
class DefaultLink implements Link {

    String path

    String extension

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
