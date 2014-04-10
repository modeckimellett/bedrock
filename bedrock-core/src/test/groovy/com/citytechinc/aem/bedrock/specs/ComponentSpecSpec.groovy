/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.specs

import com.citytechinc.aem.bedrock.components.AbstractComponent
import com.citytechinc.aem.bedrock.content.request.ComponentRequest

class ComponentSpecSpec extends ComponentSpec {

    class TestComponent extends AbstractComponent {

        @Override
        void init(ComponentRequest request) {

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

    def "testing sightly component"() {
        setup:
        def component = init(TestComponent) {
            path = "/content/citytechinc/jcr:content/component"
        }

        expect:
        component.title == "Testing Component"
    }
}