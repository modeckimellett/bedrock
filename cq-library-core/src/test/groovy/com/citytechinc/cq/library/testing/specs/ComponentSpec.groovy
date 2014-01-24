/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.testing.specs

import com.citytechinc.cq.library.components.AbstractComponent
import com.citytechinc.cq.library.content.request.ComponentRequest

class ComponentSpec extends AbstractComponentSpec {

    class TestingComponent extends AbstractComponent {

        TestingComponent(ComponentRequest request) {
            super(request)
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

    def "testing component"() {
        setup:
        def request = getComponentRequestBuilder().build {
            path "/content/citytechinc/jcr:content/component"
        }

        def component = new TestingComponent(request)

        expect:
        component.title == "Testing Component"
    }
}
