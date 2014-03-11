/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.components

import com.citytechinc.aem.bedrock.content.node.ComponentNode
import com.citytechinc.aem.bedrock.content.request.ComponentRequest
import com.citytechinc.aem.bedrock.testing.specs.ComponentSpec

class AbstractComponentSpec extends ComponentSpec {

    class TestingComponent extends AbstractComponent {

        TestingComponent(ComponentRequest request) {
            super(request)
        }

        TestingComponent(ComponentNode componentNode) {
            super(componentNode)
        }

        def getTitle() {
            get("jcr:title", "")
        }
    }

    def setupSpec() {
        pageBuilder.content {
            citytechinc("CITYTECH, Inc.") {
                "jcr:content" {
                    component("jcr:title": "Testing Component", pagePath: "/content/citytechinc",
                        externalPath: "http://www.reddit.com")
                }
            }
        }
    }

    def "instantiate component with component request"() {
        setup:
        def request = getComponentRequestBuilder("/content/citytechinc/jcr:content/component").build()
        def component = new TestingComponent(request)

        expect:
        component.title == "Testing Component"
        component.currentPage.path == "/content/citytechinc"
    }

    def "instantiate component with component node"() {
        setup:
        def componentNode = getComponentNode("/content/citytechinc/jcr:content/component")
        def component = new TestingComponent(componentNode)

        expect:
        component.title == "Testing Component"
        component.currentPage.path == "/content/citytechinc"
    }

    def "when instantiating component with component node, attempting to get a service throws exception"() {
        setup:
        def componentNode = getComponentNode("/content/citytechinc/jcr:content/component")
        def component = new TestingComponent(componentNode)

        when:
        component.getService(null)

        then:
        thrown(NullPointerException)

        when:
        component.getServices(null, null)

        then:
        thrown(NullPointerException)
    }
}
