/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.utils

import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.Resource
import spock.lang.Specification
import spock.lang.Unroll

class PathUtilsSpec extends Specification {

    @Unroll
    def "is external"() {
        expect:
        PathUtils.isExternal(path) == result

        where:
        path                      | result
        "http://www.google.com"   | true
        "http://"                 | true
        "notcontent"              | true
        "/content/global"         | false
        "/content/global/en"      | false
        "/content/global/en/test" | false
    }

    @Unroll
    def "get page path for string"() {
        expect:
        PathUtils.getPagePath(path) == result

        where:
        path                           | result
        ""                             | ""
        "/content/foo"                 | "/content/foo"
        "/content/foo/jcr:content"     | "/content/foo"
        "/content/foo/jcr:content/bar" | "/content/foo"
    }

    @Unroll
    def "get page path for request"() {
        setup:
        def request = Mock(SlingHttpServletRequest)
        def resource = Mock(Resource)

        resource.path >> path
        request.resource >> resource

        expect:
        PathUtils.getPagePath(request) == result

        where:
        path                           | result
        ""                             | ""
        "/content/foo"                 | "/content/foo"
        "/content/foo/jcr:content"     | "/content/foo"
        "/content/foo/jcr:content/bar" | "/content/foo"
    }
}