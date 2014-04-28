package com.citytechinc.aem.bedrock.core.specs

import com.citytechinc.aem.bedrock.core.components.TestComponent

class ComponentSpecSpec extends ComponentSpec {

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