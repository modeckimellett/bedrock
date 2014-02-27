/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.testing.builders

import com.citytechinc.aem.bedrock.testing.specs.AbstractBedrockSpec
import com.day.cq.wcm.api.WCMMode

class ComponentRequestBuilderSpec extends AbstractBedrockSpec {

    def setupSpec() {
        pageBuilder.content {
            citytechinc("CITYTECH, Inc.") {
                "jcr:content"(pagePath: "/content/citytechinc", externalPath: "http://www.reddit.com")
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

    def "build request with mode"() {
        setup:
        def request = new ComponentRequestBuilder(resourceResolver).build {
            path "/content/citytechinc/jcr:content"
            mode WCMMode.DISABLED
        }

        expect:
        WCMMode.fromRequest(request.slingRequest) == WCMMode.DISABLED
    }
}
