/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.testing.builders

import com.citytechinc.cq.library.testing.specs.AbstractCqSpec

class ComponentRequestBuilderSpec extends AbstractCqSpec {

    def setupSpec() {
        pageBuilder.content {
            citytechinc("CITYTECH, Inc.") {
                "jcr:content"(pagePath: "/content/citytechinc", externalPath: "http://www.reddit.com") {

                }
            }
        }
    }

    def "build request"() {
        setup:
        def request = new ComponentRequestBuilder(resourceResolver).build {
            path "/content/citytechinc/jcr:content"
        }

        expect:
        request.resource.path == "/content/citytechinc/jcr:content"
        request.currentNode.path == "/content/citytechinc/jcr:content"
        request.currentPage.path == "/content/citytechinc"
        request.pageManager
        request.componentNode
        request.properties
        request.session
        request.slingRequest
        request.slingResponse
        !request.selectors
    }
}
